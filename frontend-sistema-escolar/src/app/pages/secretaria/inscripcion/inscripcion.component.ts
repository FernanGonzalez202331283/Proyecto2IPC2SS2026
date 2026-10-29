import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { InscripcionService, Inscripcion } from '../../../services/inscripcion.service';
import { EstudianteService, Estudiante } from '../../../services/estudiante.service';
import { SeccionService, Seccion } from '../../../services/seccion.service';
import { GradoService, Grado } from '../../../services/grado.service';

@Component({
  selector: 'app-inscripciones',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './inscripcion.component.html'
})
export class InscripcionesComponent implements OnInit {
  inscripciones: Inscripcion[] = [];
  estudiantesActivos: Estudiante[] = [];
  gradosLista: Grado[] = [];
  seccionesActivas: Seccion[] = [];
  seccionesFiltradas: Seccion[] = [];

  inscripcionForm!: FormGroup;
  modoEdicion: boolean = false;
  idSeleccionado: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private inscripcionService: InscripcionService,
    private estudianteService: EstudianteService,
    private seccionService: SeccionService,
    private gradoService: GradoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarInscripciones();
    this.cargarEstudiantes();
    this.cargarGrados();
    this.cargarSecciones();
  }

  private initForm(): void {
    const fechaHoy = new Date().toISOString().substring(0, 10);
    this.inscripcionForm = this.fb.group({
      idEstudiante: [null, [Validators.required]],
      idGradoAux: [null, [Validators.required]],
      idSeccion: [null, [Validators.required]],
      fechaInscripcion: [fechaHoy, [Validators.required]],
      estado: ['ACTIVA', [Validators.required]]
    });

    // Escucha en tiempo real cambios en la selección del Grado
    this.inscripcionForm.get('idGradoAux')?.valueChanges.subscribe((idGrado) => {
      this.ngZone.run(() => {
        this.filtrarSeccionesPorGrado(idGrado);
        // Si cambia el grado, limpiamos la sección elegida anteriormente
        this.inscripcionForm.patchValue({ idSeccion: null }, { emitEvent: false });
        this.cdr.detectChanges();
      });
    });
  }

  esCampoInvalido(controlName: string): boolean {
    const control = this.inscripcionForm.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarGrados(): void {
    this.gradoService.obtenerGrados().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.gradosLista = data || [];
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        console.error('Error al obtener grados:', err);
        this.ngZone.run(() => {
          this.errorMessage = 'Error al cargar grados. Verifique los permisos en GradoResource.';
          this.cdr.detectChanges();
        });
      }
    });
  }

  cargarSecciones(): void {
    this.seccionService.obtenerSecciones().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.seccionesActivas = (data || []).filter(s => s.estado);
          // Si ya había un grado seleccionado en el formulario, re-aplicar filtro
          const idGradoActual = this.inscripcionForm.get('idGradoAux')?.value;
          if (idGradoActual) {
            this.filtrarSeccionesPorGrado(idGradoActual);
          }
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        console.error('Error al obtener secciones:', err);
        this.ngZone.run(() => {
          this.errorMessage = 'Error al cargar secciones. Verifique los permisos en SeccionResource.';
          this.cdr.detectChanges();
        });
      }
    });
  }

  private filtrarSeccionesPorGrado(idGrado: any): void {
    if (idGrado === null || idGrado === undefined) {
      this.seccionesFiltradas = [];
      return;
    }
    const idGradoNum = Number(idGrado);
    this.seccionesFiltradas = this.seccionesActivas.filter(s => Number(s.idGrado) === idGradoNum);
  }

  cargarInscripciones(): void {
    this.cargando = true;
    this.inscripcionService.obtenerInscripciones().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.inscripciones = data || [];
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

  cargarEstudiantes(): void {
    this.estudianteService.obtenerEstudiantes().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.estudiantesActivos = (data || []).filter(e => e.estado === 'ACTIVO');
          this.cdr.detectChanges();
        });
      },
      error: (err) => console.error('Error al obtener estudiantes:', err)
    });
  }

  guardarInscripcion(): void {
    this.limpiarMensajes();

    if (this.inscripcionForm.invalid) {
      this.inscripcionForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    const formVal = this.inscripcionForm.value;

    const datos: Inscripcion = {
      idEstudiante: Number(formVal.idEstudiante),
      idSeccion: Number(formVal.idSeccion),
      fechaInscripcion: formVal.fechaInscripcion,
      estado: formVal.estado
    };

    if (this.modoEdicion && this.idSeleccionado) {
      datos.idInscripcion = this.idSeleccionado;

      this.inscripcionService.actualizarInscripcion(this.idSeleccionado, datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Inscripción actualizada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarInscripciones();
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
      this.inscripcionService.registrarInscripcion(datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Inscripción registrada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarInscripciones();
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

  editarInscripcion(i: Inscripcion): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = i.idInscripcion || null;

    const sec = this.seccionesActivas.find(s => Number(s.idSeccion) === Number(i.idSeccion));
    const idGrado = sec ? sec.idGrado : null;

    if (idGrado) {
      this.filtrarSeccionesPorGrado(idGrado);
    }

    this.inscripcionForm.patchValue({
      idEstudiante: i.idEstudiante,
      idGradoAux: idGrado,
      idSeccion: i.idSeccion,
      fechaInscripcion: i.fechaInscripcion,
      estado: i.estado
    });
  }

  cambiarEstadoInscripcion(i: Inscripcion, nuevoEstado: string): void {
    if (!i.idInscripcion) return;

    this.limpiarMensajes();

    this.inscripcionService.cambiarEstado(i.idInscripcion, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado de inscripción actualizado.';
          i.estado = nuevoEstado;
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

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  obtenerNombreEstudiante(idEstudiante: number): string {
    const est = this.estudiantesActivos.find(e => Number(e.idEstudiante) === Number(idEstudiante));
    if (!est || !est.persona) return `ID Estudiante: ${idEstudiante}`;
    return `${est.codigoEstudiante} - ${est.persona.nombres} ${est.persona.apellidos}`;
  }

  obtenerNombreSeccion(idSeccion: number): string {
    const sec = this.seccionesActivas.find(s => Number(s.idSeccion) === Number(idSeccion));
    if (!sec) return `ID Sección: ${idSeccion}`;
    const grado = this.gradosLista.find(g => Number(g.idGrado) === Number(sec.idGrado));
    const gradoNombre = grado ? grado.nombre : `Grado #${sec.idGrado}`;
    return `Sección ${sec.nombre} (${gradoNombre})`;
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idSeleccionado = null;
    this.seccionesFiltradas = [];
    const fechaHoy = new Date().toISOString().substring(0, 10);
    this.inscripcionForm.reset({
      idEstudiante: null,
      idGradoAux: null,
      idSeccion: null,
      fechaInscripcion: fechaHoy,
      estado: 'ACTIVA'
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