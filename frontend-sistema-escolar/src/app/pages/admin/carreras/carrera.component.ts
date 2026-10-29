import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CarreraService, Carrera } from '../../../services/carrera.service';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-carreras',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './carreras.component.html'
})
export class CarrerasComponent implements OnInit {
  carreras: Carrera[] = [];
  carreraForm!: FormGroup;
  modoEdicion: boolean = false;
  idSeleccionado: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private carreraService: CarreraService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarCarreras();
  }

  private initForm(): void {
    this.carreraForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(150)]],
      descripcion: [''],
      estado: [true]
    });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.carreraForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarCarreras(): void {
    this.cargando = true;
    this.carreraService.obtenerCarreras().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.carreras = data || [];
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

  guardarCarrera(): void {
    this.limpiarMensajes();

    if (this.carreraForm.invalid) {
      this.carreraForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    const datos: Carrera = this.carreraForm.value;

    if (this.modoEdicion && this.idSeleccionado) {
      this.carreraService.actualizarCarrera(this.idSeleccionado, datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Carrera actualizada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarCarreras();
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
      this.carreraService.registrarCarrera(datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Carrera registrada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarCarreras();
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

  toggleEstado(carrera: Carrera): void {
    if (!carrera.idCarrera) return;

    this.limpiarMensajes();
    const nuevoEstado = !carrera.estado;

    this.carreraService.cambiarEstado(carrera.idCarrera, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
          carrera.estado = nuevoEstado;
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

  editarCarrera(carrera: Carrera): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = carrera.idCarrera || null;

    this.carreraForm.patchValue({
      nombre: carrera.nombre,
      descripcion: carrera.descripcion || '',
      estado: carrera.estado
    });
  }

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idSeleccionado = null;
    this.carreraForm.reset({
      nombre: '',
      descripcion: '',
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