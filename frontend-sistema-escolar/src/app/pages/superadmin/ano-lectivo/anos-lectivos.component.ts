import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AnoLectivoService, AnoLectivo } from '../../../services/ano-lectivo.service';

@Component({
  selector: 'app-anos-lectivos',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './anos-lectivos.component.html'
})
export class AnosLectivosComponent implements OnInit {
  anosLectivos: AnoLectivo[] = [];
  anoForm!: FormGroup;
  modoEdicion: boolean = false;
  idAnoSeleccionado: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private anoLectivoService: AnoLectivoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarAnosLectivos();
  }

  private initForm(): void {
    const anioActual = new Date().getFullYear();
    this.anoForm = this.fb.group({
      anio: [anioActual, [Validators.required, Validators.min(2000), Validators.max(2100)]],
      fechaInicio: ['', [Validators.required]],
      fechaFin: ['', [Validators.required]],
      estado: [true]
    });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.anoForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarAnosLectivos(): void {
    this.cargando = true;
    this.anoLectivoService.obtenerAnosLectivos().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.anosLectivos = data || [];
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

  guardarAnoLectivo(): void {
    this.limpiarMensajes();

    if (this.anoForm.invalid) {
      this.anoForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    const datos: AnoLectivo = this.anoForm.value;

    if (this.modoEdicion && this.idAnoSeleccionado) {
      this.anoLectivoService.actualizarAnoLectivo(this.idAnoSeleccionado, datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Año lectivo actualizado correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarAnosLectivos();
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
      this.anoLectivoService.registrarAnoLectivo(datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Año lectivo registrado correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarAnosLectivos();
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

  toggleEstado(ano: AnoLectivo): void {
    if (!ano.idAnoLectivo) return;

    this.limpiarMensajes();
    const nuevoEstado = !ano.estado;

    this.anoLectivoService.cambiarEstado(ano.idAnoLectivo, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
          ano.estado = nuevoEstado;
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

  editarAnoLectivo(ano: AnoLectivo): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idAnoSeleccionado = ano.idAnoLectivo || null;

    this.anoForm.patchValue({
      anio: ano.anio,
      fechaInicio: ano.fechaInicio,
      fechaFin: ano.fechaFin,
      estado: ano.estado
    });
  }

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idAnoSeleccionado = null;
    this.anoForm.reset({
      anio: new Date().getFullYear(),
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