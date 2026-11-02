import { Component, OnInit, inject, DestroyRef, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReporteSolvenciaService, ReporteSolvencia } from '../../../services/reporte-solvencia.service';

@Component({
  selector: 'app-reporte-solvencia',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reporte-solvencia.component.html'
})
export class ReporteSolvenciaComponent implements OnInit {
  private fb = inject(FormBuilder);
  private reporteService = inject(ReporteSolvenciaService);
  private cdr = inject(ChangeDetectorRef);
  private location = inject(Location);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  filtroForm: FormGroup = this.fb.group({
    idAnioLectivo: [1, [Validators.required, Validators.min(1)]],
    estado: ['TODOS', [Validators.required]]
  });

  listaReporte: ReporteSolvencia[] = [];
  cargando = false;
  descargandoPdf = false;
  successMessage = '';
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
    this.successMessage = '';
    this.errorMessage = '';
    this.cdr.markForCheck();

    const { idAnioLectivo, estado } = this.filtroForm.value;

    this.reporteService.obtenerReporte(idAnioLectivo, estado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: ReporteSolvencia[]) => {
          this.listaReporte = data || [];
          if (this.listaReporte.length === 0) {
            this.errorMessage = 'No se encontraron registros con los filtros seleccionados.';
          }
          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = err.error?.mensaje || 'Error al obtener el reporte de solvencia.';
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

    const { idAnioLectivo, estado } = this.filtroForm.value;

    this.reporteService.obtenerPdfReporte(idAnioLectivo, estado)
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