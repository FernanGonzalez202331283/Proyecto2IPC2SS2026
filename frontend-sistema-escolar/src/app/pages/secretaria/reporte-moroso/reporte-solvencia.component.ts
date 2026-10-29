import { Component, ChangeDetectorRef, NgZone, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ReporteSolvenciaService, ReporteSolvencia } from '../../../services/reporte-solvencia.service';

@Component({
  selector: 'app-reporte-solvencia',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reporte-solvencia.component.html'
})
export class ReporteSolvenciaComponent implements OnInit {
  filtroForm: FormGroup;
  listaReporte: ReporteSolvencia[] = [];
  
  cargando: boolean = false;
  descargandoPdf: boolean = false;
  
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private reporteService: ReporteSolvenciaService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private location: Location
  ) {
    this.filtroForm = this.fb.group({
      idAnioLectivo: [1, [Validators.required, Validators.min(1)]],
      estado: ['TODOS', [Validators.required]]
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
    this.successMessage = '';
    this.errorMessage = '';

    const { idAnioLectivo, estado } = this.filtroForm.value;

    this.reporteService.obtenerReporte(idAnioLectivo, estado).subscribe({
      next: (data: ReporteSolvencia[]) => {
        this.ngZone.run(() => {
          this.listaReporte = data || [];
          if (this.listaReporte.length === 0) {
            this.errorMessage = 'No se encontraron registros con los filtros seleccionados.';
          }
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err.error?.mensaje || 'Error al obtener el reporte de solvencia.';
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
    const { idAnioLectivo, estado } = this.filtroForm.value;

    this.reporteService.obtenerPdfReporte(idAnioLectivo, estado).subscribe({
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