import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
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
  prestamos: Prestamo[] = [];
  librosActivos: Libro[] = [];
  personasActivas: PersonaSeleccionable[] = [];
  prestamoForm!: FormGroup;

  cargando: boolean = false;
  guardando: boolean = false;

  successMessage: string = '';
  errorMessage: string = '';

  paginaActual: number = 1;
  limite: number = 10;
  totalPaginas: number = 0;
  totalRegistros: number = 0;

  constructor(
    private fb: FormBuilder,
    private prestamoService: PrestamoService,
    private libroService: LibroService,
    private location: Location,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
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
        this.ngZone.run(() => {
          this.librosActivos = (data || []).filter(l => l.cantidadDisponible > 0);
          this.cdr.detectChanges();
        });
      },
      error: () => console.error('Error al cargar libros para préstamos.')
    });
  }

  cargarPersonasActivas(): void {
  this.prestamoService.obtenerPersonasActivas().subscribe({
    next: (personas) => {
      this.ngZone.run(() => {
        this.personasActivas = personas || [];
        this.cdr.detectChanges();
      });
    },
    error: (err) => {
      this.ngZone.run(() => {
        console.warn('No se pudo cargar el listado de personas activas:', err);
        this.personasActivas = [];
        this.cdr.detectChanges();
      });
    }
  });
}

  cargarPrestamos(pagina: number = 1): void {
    this.cargando = true;
    this.paginaActual = pagina;

    this.prestamoService.obtenerTodos(this.paginaActual, this.limite).subscribe({
      next: (res: RespuestaPaginada<Prestamo>) => {
        this.ngZone.run(() => {
          this.prestamos = res.contenido || [];
          this.totalRegistros = res.totalRegistros || 0;
          this.totalPaginas = res.totalPaginas || 0;
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al obtener historial de préstamos.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.cargarPrestamos(nuevaPagina);
    }
  }

  obtenerNombrePersona(idPersona: number): string {
    const persona = this.personasActivas.find(p => p.idPersona === idPersona);
    return persona ? persona.nombreCompleto : `Persona #${idPersona}`;
  }

  obtenerTituloLibro(idLibro: number): string {
    const libro = this.librosActivos.find(l => l.idLibro === idLibro);
    return libro ? libro.titulo : `Libro #${idLibro}`;
  }

  guardarPrestamo(): void {
    if (this.prestamoForm.invalid) {
      this.prestamoForm.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.guardando = true;

    const formVal = this.prestamoForm.value;

    const prestamoData: Prestamo = {
      idLibro: Number(formVal.idLibro),
      idPersona: Number(formVal.idPersona),
      fechaPrestamo: formVal.fechaPrestamo,
      fechaDevolucionEsperada: formVal.fechaDevolucionEsperada
    };

    this.prestamoService.registrar(prestamoData).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || 'Préstamo registrado correctamente.';
          this.guardando = false;
          this.initForm();
          this.cargarLibrosDisponibles();
          this.cargarPrestamos(1);
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al registrar el préstamo.';
          this.guardando = false;
          this.cdr.detectChanges();
        });
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