import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Location } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ActividadService, Actividad } from '../../../services/actividad.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { RestConstants } from '../../../shared/rest-api/rest-constants';

@Component({
  selector: 'app-ponderacion',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './actividad.component.html'
})
export class ActicidadComponent implements OnInit {
  misAsignaciones: any[] = [];
  actividades: Actividad[] = [];
  
  filtroForm!: FormGroup;
  actividadForm!: FormGroup;

  cargando: boolean = false;
  guardando: boolean = false;
  modoEdicion: boolean = false;
  actividadEditarId: number | null = null;

  totalPorcentaje: number = 0;
  successMessage: string = '';
  errorMessage: string = '';

  private restConstants = new RestConstants();

  constructor(
    private fb: FormBuilder,
    private actividadService: ActividadService,
    private http: HttpClient,
    private location: Location,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForms();
    this.cargarMisAsignaciones();
  }

  private initForms(): void {
    this.filtroForm = this.fb.group({
      idAsignacion: [null, [Validators.required]]
    });

    const hoy = new Date().toISOString().substring(0, 10);
    this.actividadForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(150)]],
      descripcion: ['', [Validators.maxLength(255)]],
      porcentaje: [null, [Validators.required, Validators.min(1), Validators.max(100)]],
      fecha: [hoy, [Validators.required]],
      estado: [true]
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
        error: (err) => console.error('Error al cargar asignaciones:', err)
      });
  }

  cargarActividades(): void {
    this.limpiarMensajes();
    const idAsignacion = this.filtroForm.get('idAsignacion')?.value;

    if (!idAsignacion) {
      this.actividades = [];
      this.totalPorcentaje = 0;
      return;
    }

    this.cargando = true;
    this.actividadService.obtenerPorAsignacion(Number(idAsignacion)).subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.actividades = data || [];
          this.calcularTotalPorcentaje();
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al obtener actividades.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  calcularTotalPorcentaje(): void {
    this.totalPorcentaje = this.actividades
      .filter(a => a.estado)
      .reduce((sum, a) => sum + Number(a.porcentaje), 0);
  }

  guardarActividad(): void {
    this.limpiarMensajes();
    const idAsignacion = this.filtroForm.get('idAsignacion')?.value;

    if (!idAsignacion) {
      this.errorMessage = 'Debe seleccionar una asignación primero.';
      return;
    }

    if (this.actividadForm.invalid) {
      this.actividadForm.markAllAsTouched();
      this.errorMessage = 'Complete correctamente todos los campos obligatorios.';
      return;
    }

    this.guardando = true;
    const formVal = this.actividadForm.value;

    const payload: Actividad = {
      idAsignacion: Number(idAsignacion),
      nombre: formVal.nombre,
      descripcion: formVal.descripcion,
      porcentaje: Number(formVal.porcentaje),
      fecha: formVal.fecha,
      estado: formVal.estado
    };

    if (this.modoEdicion && this.actividadEditarId) {
      this.actividadService.actualizar(this.actividadEditarId, payload).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = res?.mensaje || 'Actividad actualizada correctamente.';
            this.cancelarEdicion();
            this.cargarActividades();
            this.guardando = false;
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.errorMessage = err?.error?.mensaje || 'Error al actualizar actividad.';
            this.guardando = false;
            this.cdr.detectChanges();
          });
        }
      });
    } else {
      this.actividadService.registrar(payload).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = res?.mensaje || 'Actividad registrada correctamente.';
            this.actividadForm.reset({
              fecha: new Date().toISOString().substring(0, 10),
              estado: true
            });
            this.cargarActividades();
            this.guardando = false;
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.errorMessage = err?.error?.mensaje || 'Error al registrar actividad.';
            this.guardando = false;
            this.cdr.detectChanges();
          });
        }
      });
    }
  }

  prepararEdicion(act: Actividad): void {
    this.modoEdicion = true;
    this.actividadEditarId = act.idActividad || null;
    this.actividadForm.patchValue({
      nombre: act.nombre,
      descripcion: act.descripcion,
      porcentaje: act.porcentaje,
      fecha: act.fecha,
      estado: act.estado
    });
  }

  cancelarEdicion(): void {
    this.modoEdicion = false;
    this.actividadEditarId = null;
    this.actividadForm.reset({
      fecha: new Date().toISOString().substring(0, 10),
      estado: true
    });
  }

  toggleEstado(act: Actividad): void {
    if (!act.idActividad) return;
    const nuevoEstado = !act.estado;

    this.actividadService.cambiarEstado(act.idActividad, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || 'Estado de actividad modificado.';
          this.cargarActividades();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al cambiar el estado.';
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

  private limpiarMensajes(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}