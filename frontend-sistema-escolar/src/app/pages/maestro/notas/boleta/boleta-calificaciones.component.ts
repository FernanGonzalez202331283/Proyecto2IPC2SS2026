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
  idActividadPrincipal: number;
  idNota?: number;
  calificacion: number | null;
}

@Component({
  selector: 'app-boleta-calificaciones',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './boleta-calificaciones.component.html',
  styles: [`
    .btn-regresar {
      display: inline-flex !important;
      align-items: center;
      white-space: nowrap;
    }
  `]
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
    private router: Router,
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
        this.cargarCursoSeleccionado();
      }
    });
  }

  cargarCursoSeleccionado(): void {
    this.cargando = true;
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    this.http.get<any[]>(`${this.restConstants.getApiURL()}/asistencias/mis-asignaciones`, { headers, withCredentials: true })
      .subscribe({
        next: (misAsignaciones) => {
          // Filtrar únicamente el curso/asignación seleccionada por referencia
          const asigSeleccionada = misAsignaciones.find(a => a.idAsignacion === this.idAsignacionRef);

          if (!asigSeleccionada) {
            this.errorMessage = 'No se encontró el curso seleccionado.';
            this.cargando = false;
            this.cdr.detectChanges();
            return;
          }

          this.notaService.obtenerPorEstudiante(this.idInscripcion).subscribe({
            next: (notasAlumno) => {
              this.construirFilaCurso(asigSeleccionada, notasAlumno || []);
            },
            error: () => {
              this.construirFilaCurso(asigSeleccionada, []);
            }
          });
        },
        error: () => {
          this.errorMessage = 'Error al cargar los datos del curso.';
          this.cargando = false;
          this.cdr.detectChanges();
        }
      });
  }

  private construirFilaCurso(asig: any, notasAlumno: Nota[]): void {
    this.boletaCursos = [];

    this.actividadService.obtenerPorAsignacion(asig.idAsignacion).subscribe({
      next: (actividades) => {
        const actPrincipal = (actividades || []).find(a => a.estado) || actividades[0];

        if (actPrincipal) {
          const mapaNotas = new Map<number, Nota>();
          notasAlumno.forEach(n => mapaNotas.set(n.idActividad, n));

          const notaExistente = mapaNotas.get(actPrincipal.idActividad!);

          this.boletaCursos.push({
            idAsignacion: asig.idAsignacion,
            nombreCurso: asig.curso?.nombre || 'Curso Seleccionado',
            idActividadPrincipal: actPrincipal.idActividad!,
            idNota: notaExistente?.idNota,
            calificacion: notaExistente ? notaExistente.calificacion : null
          });
        } else {
          this.errorMessage = 'No hay una actividad o zona configurada para este curso todavía.';
        }

        this.ngZone.run(() => {
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.errorMessage = 'Error al cargar actividades del curso.';
        this.ngZone.run(() => {
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
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
      this.errorMessage = 'Debe ingresar una calificación válida entre 0 y 100.';
      return;
    }

    this.guardando = true;
    this.notaService.guardarMasivo(payload).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || 'Calificación guardada correctamente.';
          this.guardando = false;
          this.cargarCursoSeleccionado();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al guardar la calificación.';
          this.guardando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  volver(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/maestro/dashboard']);
    }
  }
}