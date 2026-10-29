import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UsuarioAdminService, UsuarioAdmin } from '../../../services/usuario-admin.service';
import { ChangeDetectorRef } from '@angular/core';
import { NgZone } from '@angular/core';
@Component({
  selector: 'app-usuarios-admin',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './usuarios-admin.component.html',
})
export class UsuariosAdminComponent implements OnInit {
  usuarios: UsuarioAdmin[] = [];
  usuarioForm!: FormGroup;
  modoEdicion: boolean = false;
  idUsuarioSeleccionado: number | null = null;
  
  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false; // Indica si hay una petición en curso

  constructor(
    private fb: FormBuilder,
    private usuarioAdminService: UsuarioAdminService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone // Inyección
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarUsuarios();
  }

  private initForm(): void {
    this.usuarioForm = this.fb.group({
      username: ['', [Validators.required]],
      contraseña: ['', [Validators.required, Validators.minLength(4)]],
      rol: ['ADMIN', [Validators.required]],
      estado: [true],
      persona: this.fb.group({
        idPersona: [null],
        nombres: ['', [Validators.required]],
        apellidos: ['', [Validators.required]],
        dpi: ['', [Validators.required, Validators.minLength(13), Validators.maxLength(13)]],
        telefono: [''],
        correo: ['', [Validators.email]],
        direccion: ['']
      })
    });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.usuarioForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarUsuarios(): void {
    this.cargando = true;
    this.usuarioAdminService.obtenerUsuarios().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.usuarios = (data || []).filter(u => u.rol === 'SUPER_ADMIN' || u.rol === 'ADMIN');
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => {
          this.errorMessage = 'No se pudo conectar con el servidor.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  guardarUsuario(): void {
    this.limpiarMensajes();

    if (this.usuarioForm.invalid) {
      this.usuarioForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true; // Activa estado de carga en el botón
    const datos: UsuarioAdmin = this.usuarioForm.value;

    if (this.modoEdicion && this.idUsuarioSeleccionado) {
      this.usuarioAdminService.actualizarUsuario(this.idUsuarioSeleccionado, datos).subscribe({
        next: () => {
          this.ngZone.run(() => {
            this.successMessage = 'Usuario actualizado exitosamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarUsuarios();
            this.cdr.detectChanges();
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.errorMessage = this.obtenerTextoError(err);
            this.procesando = false;
            this.cdr.detectChanges(); // Renderiza el mensaje de error de Java AL INSTANTE
          });
        }
      });
    } else {
      this.usuarioAdminService.registrarUsuario(datos).subscribe({
        next: () => {
          this.ngZone.run(() => {
            this.successMessage = 'Usuario registrado exitosamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarUsuarios();
            this.cdr.detectChanges();
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.errorMessage = this.obtenerTextoError(err);
            this.procesando = false;
            this.cdr.detectChanges(); // Renderiza "El username ya está registrado" AL INSTANTE
          });
        }
      });
    }
  }

  toggleEstado(usuario: UsuarioAdmin): void {
    if (!usuario.idUsuario) return;

    this.limpiarMensajes();
    const nuevoEstado = !usuario.estado;

    usuario.estado = nuevoEstado;

    this.usuarioAdminService.cambiarEstado(usuario.idUsuario, nuevoEstado).subscribe({
      next: () => {
        this.ngZone.run(() => {
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          usuario.estado = !nuevoEstado;
          this.errorMessage = this.obtenerTextoError(err);
          this.cdr.detectChanges();
        });
      }
    });
  }

  editarUsuario(usuario: UsuarioAdmin): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idUsuarioSeleccionado = usuario.idUsuario || null;

    this.usuarioForm.get('contraseña')?.clearValidators();
    this.usuarioForm.get('contraseña')?.updateValueAndValidity();

    this.usuarioForm.patchValue({
      username: usuario.username,
      contraseña: '',
      rol: usuario.rol,
      estado: usuario.estado,
      persona: {
        idPersona: usuario.idPersona || usuario.persona?.idPersona || null,
        nombres: usuario.persona?.nombres || '',
        apellidos: usuario.persona?.apellidos || '',
        dpi: usuario.persona?.dpi || '',
        telefono: usuario.persona?.telefono || '',
        correo: usuario.persona?.correo || '',
        direccion: usuario.persona?.direccion || ''
      }
    });
  }

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idUsuarioSeleccionado = null;
    this.usuarioForm.reset({
      rol: 'ADMIN',
      estado: true
    });

    const passCtrl = this.usuarioForm.get('contraseña');
    passCtrl?.setValidators([Validators.required, Validators.minLength(4)]);
    passCtrl?.updateValueAndValidity();
  }

  private limpiarMensajes(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }

  private obtenerTextoError(err: any): string {
    if (typeof err.error === 'string') {
      try {
        const parsed = JSON.parse(err.error);
        return parsed.mensaje || err.error;
      } catch {
        return err.error;
      }
    }
    return err.error?.mensaje || 'Error al procesar la solicitud.';
  }
}