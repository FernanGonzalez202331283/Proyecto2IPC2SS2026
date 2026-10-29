import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CursoService, Curso } from '../../../services/curso.services';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-cursos',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './curso.component.html'
})
export class CursosComponent implements OnInit {
  cursos: Curso[] = [];
  cursoForm!: FormGroup;
  modoEdicion: boolean = false;
  idSeleccionado: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private cursoService: CursoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

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
    this.cursoService.obtenerCursos().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.cursos = data || [];
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

  guardarCurso(): void {
    this.limpiarMensajes();

    if (this.cursoForm.invalid) {
      this.cursoForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    const datos: Curso = this.cursoForm.value;

    if (this.modoEdicion && this.idSeleccionado) {
      this.cursoService.actualizarCurso(this.idSeleccionado, datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Curso actualizado correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarCursos();
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
      this.cursoService.registrarCurso(datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Curso registrado correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarCursos();
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

  toggleEstado(curso: Curso): void {
    if (!curso.idCurso) return;

    this.limpiarMensajes();
    const nuevoEstado = !curso.estado;

    this.cursoService.cambiarEstado(curso.idCurso, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
          curso.estado = nuevoEstado;
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

  editarCurso(curso: Curso): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = curso.idCurso || null;

    this.cursoForm.patchValue({
      nombre: curso.nombre,
      descripcion: curso.descripcion || '',
      estado: curso.estado
    });
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