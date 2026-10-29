import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { MultaService, Multa } from '../../../services/multa.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-multa',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './multa.component.html'
})
export class MultaComponent implements OnInit {
  multas: Multa[] = [];
  cargando: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private multaService: MultaService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.cargarMultas();
  }

  cargarMultas(): void {
    this.cargando = true;
    this.multaService.obtenerTodas().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.multas = data || [];
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al obtener el listado de multas.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  pagarMulta(idMulta: number): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.multaService.actualizarEstado(idMulta, 'PAGADA').subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || 'Multa marcada como PAGADA.';
          this.cargarMultas();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al procesar el pago de la multa.';
          this.cdr.detectChanges();
        });
      }
    });
  }
}