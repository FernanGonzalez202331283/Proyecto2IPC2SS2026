import { Component, OnInit, inject, DestroyRef, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ConfiguracionGradoService, ConfiguracionGrado } from '../../../services/configuracion-grado.service';

@Component({
  selector: 'app-configuracion-grados',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './configuracion-grados.component.html'
})
export class ConfiguracionGradosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private gradoService = inject(ConfiguracionGradoService);
  private destroyRef = inject(DestroyRef);
  private cdr = inject(ChangeDetectorRef);

  configuraciones: ConfiguracionGrado[] = [];
  configForm!: FormGroup;
  modoEdicion = false;
  idSeleccionado: number | null = null;

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
    this.cargarConfiguraciones();
  }

  private initForm(): void {
    this.configForm = this.fb.group({
      nivel: ['PRIMARIA', [Validators.required]],
      nombreGrado: ['', [Validators.required, Validators.maxLength(100)]],
      orden: [1, [Validators.required, Validators.min(1)]],
      estado: [true]
    });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.configForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarConfiguraciones(): void {
    this.cargando = true;
    this.cdr.markForCheck();

    this.gradoService.obtenerConfiguraciones(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          let lista: ConfiguracionGrado[] = [];

          if (resp && Array.isArray(resp.contenido)) {
            lista = resp.contenido;
            this.paginaActual = resp.paginaActual || 1;
            this.totalPaginas = resp.totalPaginas || 1;
            this.totalRegistros = resp.totalRegistros || 0;
          } else if (Array.isArray(resp)) {
            lista = resp;
            this.totalRegistros = resp.length;
            this.totalPaginas = 1;
          } else {
            lista = [];
            this.totalRegistros = 0;
            this.totalPaginas = 1;
          }

          this.configuraciones = lista.sort((a, b) => a.orden - b.orden);
          this.cargando = false;
          this.cdr.markForCheck(); // Notifica a Angular para redibujar la tabla
        },
        error: (err) => {
          this.configuraciones = [];
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarConfiguraciones();
    }
  }

  guardarConfiguracion(): void {
    this.limpiarMensajes();

    if (this.configForm.invalid) {
      this.configForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      this.cdr.markForCheck();
      return;
    }

    this.procesando = true;
    this.cdr.markForCheck();
    const datos: ConfiguracionGrado = this.configForm.value;

    const operacion$ = (this.modoEdicion && this.idSeleccionado)
      ? this.gradoService.actualizarConfiguracion(this.idSeleccionado, datos)
      : this.gradoService.registrarConfiguracion(datos);

    operacion$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (res) => {
        const mensajeDefault = this.modoEdicion ? 'Configuración actualizada correctamente.' : 'Configuración registrada correctamente.';
        this.successMessage = this.obtenerTextoRespuesta(res) || mensajeDefault;
        this.procesando = false;
        this.resetForm();
        this.cargarConfiguraciones();
      },
      error: (err) => {
        this.errorMessage = this.obtenerTextoRespuesta(err);
        this.procesando = false;
        this.cdr.markForCheck();
      }
    });
  }

  toggleEstado(config: ConfiguracionGrado): void {
    if (!config.idConfiguracion) return;

    this.limpiarMensajes();
    const nuevoEstado = !config.estado;

    this.gradoService.cambiarEstado(config.idConfiguracion, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
          config.estado = nuevoEstado;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cdr.markForCheck();
        }
      });
  }

  editarConfiguracion(config: ConfiguracionGrado): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = config.idConfiguracion || null;

    this.configForm.patchValue({
      nivel: config.nivel,
      nombreGrado: config.nombreGrado,
      orden: config.orden,
      estado: config.estado
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
    this.configForm.reset({
      nivel: 'PRIMARIA',
      orden: this.totalRegistros + 1,
      estado: true
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