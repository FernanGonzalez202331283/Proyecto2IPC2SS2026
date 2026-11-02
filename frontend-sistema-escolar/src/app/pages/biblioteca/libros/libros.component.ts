import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule, Location } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { LibroService, Libro, RespuestaPaginada } from '../../../services/libro.service';

@Component({
  selector: 'app-libros',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterLink],
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

  // Control de paginación
  paginaActual: number = 1;
  limite: number = 10;
  totalPaginas: number = 0;
  totalRegistros: number = 0;

  constructor(
    private fb: FormBuilder,
    private libroService: LibroService,
    private location: Location,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarLibros(1);
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

  cargarLibros(pagina: number = 1): void {
    this.cargando = true;
    this.paginaActual = pagina;

    this.libroService.obtenerTodos(this.paginaActual, this.limite).subscribe({
      next: (res: RespuestaPaginada<Libro>) => {
        this.ngZone.run(() => {
          this.libros = res.contenido || [];
          this.totalRegistros = res.totalRegistros || 0;
          this.totalPaginas = res.totalPaginas || 0;
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

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.cargarLibros(nuevaPagina);
    }
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
      estado: true
    };

    if (this.esEdicion && this.idLibroEditar) {
      libroData.idLibro = this.idLibroEditar;

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
            this.cargarLibros(this.paginaActual);
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
            this.cargarLibros(1);
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
          this.cargarLibros(this.paginaActual);
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

  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/']);
    }
  }
}