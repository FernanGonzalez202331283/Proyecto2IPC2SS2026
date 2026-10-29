import { Component, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SolvenciaService, Solvencia } from '../../../services/solvencia.service';

@Component({
  selector: 'app-generar-solvencia',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './generar-solvencia.component.html'
})
export class GenerarSolvenciaComponent {
  solvenciaForm: FormGroup;
  
  consultando: boolean = false;
  descargandoPdf: boolean = false;
  
  resultadoSolvencia: Solvencia | null = null;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private solvenciaService: SolvenciaService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private location: Location
  ) {
    // Por defecto colocamos el ciclo 1 (2026)
    this.solvenciaForm = this.fb.group({
      idEstudiante: ['', [Validators.required, Validators.min(1)]],
      idAñoLectivo: [1, [Validators.required, Validators.min(1)]]
    });
  }

  volver(): void {
    this.location.back();
  }

  verificarSolvencia(): void {
    if (this.solvenciaForm.invalid) return;

    this.consultando = true;
    this.resultadoSolvencia = null;
    this.errorMessage = '';

    const { idEstudiante, idAñoLectivo } = this.solvenciaForm.value;

    this.solvenciaService.consultarSolvencia(idEstudiante, idAñoLectivo).subscribe({
      next: (res: Solvencia) => {
        this.ngZone.run(() => {
          this.resultadoSolvencia = res;
          this.consultando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err.error?.mensaje || 'No se pudo verificar la solvencia del estudiante.';
          this.consultando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  generarConstanciaPDF(): void {
    if (!this.resultadoSolvencia || this.resultadoSolvencia.estado !== 'SOLVENTE') return;

    this.descargandoPdf = true;
    const { idEstudiante, idAñoLectivo } = this.solvenciaForm.value;

    this.solvenciaService.descargarPdfSolvencia(idEstudiante, idAñoLectivo).subscribe({
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
          this.errorMessage = 'Ocurrió un error al generar la constancia en PDF.';
          this.descargandoPdf = false;
          this.cdr.detectChanges();
        });
      }
    });
  }
}