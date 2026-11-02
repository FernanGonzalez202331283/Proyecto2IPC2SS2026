import { Component, OnInit, signal } from '@angular/core';
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
  libros = signal<Libro[]>([]);
  
  esEdicion = signal<boolean>(false);
  idLibroEditar = signal<number | null>(null);

  cargando = signal<boolean>(false);
  guardando = signal<boolean>(false);

  successMessage = signal<string>('');
  errorMessage = signal<string>('');

  paginaActual = signal<number>(1);
  limite = signal<number>(10);
  totalPaginas = signal<number>(0);
  totalRegistros = signal<number>(0);

  libroForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private libroService: LibroService,
    private location: Location,
    private router: Router
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
    this.cargando.set(true);
    this.paginaActual.set(pagina);

    this.libroService.obtenerTodos(this.paginaActual(), this.limite()).subscribe({
      next: (res: RespuestaPaginada<Libro>) => {
        this.libros.set(res.contenido || []);
        this.totalRegistros.set(res.totalRegistros || 0);
        this.totalPaginas.set(res.totalPaginas || 0);
        this.cargando.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.mensaje || 'Error al cargar los libros de la biblioteca.');
        this.cargando.set(false);
      }
    });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas()) {
      this.cargarLibros(nuevaPagina);
    }
  }

  guardarLibro(): void {
    if (this.libroForm.invalid) {
      this.libroForm.markAllAsTouched();
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    this.guardando.set(true);

    const formVal = this.libroForm.value;
    
    if (Number(formVal.cantidadDisponible) > Number(formVal.cantidadTotal)) {
      this.errorMessage.set('La cantidad disponible no puede superar la cantidad total.');
      this.guardando.set(false);
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

    if (this.esEdicion() && this.idLibroEditar()) {
      const id = this.idLibroEditar()!;
      libroData.idLibro = id;

      const libroExistente = this.libros().find(l => l.idLibro === id);
      if (libroExistente) {
        libroData.estado = libroExistente.estado;
      }

      this.libroService.actualizar(id, libroData).subscribe({
        next: (res) => {
          this.successMessage.set(res?.mensaje || 'Libro actualizado correctamente.');
          this.guardando.set(false);
          this.limpiarFormulario();
          this.cargarLibros(this.paginaActual());
        },
        error: (err) => {
          this.errorMessage.set(err?.error?.mensaje || err?.error || 'Error al actualizar el libro.');
          this.guardando.set(false);
        }
      });
    } else {
      this.libroService.registrar(libroData).subscribe({
        next: (res) => {
          this.successMessage.set(res?.mensaje || 'Libro registrado correctamente.');
          this.guardando.set(false);
          this.limpiarFormulario();
          this.cargarLibros(1);
        },
        error: (err) => {
          this.errorMessage.set(err?.error?.mensaje || err?.error || 'Error al registrar el libro.');
          this.guardando.set(false);
        }
      });
    }
  }

  seleccionarParaEditar(libro: Libro): void {
    this.esEdicion.set(true);
    this.idLibroEditar.set(libro.idLibro || null);
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
        this.successMessage.set(res?.mensaje || 'Estado actualizado correctamente.');
        this.cargarLibros(this.paginaActual());
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.mensaje || 'Error al cambiar estado del libro.');
      }
    });
  }

  limpiarFormulario(): void {
    this.esEdicion.set(false);
    this.idLibroEditar.set(null);
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