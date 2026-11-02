import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LibroService, Libro } from '../../../services/libro.service';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './catalogo.component.html'
})
export class CatalogoComponent implements OnInit {
  librosCatalogo = signal<Libro[]>([]);
  filtroBusqueda = signal<string>('');
  soloDisponibles = signal<boolean>(false);
  
  cargando = signal<boolean>(false);
  errorMessage = signal<string>('');

  librosFiltrados = computed(() => {
    const termino = this.filtroBusqueda().toLowerCase().trim();
    const soloDisp = this.soloDisponibles();
    const catalogo = this.librosCatalogo();

    return catalogo.filter((libro) => {
      const coincideBusqueda =
        !termino ||
        libro.titulo.toLowerCase().includes(termino) ||
        libro.autor.toLowerCase().includes(termino) ||
        (libro.editorial && libro.editorial.toLowerCase().includes(termino));

      const coincideStock = !soloDisp || libro.cantidadDisponible > 0;

      return coincideBusqueda && coincideStock;
    });
  });

  constructor(
    private libroService: LibroService,
    private location: Location,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarCatalogo();
  }

  cargarCatalogo(): void {
    this.cargando.set(true);
    this.errorMessage.set('');

    this.libroService.obtenerCatalogo().subscribe({
      next: (data) => {
        this.librosCatalogo.set(data || []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.mensaje || 'Error al consultar el catálogo de libros.');
        this.cargando.set(false);
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