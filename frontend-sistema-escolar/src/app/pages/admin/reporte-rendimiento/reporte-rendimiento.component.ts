import { Component, ChangeDetectorRef, NgZone, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ReporteRendimientoService, ReporteRendimiento } from '../../../../app/services/reporte-rendimiento';

@Component({
  selector: 'app-reporte-rendimiento',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reporte-rendimiento.component.html'
})
export class ReporteRendimientoComponent implements OnInit {
  filtroForm: FormGroup;
  listaReporte: ReporteRendimiento[] = [];
  
  cargando: boolean = false;
  descargandoPdf: boolean = false;
  
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private reporteService: ReporteRendimientoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private location: Location
  ) {
    this.filtroForm = this.fb.group({
      idAnioLectivo: [1, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.consultar();
  }

  volver(): void {
    this.location.back();
  }

  consultar(): void {
    if (this.filtroForm.invalid) return;

    this.cargando = true;
    this.errorMessage = '';

    const { idAnioLectivo } = this.filtroForm.value;

    this.reporteService.obtenerReporte(idAnioLectivo).subscribe({
      next: (data: ReporteRendimiento[]) => {
        this.ngZone.run(() => {
          this.listaReporte = data || [];
          if (this.listaReporte.length === 0) {
            this.errorMessage = 'No se encontraron registros de rendimiento académico para este año.';
          }
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err.error?.mensaje || 'Error al obtener el reporte de rendimiento.';
          this.listaReporte = [];
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  verReportePDF(): void {
    if (this.filtroForm.invalid) return;

    this.descargandoPdf = true;
    const { idAnioLectivo } = this.filtroForm.value;

    this.reporteService.obtenerPdfReporte(idAnioLectivo).subscribe({
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
          this.errorMessage = 'Ocurrió un error al generar el PDF del reporte.';
          this.descargandoPdf = false;
          this.cdr.detectChanges();
        });
      }
    });
  }
}