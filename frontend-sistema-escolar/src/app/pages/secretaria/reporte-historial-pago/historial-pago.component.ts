import { Component, ChangeDetectorRef, NgZone, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HistorialPagoService, HistorialPago } from '../../../services/historial-pago.service';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-historial-pago',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './historial-pago.component.html'
})
export class HistorialPagoComponent implements OnInit {
  busquedaForm: FormGroup;
  listaHistorial: HistorialPago[] = [];
  
  cargando: boolean = false;
  descargandoPdf: boolean = false;
  esEstudiante: boolean = false;
  
  errorMessage: string = '';
  infoMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private historialService: HistorialPagoService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private location: Location
  ) {
    this.busquedaForm = this.fb.group({
      idEstudiante: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    // 1. Limpiar todos los mensajes e historial al cargar la vista
    this.errorMessage = '';
    this.infoMessage = '';
    this.listaHistorial = [];
    this.esEstudiante = false;

    // 2. Determinar el rol del usuario conectado
    const rawUser = localStorage.getItem('usuario') || sessionStorage.getItem('usuario');
    if (rawUser) {
      try {
        const parsed = JSON.parse(rawUser);
        const rolUsuario = String(parsed.rol || parsed.usuario?.rol || '').toUpperCase();

        // Si es ESTUDIANTE, se consulta automáticamente su propio historial
        if (rolUsuario === 'ESTUDIANTE' || rolUsuario === 'ROLE_ESTUDIANTE') {
          this.esEstudiante = true;
          const miId = parsed.idPersona || this.authService.getIdPersona() || parsed.id;
          if (miId && miId > 0) {
            this.busquedaForm.patchValue({ idEstudiante: miId });
            this.consultar();
          }
        }
        // Si es SECRETARIA o ADMIN: No hace nada, espera a que ingrese un ID manualmente
      } catch (e) {
        console.error('Error al verificar sesión:', e);
      }
    }
  }

  volver(): void {
    this.location.back();
  }

  consultar(): void {
    if (this.busquedaForm.invalid) return;

    this.cargando = true;
    this.errorMessage = '';
    this.infoMessage = '';
    this.listaHistorial = [];

    const { idEstudiante } = this.busquedaForm.value;

    this.historialService.obtenerHistorial(idEstudiante).subscribe({
      next: (data: HistorialPago[]) => {
        this.ngZone.run(() => {
          this.listaHistorial = data || [];
          if (this.listaHistorial.length === 0) {
            this.infoMessage = `El estudiante con ID #${idEstudiante} no posee pagos o recibos registrados en el sistema.`;
          }
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err.error?.mensaje || 'Ocurrió un error al consultar el historial del estudiante.';
          this.listaHistorial = [];
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  descargarPdf(): void {
    if (this.busquedaForm.invalid || this.listaHistorial.length === 0) return;

    this.descargandoPdf = true;
    const { idEstudiante } = this.busquedaForm.value;

    this.historialService.obtenerPdfHistorial(idEstudiante).subscribe({
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
          this.errorMessage = 'Ocurrió un error al generar la vista previa del PDF.';
          this.descargandoPdf = false;
          this.cdr.detectChanges();
        });
      }
    });
  }
}