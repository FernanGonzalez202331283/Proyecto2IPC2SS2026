import { 
  Component, 
  OnInit, 
  ChangeDetectorRef, 
  ChangeDetectionStrategy, 
  DestroyRef, 
  inject 
} from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { PagoService, Pago } from '../../../services/pago.service';
import { ObligacionService, Obligacion } from '../../../services/obilgacion.service';
import { ComprobantePagoService } from '../../../services/comprobante-pago.service';
import { EstudianteService, Estudiante } from '../../../services/estudiante.service';

@Component({
  selector: 'app-registrar-pago',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './registrar-pago.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegistrarPagoComponent implements OnInit {
  private pagoService = inject(PagoService);
  private obligacionService = inject(ObligacionService);
  private comprobanteService = inject(ComprobantePagoService);
  private estudianteService = inject(EstudianteService);
  private cdr = inject(ChangeDetectorRef);
  private location = inject(Location);
  private destroyRef = inject(DestroyRef);

  estudiantesLista: Estudiante[] = [];
  estudianteSeleccionado: Estudiante | null = null;
  idEstudianteSeleccionado: number | null = null;

  listaObligaciones: Obligacion[] = []; 
  cargandoEstudiantes = false;
  buscandoDeudas = false;
  procesandoId: number | null = null;
  
  successMessage = '';
  errorMessage = '';

  ngOnInit(): void {
    this.cargarEstudiantes();
  }

  volver(): void {
    if (window.history.length > 1) {
      this.location.back();
    }
  }

  cargarEstudiantes(): void {
    this.cargandoEstudiantes = true;
    this.cdr.markForCheck();

    this.estudianteService.obtenerEstudiantes(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) ? resp : (resp?.contenido || []);
          this.estudiantesLista = lista.filter((e: Estudiante) => e.estado === 'ACTIVO');
          this.cargandoEstudiantes = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = 'Error al cargar el catálogo de estudiantes.';
          this.cargandoEstudiantes = false;
          this.cdr.markForCheck();
        }
      });
  }

  onSeleccionarEstudiante(): void {
    this.successMessage = '';
    this.errorMessage = '';
    this.listaObligaciones = [];

    if (!this.idEstudianteSeleccionado) {
      this.estudianteSeleccionado = null;
      this.cdr.markForCheck();
      return;
    }

    const id = Number(this.idEstudianteSeleccionado);
    this.estudianteSeleccionado = this.estudiantesLista.find(e => Number(e.idEstudiante) === id) || null;
    
    this.buscarDeudas(id);
  }

  buscarDeudas(idEstudiante: number): void {
    this.buscandoDeudas = true;
    this.cdr.markForCheck();

    this.obligacionService.obtenerPorEstudiante(idEstudiante)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (deudas: Obligacion[]) => {
          this.listaObligaciones = deudas || [];
          if (this.listaObligaciones.length === 0) {
            this.successMessage = 'El estudiante seleccionado no tiene obligaciones pendientes ni registradas.';
          }
          this.buscandoDeudas = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = err.error?.mensaje || 'Error al consultar las obligaciones del estudiante.';
          this.listaObligaciones = [];
          this.buscandoDeudas = false;
          this.cdr.markForCheck();
        }
      });
  }

  registrarPago(obligacion: Obligacion): void {
    this.procesandoId = obligacion.idObligacion;
    this.successMessage = '';
    this.errorMessage = '';

    const pagoData: Pago = {
      idObligacion: obligacion.idObligacion,
      concepto: obligacion.concepto,
      monto: obligacion.monto
    };

    this.pagoService.registrarPago(pagoData)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resPago: Pago) => {
          const idPagoGenerado = resPago.idPago;

          this.successMessage = `Pago procesado y Comprobante emitido con éxito.`;
          this.procesandoId = null;
          
          obligacion.estado = 'PAGADA';
          if (idPagoGenerado) {
            obligacion.idPago = idPagoGenerado;
            this.verComprobantePDF(idPagoGenerado);
          }

          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = err.error?.mensaje || 'Ocurrió un error al registrar el cobro.';
          this.procesandoId = null;
          this.cdr.markForCheck();
        }
      });
  }

  verComprobantePDF(idPago?: number): void {
    if (!idPago || idPago <= 0) {
      this.errorMessage = 'Identificador de comprobante no disponible.';
      this.cdr.markForCheck();
      return;
    }

    this.comprobanteService.obtenerPdfComprobante(idPago)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (blobData: Blob) => {
          const fileURL = URL.createObjectURL(blobData);
          window.open(fileURL, '_blank');
        },
        error: (err) => {
          console.error('Error al descargar el PDF del comprobante:', err);
          this.errorMessage = 'No se pudo generar el archivo PDF del comprobante.';
          this.cdr.markForCheck();
        }
      });
  }
}