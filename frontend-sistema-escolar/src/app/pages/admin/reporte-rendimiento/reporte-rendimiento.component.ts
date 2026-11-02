import { Component, OnInit, inject, DestroyRef, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReporteRendimientoService, ReporteRendimiento } from '../../../../app/services/reporte-rendimiento';

@Component({
  selector: 'app-reporte-rendimiento',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reporte-rendimiento.component.html'
})
export class ReporteRendimientoComponent implements OnInit {
  private fb = inject(FormBuilder);
  private reporteService = inject(ReporteRendimientoService);
  private cdr = inject(ChangeDetectorRef);
  private location = inject(Location);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  filtroForm: FormGroup = this.fb.group({
    idAnioLectivo: [1, [Validators.required, Validators.min(1)]]
  });

  listaReporte: ReporteRendimiento[] = [];
  cargando = false;
  descargandoPdf = false;
  errorMessage = '';

  ngOnInit(): void {
    this.consultar();
  }

  volver(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/admin/dashboard']);
    }
  }

  consultar(): void {
    if (this.filtroForm.invalid) return;

    this.cargando = true;
    this.errorMessage = '';
    this.cdr.markForCheck();

    const { idAnioLectivo } = this.filtroForm.value;

    this.reporteService.obtenerReporte(idAnioLectivo)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: ReporteRendimiento[]) => {
          this.listaReporte = data || [];
          if (this.listaReporte.length === 0) {
            this.errorMessage = 'No se encontraron registros de rendimiento académico para este año.';
          }
          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = err.error?.mensaje || 'Error al obtener el reporte de rendimiento.';
          this.listaReporte = [];
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  verReportePDF(): void {
    if (this.filtroForm.invalid) return;

    this.descargandoPdf = true;
    this.cdr.markForCheck();

    const { idAnioLectivo } = this.filtroForm.value;

    this.reporteService.obtenerPdfReporte(idAnioLectivo)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (blobData: Blob) => {
          const fileURL = URL.createObjectURL(blobData);
          window.open(fileURL, '_blank');
          this.descargandoPdf = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.errorMessage = 'Ocurrió un error al generar el PDF del reporte.';
          this.descargandoPdf = false;
          this.cdr.markForCheck();
        }
      });
  }
}