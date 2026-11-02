import { 
  Component, 
  OnInit, 
  inject, 
  DestroyRef, 
  ChangeDetectorRef, 
  ChangeDetectionStrategy 
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CarreraService, Carrera, RespuestaPaginada } from '../../../services/carrera.service';

@Component({
  selector: 'app-carreras',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './carreras.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CarrerasComponent implements OnInit {
  private fb = inject(FormBuilder);
  private carreraService = inject(CarreraService);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  carreras: Carrera[] = [];
  carreraForm!: FormGroup;
  modoEdicion: boolean = false;
  idSeleccionado: number | null = null;

  // Paginación
  paginaActual: number = 1;
  limite: number = 10;
  totalPaginas: number = 1;
  totalRegistros: number = 0;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

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
    this.cdr.markForCheck();

    this.carreraService.obtenerCarreras(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp)
            ? resp
            : (resp?.contenido || resp?.lista || resp?.elementos || resp?.data || []);

          this.carreras = lista;
          this.paginaActual = resp?.paginaActual || 1;
          this.totalPaginas = resp?.totalPaginas || 1;
          this.totalRegistros = resp?.totalRegistros || this.carreras.length;

          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.carreras = [];
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarCarreras();
    }
  }

  guardarCarrera(): void {
    this.limpiarMensajes();

    if (this.carreraForm.invalid) {
      this.carreraForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      this.cdr.markForCheck();
      return;
    }

    this.procesando = true;
    this.cdr.markForCheck();
    const datos: Carrera = this.carreraForm.value;

    if (this.modoEdicion && this.idSeleccionado) {
      this.carreraService.actualizarCarrera(this.idSeleccionado, datos)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Carrera actualizada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarCarreras();
          },
          error: (err) => {
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.procesando = false;
            this.cdr.markForCheck();
          }
        });
    } else {
      this.carreraService.registrarCarrera(datos)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Carrera registrada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarCarreras();
          },
          error: (err) => {
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.procesando = false;
            this.cdr.markForCheck();
          }
        });
    }
  }

  toggleEstado(carrera: Carrera): void {
    if (!carrera.idCarrera) return;

    this.limpiarMensajes();
    const nuevoEstado = !carrera.estado;

    this.carreraService.cambiarEstado(carrera.idCarrera, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
          carrera.estado = nuevoEstado;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cdr.markForCheck();
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

    this.cdr.markForCheck();
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