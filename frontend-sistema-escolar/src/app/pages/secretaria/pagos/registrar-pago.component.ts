import { Component, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PagoService, Pago } from '../../../services/pago.service';
import { ObligacionService, Obligacion } from '../../../services/obilgacion.service';
import { ComprobantePagoService } from '../../../services/comprobante-pago.service';

@Component({
  selector: 'app-registrar-pago',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './registrar-pago.component.html'
})
export class RegistrarPagoComponent {
  busquedaForm: FormGroup;
  listaObligaciones: Obligacion[] = []; 
  
  buscando: boolean = false;
  procesandoId: number | null = null;
  
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private pagoService: PagoService,
    private obligacionService: ObligacionService,
    private comprobanteService: ComprobantePagoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private location: Location
  ) {
    this.busquedaForm = this.fb.group({
      idEstudiante: ['', [Validators.required, Validators.min(1)]]
    });
  }

  volver(): void {
    this.location.back();
  }

  buscarDeudas(): void {
    if (this.busquedaForm.invalid) return;

    this.buscando = true;
    this.successMessage = '';
    this.errorMessage = '';
    
    const idEstudiante = this.busquedaForm.value.idEstudiante;
    
    this.obligacionService.obtenerPorEstudiante(idEstudiante).subscribe({
      next: (deudas: Obligacion[]) => {
        this.ngZone.run(() => {
          this.listaObligaciones = deudas || [];
          if (this.listaObligaciones.length === 0) {
            this.successMessage = 'El estudiante no tiene obligaciones registradas.';
          }
          this.buscando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = 'Error al consultar las obligaciones del estudiante.';
          this.listaObligaciones = [];
          this.buscando = false;
          this.cdr.detectChanges();
        });
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

    // Paso 1: Registrar el pago en la BD
    this.pagoService.registrarPago(pagoData).subscribe({
      next: (resPago) => {
        const idPagoGenerado = resPago.idPago;

        // Paso 2: Registrar la emisión del comprobante
        this.comprobanteService.emitirComprobante(idPagoGenerado).subscribe({
          next: () => {
            this.ngZone.run(() => {
              this.successMessage = `Pago procesado y Comprobante emitido con éxito.`;
              this.procesandoId = null;
              obligacion.estado = 'PAGADA';
              this.cdr.detectChanges();

              // Paso 3: Visualizar / Abrir el PDF generado por JasperReports
              this.verComprobantePDF(idPagoGenerado);
            });
          },
          error: (errComp) => {
            this.ngZone.run(() => {
              this.successMessage = `Pago registrado exitosamente, pero hubo un detalle al registrar el comprobante.`;
              this.procesandoId = null;
              obligacion.estado = 'PAGADA';
              this.cdr.detectChanges();
              // Intentamos abrir el PDF de todas formas
              this.verComprobantePDF(idPagoGenerado);
            });
          }
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err.error?.mensaje || 'Ocurrió un error al registrar el cobro.';
          this.procesandoId = null;
          this.cdr.detectChanges();
        });
      }
    });
  }

  // Método para descargar/abrir el PDF entregado por el endpoint /pdf/{idPago}
  verComprobantePDF(idPago: number): void {
    this.comprobanteService.obtenerPdfComprobante(idPago).subscribe({
      next: (blobData: Blob) => {
        // Crear una URL temporal para el objeto Blob recibido
        const fileURL = URL.createObjectURL(blobData);
        // Abrir el documento PDF directamente en una pestaña nueva del navegador
        window.open(fileURL, '_blank');
      },
      error: (err) => {
        console.error('Error al descargar el PDF del comprobante:', err);
      }
    });
  }
}