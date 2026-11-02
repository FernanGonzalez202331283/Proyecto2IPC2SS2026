import { Component, OnInit, inject, DestroyRef, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { 
  SeccionService, 
  Seccion, 
  GradoOption, 
  AnioLectivoOption, 
  RespuestaPaginada 
} from '../../../services/seccion.service';

@Component({
  selector: 'app-secciones',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './secciones.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeccionesComponent implements OnInit {
  private fb = inject(FormBuilder);
  private seccionService = inject(SeccionService);
  private location = inject(Location);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  secciones: Seccion[] = [];
  grados: GradoOption[] = [];
  aniosLectivos: AnioLectivoOption[] = [];

  seccionForm!: FormGroup;
  modoEdicion = false;
  idSeccionSeleccionada: number | null = null;

  // Paginación
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
    this.cargarCatalogos();
    this.cargarSecciones();
  }

  private initForm(): void {
    this.seccionForm = this.fb.group({
      idGrado: [null, [Validators.required, Validators.min(1)]],
      idAnioLectivo: [null, [Validators.required, Validators.min(1)]],
      nombre: ['', [Validators.required, Validators.maxLength(10)]],
      capacidad: [30, [Validators.required, Validators.min(1)]],
      estado: [true]
    });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.seccionForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarCatalogos(): void {
    this.seccionService.obtenerGrados()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) ? resp : (resp?.contenido || resp?.lista || []);
          this.grados = lista;
          this.cdr.markForCheck();
        },
        error: (err) => console.error('Error al cargar grados:', err)
      });

    this.seccionService.obtenerAniosLectivos()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) ? resp : (resp?.contenido || resp?.lista || []);
          this.aniosLectivos = lista.map((item: any) => ({
            ...item,
            idAnioLectivo: item.idAnioLectivo || item.idAnoLectivo
          }));
          this.cdr.markForCheck();
        },
        error: (err) => console.error('Error al cargar años lectivos:', err)
      });
  }

  cargarSecciones(): void {
    this.cargando = true;
    this.cdr.markForCheck();

    this.seccionService.obtenerSecciones(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          // Extraer la lista sin importar la clave usada en el JSON
          const lista = Array.isArray(resp) 
            ? resp 
            : (resp?.contenido || resp?.lista || resp?.elementos || resp?.data || []);

          this.secciones = lista.map((item: any) => ({
            ...item,
            idAnioLectivo: item.idAnioLectivo || item.idAnoLectivo
          }));

          this.paginaActual = resp?.paginaActual || resp?.pagina || 1;
          this.totalPaginas = resp?.totalPaginas || 1;
          this.totalRegistros = resp?.totalRegistros || resp?.total || this.secciones.length;

          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.secciones = [];
          this.errorMessage = this.obtenerTextoError(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarSecciones();
    }
  }

  guardarSeccion(): void {
    this.limpiarMensajes();

    if (this.seccionForm.invalid) {
      this.seccionForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      this.cdr.markForCheck();
      return;
    }

    this.procesando = true;
    this.cdr.markForCheck();
    const formVal = this.seccionForm.value;

    const payload: Seccion = {
      idGrado: Number(formVal.idGrado),
      idAnioLectivo: Number(formVal.idAnioLectivo),
      nombre: String(formVal.nombre).trim(),
      capacidad: Number(formVal.capacidad),
      estado: Boolean(formVal.estado)
    };

    if (this.modoEdicion && this.idSeccionSeleccionada) {
      this.seccionService.actualizarSeccion(this.idSeccionSeleccionada, payload)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Sección actualizada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarSecciones();
          },
          error: (err) => {
            this.errorMessage = this.obtenerTextoError(err);
            this.procesando = false;
            this.cdr.markForCheck();
          }
        });
    } else {
      this.seccionService.registrarSeccion(payload)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Sección registrada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarSecciones();
          },
          error: (err) => {
            this.errorMessage = this.obtenerTextoError(err);
            this.procesando = false;
            this.cdr.markForCheck();
          }
        });
    }
  }

  toggleEstado(seccion: Seccion): void {
    if (!seccion.idSeccion) return;

    this.limpiarMensajes();
    const nuevoEstado = !seccion.estado;

    this.seccionService.cambiarEstado(seccion.idSeccion, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          seccion.estado = nuevoEstado;
          this.successMessage = `Estado actualizado a ${nuevoEstado ? 'Activo' : 'Inactivo'}.`;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoError(err);
          this.cdr.markForCheck();
        }
      });
  }

  editarSeccion(seccion: Seccion): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeccionSeleccionada = seccion.idSeccion || null;

    this.seccionForm.patchValue({
      idGrado: seccion.idGrado,
      idAnioLectivo: seccion.idAnioLectivo || (seccion as any).idAnoLectivo,
      nombre: seccion.nombre,
      capacidad: seccion.capacidad,
      estado: seccion.estado
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
    this.idSeccionSeleccionada = null;
    this.seccionForm.reset({
      idGrado: null,
      idAnioLectivo: null,
      capacidad: 30,
      estado: true
    });
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