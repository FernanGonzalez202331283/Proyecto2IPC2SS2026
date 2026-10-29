import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Location } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { AsignacionDocenteService, AsignacionDocente } from '../../../services/asignacion-docente.service';
import { EmpleadoService, Empleado } from '../../../services/empleado.service';
import { SeccionService, Seccion } from '../../../services/seccion.service';
import { GradoService, Grado } from '../../../services/grado.service';
import { CursoService, Curso } from '../../../services/curso.services';

@Component({
  selector: 'app-asignacion-docente',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './asignacion-docente.component.html'
})
export class AsignacionDocenteComponent implements OnInit {
  asignaciones: AsignacionDocente[] = [];
  maestrosLista: Empleado[] = [];
  cursosLista: Curso[] = [];
  seccionesLista: Seccion[] = [];
  gradosLista: Grado[] = [];

  asignacionForm!: FormGroup;
  modoEdicion: boolean = false;
  idSeleccionado: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private asignacionService: AsignacionDocenteService,
    private empleadoService: EmpleadoService,
    private cursoService: CursoService,
    private seccionService: SeccionService,
    private gradoService: GradoService,
    private location: Location,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarCatalogos();
    this.cargarAsignaciones();
  }

  private initForm(): void {
    this.asignacionForm = this.fb.group({
      idEmpleado: [null, [Validators.required]],
      idCurso: [null, [Validators.required]],
      idSeccion: [null, [Validators.required]]
    });
  }

  esCampoInvalido(controlName: string): boolean {
    const control = this.asignacionForm.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarCatalogos(): void {
    this.empleadoService.obtenerEmpleados().subscribe({
      next: (data) => {
        this.maestrosLista = (data || []).filter(e => e.estado !== false);
      },
      error: (err) => console.error('Error al cargar empleados:', err)
    });

    this.cursoService.obtenerCursos().subscribe({
      next: (data) => {
        this.cursosLista = (data || []).filter(c => c.estado !== false);
      },
      error: (err) => console.error('Error al cargar cursos:', err)
    });

    this.seccionService.obtenerSecciones().subscribe({
      next: (data) => {
        this.seccionesLista = (data || []).filter(s => s.estado !== false);
      },
      error: (err) => console.error('Error al cargar secciones:', err)
    });

    this.gradoService.obtenerGrados().subscribe({
      next: (data) => { this.gradosLista = data || []; },
      error: (err) => console.error('Error al cargar grados:', err)
    });
  }

  cargarAsignaciones(): void {
    this.cargando = true;
    this.asignacionService.obtenerAsignaciones().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.asignaciones = data || [];
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  guardarAsignacion(): void {
    this.limpiarMensajes();

    if (this.asignacionForm.invalid) {
      this.asignacionForm.markAllAsTouched();
      this.errorMessage = 'Seleccione todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    const formVal = this.asignacionForm.value;
    const idCursoNum = Number(formVal.idCurso);
    const idSeccionNum = Number(formVal.idSeccion);

    // Validar localmente que el curso no esté ya asignado a esta sección
    const yaExiste = this.asignaciones.some(asig => 
      asig.estado &&
      asig.curso?.idCurso === idCursoNum &&
      asig.seccion?.idSeccion === idSeccionNum &&
      (!this.modoEdicion || asig.idAsignacion !== this.idSeleccionado)
    );

    if (yaExiste) {
      this.errorMessage = 'El curso seleccionado ya se encuentra asignado a esta sección.';
      return;
    }

    this.procesando = true;

    const payload: AsignacionDocente = {
      empleado: { idEmpleado: Number(formVal.idEmpleado) },
      curso: { idCurso: idCursoNum },
      seccion: { idSeccion: idSeccionNum }
    };

    if (this.modoEdicion && this.idSeleccionado) {
      payload.idAsignacion = this.idSeleccionado;

      this.asignacionService.actualizarAsignacion(this.idSeleccionado, payload).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Asignación docente actualizada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarAsignaciones();
            this.cdr.detectChanges();
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.procesando = false;
            this.cdr.detectChanges();
          });
        }
      });
    } else {
      this.asignacionService.registrarAsignacion(payload).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Asignación docente registrada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarAsignaciones();
            this.cdr.detectChanges();
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.procesando = false;
            this.cdr.detectChanges();
          });
        }
      });
    }
  }

  editarAsignacion(item: AsignacionDocente): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = item.idAsignacion || null;

    this.asignacionForm.patchValue({
      idEmpleado: item.empleado?.idEmpleado || null,
      idCurso: item.curso?.idCurso || null,
      idSeccion: item.seccion?.idSeccion || null
    });
  }

  cambiarEstadoAsignacion(item: AsignacionDocente, nuevoEstado: boolean): void {
    if (!item.idAsignacion) return;

    this.limpiarMensajes();

    this.asignacionService.cambiarEstado(item.idAsignacion, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado de asignación actualizado.';
          item.estado = nuevoEstado;
          this.cargarAsignaciones();
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cdr.detectChanges();
        });
      }
    });
  }

  obtenerDetalleSeccion(sec?: Seccion | null): string {
    if (!sec) return 'Sin sección';
    const idGradoBusqueda = sec.idGrado;
    const grado = this.gradosLista.find(g => Number(g.idGrado) === Number(idGradoBusqueda));
    const nombreGrado = grado ? grado.nombre : '';
    return nombreGrado ? `${nombreGrado} - Sección ${sec.nombre || ''}` : `Sección ${sec.nombre || ''}`;
  }

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/admin/dashboard']);
    }
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idSeleccionado = null;
    this.asignacionForm.reset({
      idEmpleado: null,
      idCurso: null,
      idSeccion: null
    });
  }

  private limpiarMensajes(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }

  private obtenerTextoRespuesta(res: any): string {
    if (typeof res === 'string') {
      try {
        const parsed = JSON.parse(res);
        return parsed.mensaje || res;
      } catch {
        return res;
      }
    }
    return res?.mensaje || res?.error?.mensaje || res?.error || 'Procesado correctamente.';
  }
}