import { Component,  OnInit, inject, DestroyRef, ChangeDetectorRef,  ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CursoService, Curso, RespuestaPaginada } from '../../../services/curso.services';

@Component({
  selector: 'app-cursos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './curso.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CursosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private cursoService = inject(CursoService);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  cursos: Curso[] = [];
  cursoForm!: FormGroup;
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
    this.cargarCursos();
  }

  private initForm(): void {
    this.cursoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: [''],
      estado: [true]
    });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.cursoForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarCursos(): void {
    this.cargando = true;
    this.cdr.markForCheck();

    this.cursoService.obtenerCursos(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp)
            ? resp
            : (resp?.contenido || resp?.lista || resp?.elementos || resp?.data || []);

          this.cursos = lista;
          this.paginaActual = resp?.paginaActual || 1;
          this.totalPaginas = resp?.totalPaginas || 1;
          this.totalRegistros = resp?.totalRegistros || this.cursos.length;

          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.cursos = [];
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarCursos();
    }
  }

  guardarCurso(): void {
    this.limpiarMensajes();

    if (this.cursoForm.invalid) {
      this.cursoForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      this.cdr.markForCheck();
      return;
    }

    this.procesando = true;
    this.cdr.markForCheck();
    const datos: Curso = this.cursoForm.value;

    const operacion$ = (this.modoEdicion && this.idSeleccionado)
      ? this.cursoService.actualizarCurso(this.idSeleccionado, datos)
      : this.cursoService.registrarCurso(datos);

    operacion$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (res) => {
        const mensajeDefault = this.modoEdicion ? 'Curso actualizado correctamente.' : 'Curso registrado correctamente.';
        this.successMessage = this.obtenerTextoRespuesta(res) || mensajeDefault;
        this.procesando = false;
        this.resetForm();
        this.cargarCursos();
      },
      error: (err) => {
        this.errorMessage = this.obtenerTextoRespuesta(err);
        this.procesando = false;
        this.cdr.markForCheck();
      }
    });
  }

  toggleEstado(curso: Curso): void {
    if (!curso.idCurso) return;

    this.limpiarMensajes();
    const nuevoEstado = !curso.estado;

    this.cursoService.cambiarEstado(curso.idCurso, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
          curso.estado = nuevoEstado;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cdr.markForCheck();
        }
      });
  }

  editarCurso(curso: Curso): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = curso.idCurso || null;

    this.cursoForm.patchValue({
      nombre: curso.nombre,
      descripcion: curso.descripcion || '',
      estado: curso.estado
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
    this.cursoForm.reset({
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