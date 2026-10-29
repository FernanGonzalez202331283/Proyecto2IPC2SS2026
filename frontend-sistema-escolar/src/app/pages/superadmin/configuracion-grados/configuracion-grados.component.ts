import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfiguracionGradoService, ConfiguracionGrado } from '../../../services/configuracion-grado.service';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-configuracion-grados',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './configuracion-grados.component.html'
})
export class ConfiguracionGradosComponent implements OnInit {
  configuraciones: ConfiguracionGrado[] = [];
  configForm!: FormGroup;
  modoEdicion: boolean = false;
  idSeleccionado: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private gradoService: ConfiguracionGradoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

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
    this.gradoService.obtenerConfiguraciones().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.configuraciones = (data || []).sort((a, b) => a.orden - b.orden);
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

  guardarConfiguracion(): void {
    this.limpiarMensajes();

    if (this.configForm.invalid) {
      this.configForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    const datos: ConfiguracionGrado = this.configForm.value;

    if (this.modoEdicion && this.idSeleccionado) {
      this.gradoService.actualizarConfiguracion(this.idSeleccionado, datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Configuración actualizada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarConfiguraciones();
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
      this.gradoService.registrarConfiguracion(datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Configuración registrada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarConfiguraciones();
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

  toggleEstado(config: ConfiguracionGrado): void {
    if (!config.idConfiguracion) return;

    this.limpiarMensajes();
    const nuevoEstado = !config.estado;

    this.gradoService.cambiarEstado(config.idConfiguracion, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
          config.estado = nuevoEstado;
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
      orden: this.configuraciones.length + 1,
      estado: true
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