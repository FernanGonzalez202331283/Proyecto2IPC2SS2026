import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LibroService, Libro } from '../../../services/libro.service';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './catalogo.component.html'
})
export class CatalogoComponent implements OnInit {
  librosCatalogo: Libro[] = [];
  librosFiltrados: Libro[] = [];

  filtroBusqueda: string = '';
  soloDisponibles: boolean = false;
  cargando: boolean = false;
  errorMessage: string = '';

  constructor(
    private libroService: LibroService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.cargarCatalogo();
  }

  cargarCatalogo(): void {
    this.cargando = true;
    this.errorMessage = '';

    this.libroService.obtenerCatalogo().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.librosCatalogo = data || [];
          this.aplicarFiltros();
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al consultar el catálogo de libros.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  aplicarFiltros(): void {
    const termino = this.filtroBusqueda.toLowerCase().trim();

    this.librosFiltrados = this.librosCatalogo.filter((libro) => {
      const coincideBusqueda =
        !termino ||
        libro.titulo.toLowerCase().includes(termino) ||
        libro.autor.toLowerCase().includes(termino) ||
        (libro.editorial && libro.editorial.toLowerCase().includes(termino));

      const coincideStock = !this.soloDisponibles || libro.cantidadDisponible > 0;

      return coincideBusqueda && coincideStock;
    });
  }
}