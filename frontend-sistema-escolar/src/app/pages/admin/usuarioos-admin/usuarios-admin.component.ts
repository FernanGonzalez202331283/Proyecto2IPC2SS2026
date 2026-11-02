import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UsuarioAdminService, UsuarioAdmin } from '../../../services/usuario-admin.service';

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
  
  paginaActual: number = 1;
  limite: number = 10;
  totalPaginas: number = 1;
  totalRegistros: number = 0;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private usuarioAdminService: UsuarioAdminService,
    private cdr: ChangeDetectorRef
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
    this.cdr.detectChanges(); 

    this.usuarioAdminService.obtenerUsuarios(this.paginaActual, this.limite).subscribe({
      next: (resp) => {
        this.usuarios = resp.contenido || [];
        this.paginaActual = resp.paginaActual || 1;
        this.totalPaginas = resp.totalPaginas || 1;
        this.totalRegistros = resp.totalRegistros || 0;
        this.cargando = false;
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        this.errorMessage = this.obtenerTextoError(err);
        this.cargando = false;
        this.cdr.detectChanges();
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
      return;
    }

    this.procesando = true;
    this.cdr.detectChanges();

    const datos: UsuarioAdmin = this.usuarioForm.value;

    if (this.modoEdicion && this.idUsuarioSeleccionado) {
      this.usuarioAdminService.actualizarUsuario(this.idUsuarioSeleccionado, datos).subscribe({
        next: () => {
          this.successMessage = 'Usuario actualizado exitosamente.';
          this.procesando = false;
          this.resetForm();
          this.cargarUsuarios();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoError(err);
          this.procesando = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.usuarioAdminService.registrarUsuario(datos).subscribe({
        next: () => {
          this.successMessage = 'Usuario registrado exitosamente.';
          this.procesando = false;
          this.resetForm();
          this.cargarUsuarios();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoError(err);
          this.procesando = false;
          this.cdr.detectChanges();
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
        this.cdr.detectChanges();
      },
      error: (err) => {
        usuario.estado = !nuevoEstado;
        this.errorMessage = this.obtenerTextoError(err);
        this.cdr.detectChanges();
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

  obtenerClaseRol(rol: string): string {
    switch (rol) {
      case 'SUPER_ADMIN':
        return 'bg-danger text-white';
      case 'ADMIN':
        return 'bg-warning text-dark';
      case 'SECRETARIA':
        return 'bg-info text-dark';
      case 'MAESTRO':
        return 'bg-primary text-white';
      case 'BIBLIOTECARIO':
        return 'bg-success text-white';
      case 'ESTUDIANTE':
        return 'bg-dark text-white';
      default:
        return 'bg-secondary text-white';
    }
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