import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LibroService, Libro } from '../../../services/libro.service';

@Component({
  selector: 'app-libros',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, RouterLink],
  templateUrl: './libros.component.html'
})
export class LibrosComponent implements OnInit {
  libros: Libro[] = [];
  libroForm!: FormGroup;

  esEdicion: boolean = false;
  idLibroEditar: number | null = null;

  cargando: boolean = false;
  guardando: boolean = false;

  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private libroService: LibroService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarLibros();
  }

  initForm(): void {
    this.libroForm = this.fb.group({
      titulo: ['', [Validators.required, Validators.maxLength(200)]],
      autor: ['', [Validators.required, Validators.maxLength(150)]],
      editorial: ['', [Validators.maxLength(150)]],
      cantidadTotal: [1, [Validators.required, Validators.min(1)]],
      cantidadDisponible: [1, [Validators.required, Validators.min(0)]]
    });
  }

  cargarLibros(): void {
    this.cargando = true;
    this.libroService.obtenerTodos().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.libros = data || [];
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al cargar los libros de la biblioteca.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  guardarLibro(): void {
    if (this.libroForm.invalid) {
      this.libroForm.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.guardando = true;

    const formVal = this.libroForm.value;
    
    // Validación lógica de cantidades
    if (Number(formVal.cantidadDisponible) > Number(formVal.cantidadTotal)) {
      this.errorMessage = 'La cantidad disponible no puede superar la cantidad total.';
      this.guardando = false;
      return;
    }

    const libroData: Libro = {
      titulo: formVal.titulo,
      autor: formVal.autor,
      editorial: formVal.editorial ? formVal.editorial : undefined,
      cantidadTotal: Number(formVal.cantidadTotal),
      cantidadDisponible: Number(formVal.cantidadDisponible),
      estado: true // Se establece activo por defecto al registrar
    };

    if (this.esEdicion && this.idLibroEditar) {
      libroData.idLibro = this.idLibroEditar;

      // Preserva el estado original del registro que se está editando
      const libroExistente = this.libros.find(l => l.idLibro === this.idLibroEditar);
      if (libroExistente) {
        libroData.estado = libroExistente.estado;
      }

      this.libroService.actualizar(this.idLibroEditar, libroData).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = res?.mensaje || 'Libro actualizado correctamente.';
            this.guardando = false;
            this.limpiarFormulario();
            this.cargarLibros();
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.errorMessage = err?.error?.mensaje || err?.error || 'Error al actualizar el libro.';
            this.guardando = false;
            this.cdr.detectChanges();
          });
        }
      });
    } else {
      this.libroService.registrar(libroData).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = res?.mensaje || 'Libro registrado correctamente.';
            this.guardando = false;
            this.limpiarFormulario();
            this.cargarLibros();
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.errorMessage = err?.error?.mensaje || err?.error || 'Error al registrar el libro.';
            this.guardando = false;
            this.cdr.detectChanges();
          });
        }
      });
    }
  }

  seleccionarParaEditar(libro: Libro): void {
    this.esEdicion = true;
    this.idLibroEditar = libro.idLibro || null;
    this.libroForm.patchValue({
      titulo: libro.titulo,
      autor: libro.autor,
      editorial: libro.editorial || '',
      cantidadTotal: libro.cantidadTotal,
      cantidadDisponible: libro.cantidadDisponible
    });
  }

  cambiarEstado(libro: Libro): void {
    if (!libro.idLibro) return;
    const nuevoEstado = !libro.estado;

    this.libroService.cambiarEstado(libro.idLibro, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || 'Estado actualizado correctamente.';
          this.cargarLibros();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al cambiar estado del libro.';
          this.cdr.detectChanges();
        });
      }
    });
  }

  limpiarFormulario(): void {
    this.esEdicion = false;
    this.idLibroEditar = null;
    this.libroForm.reset({
      titulo: '',
      autor: '',
      editorial: '',
      cantidadTotal: 1,
      cantidadDisponible: 1
    });
  }
}