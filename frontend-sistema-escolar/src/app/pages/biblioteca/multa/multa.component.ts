import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { MultaService, Multa, RespuestaPaginada } from '../../../services/multa.service';
import { CommonModule } from '@angular/common';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
@Component({
  selector: 'app-multa',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './multa.component.html'
})
export class MultaComponent implements OnInit {
  multas: Multa[] = [];
  cargando: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  paginaActual: number = 1;
  limite: number = 10;
  totalPaginas: number = 0;
  totalRegistros: number = 0;

  constructor(
    private multaService: MultaService,
    private cdr: ChangeDetectorRef,
    private location: Location,
    private router: Router,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.cargarMultas(1);
  }

  cargarMultas(pagina: number = 1): void {
    this.cargando = true;
    this.paginaActual = pagina;

    this.multaService.obtenerTodas(this.paginaActual, this.limite).subscribe({
      next: (res: RespuestaPaginada<Multa>) => {
        this.ngZone.run(() => {
          this.multas = res.contenido || [];
          this.totalRegistros = res.totalRegistros || 0;
          this.totalPaginas = res.totalPaginas || 0;
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al obtener el listado de multas.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.cargarMultas(nuevaPagina);
    }
  }

  pagarMulta(idMulta: number): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.multaService.actualizarEstado(idMulta, 'PAGADA').subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || 'Multa marcada como PAGADA.';
          this.cargarMultas(this.paginaActual);
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al procesar el pago de la multa.';
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