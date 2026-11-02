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
import { CurriculoService, Curriculo } from '../../../services/curriculo.service';
import { GradoService, Grado } from '../../../services/grado.service';
import { CursoService, Curso } from '../../../services/curso.services';
import { AnoLectivoService, AnoLectivo } from '../../../services/ano-lectivo.service';

@Component({
  selector: 'app-curriculos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './curriculo.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CurriculosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private curriculoService = inject(CurriculoService);
  private gradoService = inject(GradoService);
  private cursoService = inject(CursoService);
  private anoLectivoService = inject(AnoLectivoService);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  curriculos: Curriculo[] = [];
  grados: Grado[] = [];
  cursos: Curso[] = [];
  anosLectivos: AnoLectivo[] = [];

  curriculoForm!: FormGroup;
  modoEdicion = false;
  idSeleccionado: number | null = null;

  paginaActual = 1;
  limite = 10;
  totalPaginas = 1;
  totalRegistros = 0;

  cargando = false;
  procesando = false;
  successMessage = '';
  errorMessage = '';

  ngOnInit(): void {
    this.initForm();
    this.cargarCatalogos();
    this.cargarCurriculos();
  }

  private initForm(): void {
    this.curriculoForm = this.fb.group({
      idGrado: [null, [Validators.required, Validators.min(1)]],
      idCurso: [null, [Validators.required, Validators.min(1)]],
      idAnioLectivo: [null, [Validators.required, Validators.min(1)]],
      descripcion: [''],
      estado: [true]
    });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.curriculoForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarCatalogos(): void {
    this.gradoService.obtenerGrados(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res: any) => {
          this.grados = Array.isArray(res) ? res : (res?.contenido || []);
          this.cdr.markForCheck();
        }
      });

    this.cursoService.obtenerCursos(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res: any) => {
          const lista = Array.isArray(res) ? res : (res?.contenido || []);
          this.cursos = lista.filter((c: Curso) => c.estado);
          this.cdr.markForCheck();
        }
      });

    this.anoLectivoService.obtenerAnosLectivos(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) ? resp : (resp?.contenido || []);
          this.anosLectivos = lista.filter((a: AnoLectivo) => a.estado);
          this.cdr.markForCheck();
        },
        error: (err) => console.error('Error al cargar años lectivos:', err)
      });
  }

  cargarCurriculos(): void {
    this.cargando = true;
    this.cdr.markForCheck();

    this.curriculoService.obtenerCurriculos(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) 
            ? resp 
            : (resp?.contenido || resp?.lista || resp?.datos || resp?.elementos || resp?.data || []);

          this.curriculos = lista;
          this.paginaActual = resp?.paginaActual || 1;
          this.totalPaginas = resp?.totalPaginas || 1;
          this.totalRegistros = resp?.totalRegistros || this.curriculos.length;

          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.curriculos = [];
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarCurriculos();
    }
  }

  guardarCurriculo(): void {
    this.limpiarMensajes();

    if (this.curriculoForm.invalid) {
      this.curriculoForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete los campos requeridos marcados en rojo.';
      this.cdr.markForCheck();
      return;
    }

    this.procesando = true;
    this.cdr.markForCheck();

    const payload: Curriculo = {
      idGrado: Number(this.curriculoForm.value.idGrado),
      idCurso: Number(this.curriculoForm.value.idCurso),
      idAnioLectivo: Number(this.curriculoForm.value.idAnioLectivo),
      descripcion: this.curriculoForm.value.descripcion,
      estado: this.curriculoForm.value.estado
    };

    const operacion$ = (this.modoEdicion && this.idSeleccionado)
      ? this.curriculoService.actualizarCurriculo(this.idSeleccionado, payload)
      : this.curriculoService.registrarCurriculo(payload);

    operacion$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (res) => {
        const defaultMsg = this.modoEdicion ? 'Currículo actualizado correctamente.' : 'Currículo registrado correctamente.';
        this.successMessage = this.obtenerTextoRespuesta(res) || defaultMsg;
        this.procesando = false;
        this.resetForm();
        this.cargarCurriculos();
      },
      error: (err) => {
        this.errorMessage = this.obtenerTextoRespuesta(err);
        this.procesando = false;
        this.cdr.markForCheck();
      }
    });
  }

  toggleEstado(c: Curriculo): void {
    if (!c.idCurriculo) return;

    this.limpiarMensajes();
    const nuevoEstado = !c.estado;

    this.curriculoService.cambiarEstado(c.idCurriculo, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
          c.estado = nuevoEstado;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cdr.markForCheck();
        }
      });
  }

  editarCurriculo(c: Curriculo): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = c.idCurriculo || null;

    this.curriculoForm.patchValue({
      idGrado: c.idGrado,
      idCurso: c.idCurso,
      idAnioLectivo: c.idAnioLectivo,
      descripcion: c.descripcion || '',
      estado: c.estado
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
    this.curriculoForm.reset({
      idGrado: null,
      idCurso: null,
      idAnioLectivo: null,
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