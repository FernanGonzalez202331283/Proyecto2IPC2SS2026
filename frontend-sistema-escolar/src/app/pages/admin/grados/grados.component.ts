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
import { GradoService, Grado, RespuestaPaginada } from '../../../services/grado.service';
import { CarreraService, Carrera } from '../../../services/carrera.service';

@Component({
  selector: 'app-grados',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './grados.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GradosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private gradoService = inject(GradoService);
  private carreraService = inject(CarreraService);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  grados: Grado[] = [];
  carrerasActivas: Carrera[] = [];
  gradoForm!: FormGroup;
  modoEdicion = false;
  idSeleccionado: number | null = null;

  niveles = ['PREPRIMARIA', 'PRIMARIA', 'BASICO', 'DIVERSIFICADO'];

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
    this.cargarCarrerasActivas();
    this.cargarGrados();
  }

  private initForm(): void {
    this.gradoForm = this.fb.group({
      nivel: ['', [Validators.required]],
      idCarrera: [null],
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['']
    });

    // Cambiar dinámicamente la validación de 'idCarrera'
    this.gradoForm.get('nivel')?.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((nivel) => {
        const controlCarrera = this.gradoForm.get('idCarrera');
        if (nivel === 'DIVERSIFICADO') {
          controlCarrera?.setValidators([Validators.required, Validators.min(1)]);
        } else {
          controlCarrera?.clearValidators();
          controlCarrera?.setValue(null);
        }
        controlCarrera?.updateValueAndValidity();
        this.cdr.markForCheck();
      });
  }

  get esDiversificado(): boolean {
    return this.gradoForm.get('nivel')?.value === 'DIVERSIFICADO';
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.gradoForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarCarrerasActivas(): void {
    this.carreraService.obtenerCarrerasActivas()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.carrerasActivas = data || [];
          this.cdr.markForCheck();
        },
        error: (err) => console.error('Error al cargar carreras activas:', err)
      });
  }

  cargarGrados(): void {
    this.cargando = true;
    this.cdr.markForCheck();

    this.gradoService.obtenerGrados(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp)
            ? resp
            : (resp?.contenido || resp?.lista || resp?.elementos || resp?.data || []);

          this.grados = lista;
          this.paginaActual = resp?.paginaActual || 1;
          this.totalPaginas = resp?.totalPaginas || 1;
          this.totalRegistros = resp?.totalRegistros || this.grados.length;

          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.grados = [];
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarGrados();
    }
  }

  guardarGrado(): void {
    this.limpiarMensajes();

    if (this.gradoForm.invalid) {
      this.gradoForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      this.cdr.markForCheck();
      return;
    }

    this.procesando = true;
    this.cdr.markForCheck();

    const payload: Grado = {
      nivel: this.gradoForm.value.nivel,
      idCarrera: this.esDiversificado ? Number(this.gradoForm.value.idCarrera) : null,
      nombre: String(this.gradoForm.value.nombre).trim(),
      descripcion: this.gradoForm.value.descripcion
    };

    const operacion$ = (this.modoEdicion && this.idSeleccionado)
      ? this.gradoService.actualizarGrado(this.idSeleccionado, payload)
      : this.gradoService.registrarGrado(payload);

    operacion$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (res) => {
        const mensajeDefault = this.modoEdicion ? 'Grado actualizado correctamente.' : 'Grado registrado correctamente.';
        this.successMessage = this.obtenerTextoRespuesta(res) || mensajeDefault;
        this.procesando = false;
        this.resetForm();
        this.cargarGrados();
      },
      error: (err) => {
        this.errorMessage = this.obtenerTextoRespuesta(err);
        this.procesando = false;
        this.cdr.markForCheck();
      }
    });
  }

  editarGrado(grado: Grado): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = grado.idGrado || null;

    this.gradoForm.patchValue({
      nivel: grado.nivel,
      idCarrera: grado.idCarrera,
      nombre: grado.nombre,
      descripcion: grado.descripcion || ''
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
    this.gradoForm.reset({
      nivel: '',
      idCarrera: null,
      nombre: '',
      descripcion: ''
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