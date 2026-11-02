import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
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
    private anoLectivoService: AnoLectivoService,
    private cdr: ChangeDetectorRef
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
    this.cdr.detectChanges();

    this.anoLectivoService.obtenerAnosLectivos(this.paginaActual, this.limite).subscribe({
      next: (resp: any) => {
        console.log('Respuesta recibida del backend:', resp);

        if (resp && Array.isArray(resp.contenido)) {
          this.anosLectivos = resp.contenido;
          this.paginaActual = resp.paginaActual || 1;
          this.totalPaginas = resp.totalPaginas || 1;
          this.totalRegistros = resp.totalRegistros || 0;
        } else if (Array.isArray(resp)) {
          // 2. Respuesta en lista plana directa: [...]
          this.anosLectivos = resp;
          this.totalRegistros = resp.length;
          this.totalPaginas = 1;
        } else {
          this.anosLectivos = [];
          this.totalRegistros = 0;
          this.totalPaginas = 1;
        }

        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar años lectivos:', err);
        this.anosLectivos = []; // Garantiza que siempre sea un arreglo ante fallos HTTP
        this.errorMessage = this.obtenerTextoRespuesta(err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarAnosLectivos();
    }
  }

  guardarAnoLectivo(): void {
    this.limpiarMensajes();

    if (this.anoForm.invalid) {
      this.anoForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    this.cdr.detectChanges();
    const datos: AnoLectivo = this.anoForm.value;

    if (this.modoEdicion && this.idAnoSeleccionado) {
      this.anoLectivoService.actualizarAnoLectivo(this.idAnoSeleccionado, datos).subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Año lectivo actualizado correctamente.';
          this.procesando = false;
          this.resetForm();
          this.cargarAnosLectivos();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.procesando = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.anoLectivoService.registrarAnoLectivo(datos).subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Año lectivo registrado correctamente.';
          this.procesando = false;
          this.resetForm();
          this.cargarAnosLectivos();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.procesando = false;
          this.cdr.detectChanges();
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
        this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
        ano.estado = nuevoEstado;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = this.obtenerTextoRespuesta(err);
        this.cdr.detectChanges();
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