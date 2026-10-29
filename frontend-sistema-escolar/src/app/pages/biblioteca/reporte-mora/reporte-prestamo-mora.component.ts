import { Component, ChangeDetectorRef, NgZone, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReportePrestamoMoraService, ReportePrestamoMora } from '../../../services/reporte-prestamo-mora.service';

@Component({
  selector: 'app-reporte-prestamo-mora',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './reporte-prestamo-mora.component.html'
})
export class ReportePrestamoMoraComponent implements OnInit {
  listaMora: ReportePrestamoMora[] = [];
  cargando: boolean = false;
  descargandoPdf: boolean = false;
  errorMessage: string = '';

  constructor(
    private reporteService: ReportePrestamoMoraService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.cargarReporte();
  }

  volver(): void {
    this.location.back();
  }

  cargarReporte(): void {
    this.cargando = true;
    this.errorMessage = '';

    this.reporteService.obtenerReporte().subscribe({
      next: (data: ReportePrestamoMora[]) => {
        this.ngZone.run(() => {
          this.listaMora = data || [];
          if (this.listaMora.length === 0) {
            this.errorMessage = 'No se encontraron préstamos fuera de plazo o en mora actualmente.';
          }
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err.error?.mensaje || 'Error al obtener el reporte de morosidad de biblioteca.';
          this.listaMora = [];
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  descargarPdf(): void {
    if (this.listaMora.length === 0) return;

    this.descargandoPdf = true;

    this.reporteService.obtenerPdfReporte().subscribe({
      next: (blobData: Blob) => {
        this.ngZone.run(() => {
          const fileURL = URL.createObjectURL(blobData);
          window.open(fileURL, '_blank');
          this.descargandoPdf = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = 'Ocurrió un error al generar el archivo PDF de morosidad.';
          this.descargandoPdf = false;
          this.cdr.detectChanges();
        });
      }
    });
  }
}