import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule, Location } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { PrestamoService, Prestamo, PersonaSeleccionable, RespuestaPaginada } from '../../../services/prestamo.service';
import { LibroService, Libro } from '../../../services/libro.service';

@Component({
  selector: 'app-prestamo',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterLink],
  templateUrl: './prestamo.component.html'
})
export class PrestamoComponent implements OnInit {
  prestamos = signal<Prestamo[]>([]);
  librosActivos = signal<Libro[]>([]);
  personasActivas = signal<PersonaSeleccionable[]>([]);
  
  prestamoForm!: FormGroup;

  cargando = signal<boolean>(false);
  guardando = signal<boolean>(false);

  successMessage = signal<string>('');
  errorMessage = signal<string>('');

  paginaActual = signal<number>(1);
  limite = signal<number>(10);
  totalPaginas = signal<number>(0);
  totalRegistros = signal<number>(0);

  constructor(
    private fb: FormBuilder,
    private prestamoService: PrestamoService,
    private libroService: LibroService,
    private location: Location,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarLibrosDisponibles();
    this.cargarPersonasActivas();
    this.cargarPrestamos(1);
  }

  initForm(): void {
    const hoy = new Date().toISOString().split('T')[0];
    this.prestamoForm = this.fb.group({
      idLibro: [null, [Validators.required]],
      idPersona: [null, [Validators.required]],
      fechaPrestamo: [hoy, [Validators.required]],
      fechaDevolucionEsperada: ['', [Validators.required]]
    });
  }

  cargarLibrosDisponibles(): void {
    this.libroService.obtenerCatalogo().subscribe({
      next: (data) => {
        this.librosActivos.set((data || []).filter(l => l.cantidadDisponible > 0));
      },
      error: () => console.error('Error al cargar libros para préstamos.')
    });
  }

  cargarPersonasActivas(): void {
    this.prestamoService.obtenerPersonasActivas().subscribe({
      next: (personas) => {
        this.personasActivas.set(personas || []);
      },
      error: (err) => {
        console.warn('No se pudo cargar el listado de personas activas:', err);
        this.personasActivas.set([]);
      }
    });
  }

  cargarPrestamos(pagina: number = 1): void {
    this.cargando.set(true);
    this.paginaActual.set(pagina);

    this.prestamoService.obtenerTodos(this.paginaActual(), this.limite()).subscribe({
      next: (res: RespuestaPaginada<Prestamo>) => {
        this.prestamos.set(res.contenido || []);
        this.totalRegistros.set(res.totalRegistros || 0);
        this.totalPaginas.set(res.totalPaginas || 0);
        this.cargando.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.mensaje || 'Error al obtener historial de préstamos.');
        this.cargando.set(false);
      }
    });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas()) {
      this.cargarPrestamos(nuevaPagina);
    }
  }

  obtenerNombrePersona(idPersona: number): string {
    const persona = this.personasActivas().find(p => p.idPersona === idPersona);
    return persona ? persona.nombreCompleto : `Persona #${idPersona}`;
  }

  obtenerTituloLibro(idLibro: number): string {
    const libro = this.librosActivos().find(l => l.idLibro === idLibro);
    return libro ? libro.titulo : `Libro #${idLibro}`;
  }

  guardarPrestamo(): void {
    if (this.prestamoForm.invalid) {
      this.prestamoForm.markAllAsTouched();
      return;
    }

    this.errorMessage.set('');
    this.successMessage.set('');
    this.guardando.set(true);

    const formVal = this.prestamoForm.value;

    const prestamoData: Prestamo = {
      idLibro: Number(formVal.idLibro),
      idPersona: Number(formVal.idPersona),
      fechaPrestamo: formVal.fechaPrestamo,
      fechaDevolucionEsperada: formVal.fechaDevolucionEsperada
    };

    this.prestamoService.registrar(prestamoData).subscribe({
      next: (res) => {
        this.successMessage.set(res?.mensaje || 'Préstamo registrado correctamente.');
        this.guardando.set(false);
        this.initForm();
        this.cargarLibrosDisponibles();
        this.cargarPrestamos(1);
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.mensaje || 'Error al registrar el préstamo.');
        this.guardando.set(false);
      }
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