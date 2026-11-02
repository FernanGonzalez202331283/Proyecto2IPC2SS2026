import { 
  Component, 
  OnInit, 
  inject, 
  DestroyRef, 
  ChangeDetectorRef, 
  ChangeDetectionStrategy 
} from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EstudianteService, Estudiante } from '../../../services/estudiante.service';

@Component({
  selector: 'app-estudiantes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './estudiantes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EstudiantesComponent implements OnInit {
  private fb = inject(FormBuilder);
  private estudianteService = inject(EstudianteService);
  private router = inject(Router);
  private location = inject(Location);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  estudiantes: Estudiante[] = [];
  estudianteForm!: FormGroup;
  modoEdicion = false;
  idSeleccionado: number | null = null;
  idPersonaSeleccionada: number | null = null;

  // Variables de paginación
  paginaActual = 1;
  limite = 10;
  totalPaginas = 1;
  totalRegistros = 0;

  successMessage = '';
  errorMessage = '';
  cargando = false;
  procesando = false;

  ngOnInit(): void {
    this.initForm();
    this.cargarEstudiantes();
  }

  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
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
    this.cdr.markForCheck();

    this.estudianteService.obtenerEstudiantes(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) 
            ? resp 
            : (resp?.contenido || resp?.lista || resp?.datos || []);

          this.estudiantes = lista;
          this.paginaActual = resp?.paginaActual || 1;
          this.totalPaginas = resp?.totalPaginas || 1;
          this.totalRegistros = resp?.totalRegistros || this.estudiantes.length;

          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.estudiantes = [];
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarEstudiantes();
    }
  }

  guardarEstudiante(): void {
    this.limpiarMensajes();

    if (this.estudianteForm.invalid) {
      this.estudianteForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      this.cdr.markForCheck();
      return;
    }

    this.procesando = true;
    this.cdr.markForCheck();
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

      this.estudianteService.actualizarEstudiante(this.idSeleccionado, datos)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Expediente de estudiante actualizado correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarEstudiantes();
            this.cdr.markForCheck();
          },
          error: (err) => {
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.procesando = false;
            this.cdr.markForCheck();
          }
        });
    } else {
      this.estudianteService.registrarEstudiante(datos)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Estudiante y encargado registrados correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarEstudiantes();
            this.cdr.markForCheck();
          },
          error: (err) => {
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.procesando = false;
            this.cdr.markForCheck();
          }
        });
    }
  }

  cambiarEstadoEstudiante(e: Estudiante, nuevoEstado: string): void {
    if (!e.idEstudiante) return;

    this.limpiarMensajes();

    this.estudianteService.cambiarEstado(e.idEstudiante, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado.';
          e.estado = nuevoEstado;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cdr.markForCheck();
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

    this.cdr.markForCheck();
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
    this.cdr.markForCheck();
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