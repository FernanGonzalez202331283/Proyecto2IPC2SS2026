import { Component, OnInit, inject, DestroyRef, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { 
  UsuarioOperativoService, 
  UsuarioOperativo, 
  UsuarioOperativoDTO,
  RespuestaPaginada 
} from '../../../services/usuario-operativo-service';

@Component({
  selector: 'app-usuarios-operativos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './usuario-operativo.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsuariosOperativosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private usuarioOperativoService = inject(UsuarioOperativoService);
  private location = inject(Location);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  usuarios: UsuarioOperativo[] = [];
  usuarioForm!: FormGroup;
  modoEdicion = false;
  idUsuarioSeleccionado: number | null = null;

  // Control de Paginación
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
    this.cargarUsuarios();
  }

  private initForm(): void {
    const fechaHoy = new Date().toISOString().substring(0, 10);

    this.usuarioForm = this.fb.group({
      username: ['', [Validators.required]],
      contraseña: ['', [Validators.required, Validators.minLength(4)]],
      rol: ['MAESTRO', [Validators.required]],
      estado: [true],
      persona: this.fb.group({
        idPersona: [null],
        nombres: ['', [Validators.required]],
        apellidos: ['', [Validators.required]],
        dpi: ['', [Validators.required, Validators.minLength(13), Validators.maxLength(13)]],
        telefono: [''],
        correo: ['', [Validators.email]],
        direccion: ['']
      }),
      empleado: this.fb.group({
        puesto: ['Docente', [Validators.required]],
        fechaContratacion: [fechaHoy, [Validators.required]],
        salario: [3500.00, [Validators.required, Validators.min(1)]]
      })
    });

    this.usuarioForm.get('rol')?.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(rol => {
        const puestoControl = this.usuarioForm.get('empleado.puesto');
        if (rol === 'MAESTRO') puestoControl?.setValue('Docente');
        else if (rol === 'SECRETARIA') puestoControl?.setValue('Secretaria Académica');
        else if (rol === 'BIBLIOTECARIO') puestoControl?.setValue('Encargado de Biblioteca');
        else if (rol === 'PERSONAL') puestoControl?.setValue('Personal de Mantenimiento');
        
        this.cdr.markForCheck();
      });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.usuarioForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarUsuarios(): void {
    this.cargando = true;
    this.cdr.markForCheck();

    this.usuarioOperativoService.obtenerUsuarios(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: RespuestaPaginada<UsuarioOperativo> | any) => {
          if (resp && Array.isArray(resp.contenido)) {
            this.usuarios = resp.contenido;
            this.paginaActual = resp.paginaActual || 1;
            this.totalPaginas = resp.totalPaginas || 1;
            this.totalRegistros = resp.totalRegistros || 0;
          } else if (Array.isArray(resp)) {
            this.usuarios = resp;
            this.totalRegistros = resp.length;
            this.totalPaginas = 1;
          } else {
            this.usuarios = [];
            this.totalRegistros = 0;
            this.totalPaginas = 1;
          }

          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.usuarios = [];
          this.errorMessage = this.obtenerTextoError(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarUsuarios();
    }
  }

  guardarUsuario(): void {
    this.limpiarMensajes();

    if (this.usuarioForm.invalid) {
      this.usuarioForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      this.cdr.markForCheck();
      return;
    }

    this.procesando = true;
    this.cdr.markForCheck();
    const formVal = this.usuarioForm.value;

    if (this.modoEdicion && this.idUsuarioSeleccionado) {
      const datosActualizar: UsuarioOperativo = {
        username: formVal.username,
        contraseña: formVal.contraseña || undefined,
        rol: formVal.rol,
        estado: formVal.estado,
        persona: formVal.persona
      };

      this.usuarioOperativoService.actualizarUsuario(this.idUsuarioSeleccionado, datosActualizar)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Usuario operativo actualizado exitosamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarUsuarios();
          },
          error: (err) => {
            this.errorMessage = this.obtenerTextoError(err);
            this.procesando = false;
            this.cdr.markForCheck();
          }
        });
    } else {
      const dtoPayload: UsuarioOperativoDTO = {
        crearCuentaUsuario: true,
        persona: formVal.persona,
        empleado: {
          puesto: formVal.empleado.puesto,
          fechaContratacion: formVal.empleado.fechaContratacion,
          salario: Number(formVal.empleado.salario)
        },
        usuario: {
          username: formVal.username,
          contraseña: formVal.contraseña,
          rol: formVal.rol
        }
      };

      this.usuarioOperativoService.registrarUsuarioOperativo(dtoPayload)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Usuario operativo y contrato registrados exitosamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarUsuarios();
          },
          error: (err) => {
            this.errorMessage = this.obtenerTextoError(err);
            this.procesando = false;
            this.cdr.markForCheck();
          }
        });
    }
  }

  toggleEstado(usuario: UsuarioOperativo): void {
    const id = usuario.idUsuario || usuario.usuario?.idUsuario;
    if (!id) return;

    this.limpiarMensajes();
    const nuevoEstado = !usuario.estado;

    this.usuarioOperativoService.cambiarEstado(id, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          usuario.estado = nuevoEstado;
          if (usuario.usuario) usuario.usuario.estado = nuevoEstado;
          this.successMessage = `Estado actualizado a ${nuevoEstado ? 'Activo' : 'Inactivo'}.`;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoError(err);
          this.cdr.markForCheck();
        }
      });
  }

  editarUsuario(usuario: UsuarioOperativo): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idUsuarioSeleccionado = usuario.idUsuario || usuario.usuario?.idUsuario || null;

    this.usuarioForm.get('contraseña')?.clearValidators();
    this.usuarioForm.get('contraseña')?.updateValueAndValidity();

    this.usuarioForm.patchValue({
      username: usuario.username || usuario.usuario?.username || '',
      contraseña: '',
      rol: usuario.rol || usuario.usuario?.rol || 'MAESTRO',
      estado: usuario.estado ?? usuario.usuario?.estado ?? true,
      persona: {
        idPersona: usuario.persona?.idPersona || usuario.idPersona || null,
        nombres: usuario.persona?.nombres || '',
        apellidos: usuario.persona?.apellidos || '',
        dpi: usuario.persona?.dpi || '',
        telefono: usuario.persona?.telefono || '',
        correo: usuario.persona?.correo || '',
        direccion: usuario.persona?.direccion || ''
      }
    });

    this.cdr.markForCheck();
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
    this.idUsuarioSeleccionado = null;
    const fechaHoy = new Date().toISOString().substring(0, 10);

    this.usuarioForm.reset({
      rol: 'MAESTRO',
      estado: true,
      empleado: {
        puesto: 'Docente',
        fechaContratacion: fechaHoy,
        salario: 3500.00
      }
    });

    const passCtrl = this.usuarioForm.get('contraseña');
    passCtrl?.setValidators([Validators.required, Validators.minLength(4)]);
    passCtrl?.updateValueAndValidity();

    this.cdr.markForCheck();
  }

  private limpiarMensajes(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }

  private obtenerTextoError(err: any): string {
    if (typeof err?.error === 'string') {
      try {
        const parsed = JSON.parse(err.error);
        return parsed.mensaje || err.error;
      } catch {
        return err.error;
      }
    }
    return err?.error?.mensaje || 'Error al procesar la solicitud.';
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
    return res?.mensaje || 'Operación completada.';
  }
}