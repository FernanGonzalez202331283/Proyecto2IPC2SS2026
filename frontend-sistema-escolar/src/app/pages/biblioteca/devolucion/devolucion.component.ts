import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PrestamoService, Prestamo, PersonaSeleccionable, RespuestaPaginada } from '../../../services/prestamo.service';

@Component({
  selector: 'app-devolucion',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './devolucion.component.html'
})
export class DevolucionComponent implements OnInit {
  todosLosPrestamos: Prestamo[] = [];
  prestamosFiltrados: Prestamo[] = [];
  personasActivas: PersonaSeleccionable[] = [];
  
  soloPendientes: boolean = true;
  cargando: boolean = false;

  successMessage: string = '';
  errorMessage: string = '';

  paginaActual: number = 1;
  limite: number = 10;
  totalPaginas: number = 0;
  totalRegistros: number = 0;

  constructor(
    private prestamoService: PrestamoService,
    private location: Location,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.cargarPersonas();
    this.cargarPrestamos(1);
  }

  cargarPersonas(): void {
    this.prestamoService.obtenerPersonasActivas().subscribe({
      next: (personas) => {
        this.ngZone.run(() => {
          this.personasActivas = personas || [];
          this.cdr.detectChanges();
        });
      },
      error: () => {
        console.warn('No se pudo cargar el listado auxiliar de personas activas.');
      }
    });
  }

  cargarPrestamos(pagina: number = 1): void {
    this.cargando = true;
    this.paginaActual = pagina;

    this.prestamoService.obtenerTodos(this.paginaActual, this.limite).subscribe({
      next: (res: RespuestaPaginada<Prestamo>) => {
        this.ngZone.run(() => {
          this.todosLosPrestamos = res.contenido || [];
          this.totalRegistros = res.totalRegistros || 0;
          this.totalPaginas = res.totalPaginas || 0;
          this.aplicarFiltro();
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al obtener los préstamos para devolución.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  aplicarFiltro(): void {
    if (this.soloPendientes) {
      this.prestamosFiltrados = this.todosLosPrestamos.filter(
        p => p.estado === 'PRESTADO' || p.estado === 'VENCIDO'
      );
    } else {
      this.prestamosFiltrados = [...this.todosLosPrestamos];
    }
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.cargarPrestamos(nuevaPagina);
    }
  }

  obtenerNombrePersona(p: Prestamo): string {
    if (p.nombrePersona && p.nombrePersona.trim() !== '') {
      return p.nombrePersona;
    }
    const persona = this.personasActivas.find(item => item.idPersona === p.idPersona);
    return persona ? persona.nombreCompleto : `Persona #${p.idPersona}`;
  }

  devolverLibro(idPrestamo: number): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.prestamoService.registrarDevolucion(idPrestamo).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || 'Devolución registrada correctamente.';
          this.cargarPrestamos(this.paginaActual);
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'No se pudo procesar la devolución. El préstamo podría haber sido devuelto anteriormente.';
          this.cargarPrestamos(this.paginaActual);
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