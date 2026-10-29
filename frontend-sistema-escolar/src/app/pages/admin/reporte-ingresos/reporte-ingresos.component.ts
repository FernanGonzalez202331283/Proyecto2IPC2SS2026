import { Component, ChangeDetectorRef, NgZone, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReporteIngresosService, ReporteIngresos } from '../../../services/repote-ingresos.service';

@Component({
  selector: 'app-reporte-ingresos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './reporte-ingresos.component.html'
})
export class ReporteIngresosComponent implements OnInit {
  reporte: ReporteIngresos | null = null;
  cargando: boolean = false;
  descargandoPdf: boolean = false;
  errorMessage: string = '';

  constructor(
    private reporteService: ReporteIngresosService,
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
      next: (data: ReporteIngresos) => {
        this.ngZone.run(() => {
          this.reporte = data;
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err.error?.mensaje || 'Error al obtener el reporte general de ingresos.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  descargarPdf(): void {
    if (!this.reporte) return;

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
          this.errorMessage = 'Ocurrió un error al generar el PDF del reporte de ingresos.';
          this.descargandoPdf = false;
          this.cdr.detectChanges();
        });
      }
    });
  }
}