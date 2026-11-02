import { Component, OnInit, inject, DestroyRef, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HistorialPagoService, HistorialPago } from '../../../services/historial-pago.service';

@Component({
  selector: 'app-historial-pago',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './historial-pago.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistorialPagoComponent implements OnInit {
  private fb = inject(FormBuilder);
  private historialService = inject(HistorialPagoService);
  private cdr = inject(ChangeDetectorRef);
  private location = inject(Location);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  busquedaForm: FormGroup = this.fb.group({
    idEstudiante: ['', [Validators.required, Validators.min(1)]]
  });

  listaHistorial: HistorialPago[] = [];
  cargando = false;
  descargandoPdf = false;
  esEstudiante = false;

  errorMessage = '';
  infoMessage = '';

  ngOnInit(): void {
    this.errorMessage = '';
    this.infoMessage = '';
    this.listaHistorial = [];
    this.esEstudiante = false;

    const rawUser = localStorage.getItem('usuario') || sessionStorage.getItem('usuario');
    if (rawUser) {
      try {
        const parsed = JSON.parse(rawUser);
        const rolUsuario = String(parsed.rol || parsed.usuario?.rol || '').toUpperCase();

        if (rolUsuario === 'ESTUDIANTE' || rolUsuario === 'ROLE_ESTUDIANTE') {
          this.esEstudiante = true;
          const miId = parsed.idPersona || parsed.idEstudiante || parsed.id;
          if (miId && miId > 0) {
            this.busquedaForm.patchValue({ idEstudiante: miId });
            this.consultar();
          }
        }
      } catch (e) {
        console.error('Error al verificar datos de sesión:', e);
      }
    }
  }

  volver(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/admin/dashboard']);
    }
  }

  consultar(): void {
    if (this.busquedaForm.invalid) return;

    this.cargando = true;
    this.errorMessage = '';
    this.infoMessage = '';
    this.listaHistorial = [];
    this.cdr.markForCheck();

    const { idEstudiante } = this.busquedaForm.value;

    this.historialService.obtenerHistorial(idEstudiante)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: HistorialPago[]) => {
          this.listaHistorial = data || [];
          if (this.listaHistorial.length === 0) {
            this.infoMessage = `El estudiante con ID #${idEstudiante} no posee pagos o recibos registrados en el sistema.`;
          }
          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = err.error?.mensaje || 'Ocurrió un error al consultar el historial del estudiante.';
          this.listaHistorial = [];
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  descargarPdf(): void {
    if (this.busquedaForm.invalid || this.listaHistorial.length === 0) return;

    this.descargandoPdf = true;
    this.cdr.markForCheck();

    const { idEstudiante } = this.busquedaForm.value;

    this.historialService.obtenerPdfHistorial(idEstudiante)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (blobData: Blob) => {
          const fileURL = URL.createObjectURL(blobData);
          window.open(fileURL, '_blank');
          this.descargandoPdf = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.errorMessage = 'Ocurrió un error al generar la vista previa del PDF.';
          this.descargandoPdf = false;
          this.cdr.markForCheck();
        }
      });
  }
}