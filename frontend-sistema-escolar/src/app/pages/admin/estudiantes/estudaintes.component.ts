import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EstudianteService, Estudiante } from '../../../services/estudiante.service';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-estudiantes',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './estudiantes.component.html'
})
export class EstudiantesComponent implements OnInit {
  estudiantes: Estudiante[] = [];
  estudianteForm!: FormGroup;
  modoEdicion: boolean = false;
  idSeleccionado: number | null = null;
  idPersonaSeleccionada: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private estudianteService: EstudianteService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarEstudiantes();
  }

  private initForm(): void {
    this.estudianteForm = this.fb.group({
      codigoEstudiante: ['', [Validators.required, Validators.maxLength(50)]],
      informacionMedica: [''],
      estado: ['ACTIVO', [Validators.required]],
      persona: this.fb.group({
        idPersona: [null],
        nombres: ['', [Validators.required, Validators.maxLength(100)]],
        apellidos: ['', [Validators.required, Validators.maxLength(100)]],
        dpi: ['', [Validators.required, Validators.maxLength(20)]],
        telefono: ['', [Validators.maxLength(20)]],
        correo: ['', [Validators.email]],
        direccion: ['', [Validators.maxLength(255)]]
      })
    });
  }

  esCampoInvalido(controlName: string, parentGroup: string = ''): boolean {
    let control;
    if (parentGroup) {
      control = this.estudianteForm.get(parentGroup)?.get(controlName);
    } else {
      control = this.estudianteForm.get(controlName);
    }
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarEstudiantes(): void {
    this.cargando = true;
    this.estudianteService.obtenerEstudiantes().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.estudiantes = data || [];
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

  guardarEstudiante(): void {
    this.limpiarMensajes();

    if (this.estudianteForm.invalid) {
      this.estudianteForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    const datos: Estudiante = this.estudianteForm.value;

    if (this.modoEdicion && this.idSeleccionado) {
      datos.idEstudiante = this.idSeleccionado;
      if (this.idPersonaSeleccionada) {
        datos.idPersona = this.idPersonaSeleccionada;
        datos.persona.idPersona = this.idPersonaSeleccionada;
      }

      this.estudianteService.actualizarEstudiante(this.idSeleccionado, datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Expediente de estudiante actualizado.';
            this.procesando = false;
            this.resetForm();
            this.cargarEstudiantes();
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
      this.estudianteService.registrarEstudiante(datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Estudiante registrado correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarEstudiantes();
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

  cambiarEstadoEstudiante(e: Estudiante, nuevoEstado: string): void {
    if (!e.idEstudiante) return;

    this.limpiarMensajes();

    this.estudianteService.cambiarEstado(e.idEstudiante, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado.';
          e.estado = nuevoEstado;
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

  editarEstudiante(e: Estudiante): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = e.idEstudiante || null;
    this.idPersonaSeleccionada = e.persona?.idPersona || null;

    this.estudianteForm.patchValue({
      codigoEstudiante: e.codigoEstudiante,
      informacionMedica: e.informacionMedica || '',
      estado: e.estado,
      persona: {
        idPersona: e.persona?.idPersona || null,
        nombres: e.persona?.nombres || '',
        apellidos: e.persona?.apellidos || '',
        dpi: e.persona?.dpi || '',
        telefono: e.persona?.telefono || '',
        correo: e.persona?.correo || '',
        direccion: e.persona?.direccion || ''
      }
    });
  }

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idSeleccionado = null;
    this.idPersonaSeleccionada = null;
    this.estudianteForm.reset({
      codigoEstudiante: '',
      informacionMedica: '',
      estado: 'ACTIVO',
      persona: {
        idPersona: null,
        nombres: '',
        apellidos: '',
        dpi: '',
        telefono: '',
        correo: '',
        direccion: ''
      }
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