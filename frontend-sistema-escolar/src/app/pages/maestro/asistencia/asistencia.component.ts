import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Location } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AsistenciaService, Asistencia } from '../../../services/asistencia.service';
import { NominaService, NominaAlumno } from '../../../services/nomina.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { RestConstants } from '../../../shared/rest-api/rest-constants';

@Component({
  selector: 'app-asistencia',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './asistencia.component.html'
})
export class AsistenciaComponent implements OnInit {
  alumnos: NominaAlumno[] = [];
  misAsignaciones: any[] = [];
  asistenciaForm!: FormGroup;

  cargando: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  private restConstants = new RestConstants();

  constructor(
    private fb: FormBuilder,
    private asistenciaService: AsistenciaService,
    private nominaService: NominaService,
    private http: HttpClient,
    private location: Location,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarMisAsignaciones();
  }

  private initForm(): void {
    const hoy = new Date().toISOString().substring(0, 10);
    this.asistenciaForm = this.fb.group({
      fecha: [hoy, [Validators.required]],
      idAsignacion: [null, [Validators.required]],
      anio: [new Date().getFullYear(), [Validators.required]]
    });
  }

  cargarMisAsignaciones(): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    this.http.get<any[]>(`${this.restConstants.getApiURL()}/asistencias/mis-asignaciones`, { headers, withCredentials: true })
      .subscribe({
        next: (data) => {
          this.ngZone.run(() => {
            this.misAsignaciones = data || [];
            this.cdr.detectChanges();
          });
        },
        error: (err) => {
          console.error('Error al cargar asignaciones del docente:', err);
        }
      });
  }

  cargarAlumnos(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.asistenciaForm.invalid) {
      this.asistenciaForm.markAllAsTouched();
      this.errorMessage = 'Por favor seleccione una asignación y fecha válidas.';
      return;
    }

    this.cargando = true;
    const formVal = this.asistenciaForm.value;

    // Buscamos el objeto AsignacionDocente mapeado
    const asigSeleccionada = this.misAsignaciones.find(a => a.idAsignacion === Number(formVal.idAsignacion));
    
    // Extraemos los IDs desde la propiedad "seccion" del objeto Java
    const idGrado = asigSeleccionada?.seccion?.idGrado;
    const idSeccion = asigSeleccionada?.seccion?.idSeccion;

    this.nominaService.obtenerNomina(formVal.anio, idGrado, idSeccion).subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.alumnos = data || [];
          if (this.alumnos.length === 0) {
            this.successMessage = 'No se encontraron alumnos para este grado y sección.';
          }
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al obtener alumnos.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  guardarAsistenciaIndividual(alumno: NominaAlumno, estado: 'PRESENTE' | 'AUSENTE' | 'TARDE' | 'JUSTIFICADO', obs: string = ''): void {
    const formVal = this.asistenciaForm.value;

    const payload: Asistencia = {
      idInscripcion: alumno.idInscripcion,
      idAsignacion: Number(formVal.idAsignacion),
      fecha: formVal.fecha,
      estado: estado,
      observacion: obs
    };

    this.asistenciaService.registrarAsistencia(payload).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || `Asistencia registrada correctamente.`;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al guardar la asistencia.';
          this.cdr.detectChanges();
        });
      }
    });
  }

  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/maestro/dashboard']);
    }
  }
}