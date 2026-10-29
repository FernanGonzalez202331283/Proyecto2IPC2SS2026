import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Location } from '@angular/common'; // 1. Importar Location
import { EstudianteService, Estudiante } from '../../../services/estudiante.service';
import { Router, RouterLink } from '@angular/router';
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
    private router: Router,
    private location: Location, // 2. Inyectar Location
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarEstudiantes();
  }

  // Método seguro que regresa a la página previa o redirige según el rol si no hay historial
  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      // Respaldo dinámico en caso de haber ingresado directamente por URL
      const rawUser = localStorage.getItem('usuario') || sessionStorage.getItem('usuario') || localStorage.getItem('user');
      let rol = '';

      if (rawUser) {
        try {
          const parsed = JSON.parse(rawUser);
          rol = parsed.rol || parsed.usuario?.rol || '';
        } catch {
          rol = '';
        }
      }

      if (rol === 'SECRETARIA') {
        this.router.navigate(['/secretaria/dashboard']);
      } else {
        this.router.navigate(['/admin/dashboard']);
      }
    }
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
      }),
      encargado: this.fb.group({
        nombres: ['', [Validators.required, Validators.maxLength(100)]],
        apellidos: ['', [Validators.required, Validators.maxLength(100)]],
        telefono: ['', [Validators.required, Validators.maxLength(20)]],
        correo: ['', [Validators.email]],
        direccion: ['', [Validators.maxLength(255)]],
        parentesco: ['PADRE', [Validators.required]],
        responsablePrincipal: [true]
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
    const formVal = this.estudianteForm.value;

    const datos: Estudiante = {
      codigoEstudiante: formVal.codigoEstudiante,
      informacionMedica: formVal.informacionMedica,
      estado: formVal.estado,
      persona: formVal.persona,
      encargados: [
        {
          idEncargado: 0,
          parentesco: formVal.encargado.parentesco,
          responsablePrincipal: formVal.encargado.responsablePrincipal,
          encargado: {
            nombres: formVal.encargado.nombres,
            apellidos: formVal.encargado.apellidos,
            telefono: formVal.encargado.telefono,
            correo: formVal.encargado.correo,
            direccion: formVal.encargado.direccion,
            estado: true
          }
        }
      ]
    };

    if (this.modoEdicion && this.idSeleccionado) {
      datos.idEstudiante = this.idSeleccionado;
      if (this.idPersonaSeleccionada) {
        datos.idPersona = this.idPersonaSeleccionada;
        datos.persona.idPersona = this.idPersonaSeleccionada;
      }

      this.estudianteService.actualizarEstudiante(this.idSeleccionado, datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Expediente actualizado correctamente.';
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
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Estudiante y encargado registrados correctamente.';
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

    const primerEncargado = e.encargados && e.encargados.length > 0 ? e.encargados[0] : null;

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
      },
      encargado: {
        nombres: primerEncargado?.encargado?.nombres || '',
        apellidos: primerEncargado?.encargado?.apellidos || '',
        telefono: primerEncargado?.encargado?.telefono || '',
        correo: primerEncargado?.encargado?.correo || '',
        direccion: primerEncargado?.encargado?.direccion || '',
        parentesco: primerEncargado?.parentesco || 'PADRE',
        responsablePrincipal: primerEncargado?.responsablePrincipal ?? true
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
      },
      encargado: {
        nombres: '',
        apellidos: '',
        telefono: '',
        correo: '',
        direccion: '',
        parentesco: 'PADRE',
        responsablePrincipal: true
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