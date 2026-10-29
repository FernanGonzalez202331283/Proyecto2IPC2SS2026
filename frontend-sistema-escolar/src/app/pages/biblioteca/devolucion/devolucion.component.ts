import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PrestamoService, Prestamo } from '../../../services/prestamo.service';

@Component({
  selector: 'app-devolucion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './devolucion.component.html'
})
export class DevolucionComponent implements OnInit {
  todosLosPrestamos: Prestamo[] = [];
  prestamosFiltrados: Prestamo[] = [];
  
  soloPendientes: boolean = true;
  cargando: boolean = false;

  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private prestamoService: PrestamoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.cargarPrestamos();
  }

  cargarPrestamos(): void {
    this.cargando = true;
    this.prestamoService.obtenerTodos().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.todosLosPrestamos = data || [];
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

  devolverLibro(idPrestamo: number): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.prestamoService.registrarDevolucion(idPrestamo).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || 'Devolución registrada correctamente.';
          this.cargarPrestamos();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al registrar la devolución del libro.';
          this.cdr.detectChanges();
        });
      }
    });
  }
}