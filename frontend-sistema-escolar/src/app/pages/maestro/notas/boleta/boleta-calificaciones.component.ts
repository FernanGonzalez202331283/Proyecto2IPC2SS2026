import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NotaService, Nota } from '../../../../services/nota.service';
import { ActividadService } from '../../../../services/actividad.service';
import { RestConstants } from '../../../../shared/rest-api/rest-constants';

interface RenglonNotaCurso {
  idAsignacion: number;
  nombreCurso: string;
  idActividadPrincipal: number; // Actividad principal o global del curso
  idNota?: number;
  calificacion: number | null;
}

@Component({
  selector: 'app-boleta-calificaciones',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './boleta-calificaciones.component.html'
})
export class BoletaCalificacionesComponent implements OnInit {
  idInscripcion!: number;
  idAsignacionRef!: number;
  
  boletaCursos: RenglonNotaCurso[] = [];

  cargando: boolean = false;
  guardando: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  private restConstants = new RestConstants();

  constructor(
    private route: ActivatedRoute,
    private notaService: NotaService,
    private actividadService: ActividadService,
    private http: HttpClient,
    private location: Location,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.idInscripcion = Number(params['idInscripcion']);
    });

    this.route.queryParams.subscribe(queryParams => {
      this.idAsignacionRef = Number(queryParams['idAsignacionRef']);
      if (this.idInscripcion && this.idAsignacionRef) {
        this.cargarBoletaPorCursos();
      }
    });
  }

  cargarBoletaPorCursos(): void {
    this.cargando = true;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    // 1. Obtener todas las asignaciones del docente para identificar los cursos del grado
    this.http.get<any[]>(`${this.restConstants.getApiURL()}/asistencias/mis-asignaciones`, { headers, withCredentials: true })
      .subscribe({
        next: (misAsignaciones) => {
          const asigRef = misAsignaciones.find(a => a.idAsignacion === this.idAsignacionRef);
          const idSeccionTarget = asigRef?.seccion?.idSeccion || asigRef?.idSeccion;

          // Asignaciones/Cursos pertenecientes a la misma sección
          const asignacionesCursos = misAsignaciones.filter(a => {
            const idSec = a.seccion?.idSeccion || a.idSeccion;
            return idSec === idSeccionTarget;
          });

          // 2. Obtener las notas actuales del estudiante
          this.notaService.obtenerPorEstudiante(this.idInscripcion).subscribe({
            next: (notasAlumno) => {
              this.construirBoletaCursos(asignacionesCursos, notasAlumno || []);
            },
            error: () => {
              this.construirBoletaCursos(asignacionesCursos, []);
            }
          });
        },
        error: () => {
          this.cargando = false;
        }
      });
  }

  private construirBoletaCursos(asignacionesCursos: any[], notasAlumno: Nota[]): void {
    const mapaNotas = new Map<number, Nota>();
    notasAlumno.forEach(n => mapaNotas.set(n.idActividad, n));

    let pendientes = asignacionesCursos.length;
    this.boletaCursos = [];

    if (pendientes === 0) {
      this.ngZone.run(() => {
        this.cargando = false;
        this.cdr.detectChanges();
      });
      return;
    }

    asignacionesCursos.forEach(asig => {
      // Obtenemos la actividad de la asignación para asociar la nota
      this.actividadService.obtenerPorAsignacion(asig.idAsignacion).subscribe({
        next: (actividades) => {
          const actPrincipal = (actividades || []).find(a => a.estado) || actividades[0];

          if (actPrincipal) {
            const notaExistente = mapaNotas.get(actPrincipal.idActividad!);
            
            this.boletaCursos.push({
              idAsignacion: asig.idAsignacion,
              nombreCurso: asig.curso?.nombre || 'Curso',
              idActividadPrincipal: actPrincipal.idActividad!,
              idNota: notaExistente?.idNota,
              calificacion: notaExistente ? notaExistente.calificacion : null
            });
          }

          pendientes--;
          if (pendientes === 0) {
            this.ngZone.run(() => {
              this.cargando = false;
              this.cdr.detectChanges();
            });
          }
        },
        error: () => {
          pendientes--;
          if (pendientes === 0) {
            this.ngZone.run(() => {
              this.cargando = false;
              this.cdr.detectChanges();
            });
          }
        }
      });
    });
  }

  guardarNotasBoleta(): void {
    const payload: Nota[] = this.boletaCursos
      .filter(item => item.calificacion !== null && item.calificacion >= 0 && item.calificacion <= 100)
      .map(item => ({
        idNota: item.idNota,
        idActividad: item.idActividadPrincipal,
        idInscripcion: this.idInscripcion,
        calificacion: Number(item.calificacion)
      }));

    if (payload.length === 0) {
      this.errorMessage = 'Debe ingresar al menos una calificación válida de curso.';
      return;
    }

    this.guardando = true;
    this.notaService.guardarMasivo(payload).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || 'Notas finales por curso guardadas correctamente.';
          this.guardando = false;
          this.cargarBoletaPorCursos();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al guardar calificaciones de la boleta.';
          this.guardando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  volver(): void {
    this.location.back();
  }
}