import { Component, OnInit, inject, DestroyRef, ChangeDetectionStrategy, signal } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HistorialPagoService, HistorialPago } from '../../../services/historial-pago.service';
import { EstudianteService, Estudiante } from '../../../services/estudiante.service';

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
  private estudianteService = inject(EstudianteService); 
  private location = inject(Location);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  busquedaForm: FormGroup = this.fb.group({
    idEstudiante: ['', [Validators.required]] 
  });

  listaHistorial = signal<HistorialPago[]>([]);
  listaEstudiantes = signal<Estudiante[]>([]); 
  cargando = signal(false);
  cargandoEstudiantes = signal(true);
  descargandoPdf = signal(false);
  esEstudiante = signal(false);
  
  errorMessage = signal('');
  infoMessage = signal('');

  ngOnInit(): void {
    const rawUser = localStorage.getItem('usuario') || sessionStorage.getItem('usuario');
    if (rawUser) {
      try {
        const parsed = JSON.parse(rawUser);
        const rolUsuario = String(parsed.rol || parsed.usuario?.rol || '').toUpperCase();

        if (rolUsuario === 'ESTUDIANTE' || rolUsuario === 'ROLE_ESTUDIANTE') {
          this.esEstudiante.set(true);
          const miId = parsed.idPersona || parsed.idEstudiante || parsed.id;
          if (miId && miId > 0) {
            this.busquedaForm.patchValue({ idEstudiante: miId });
            this.consultar();
          }
        } else {
          this.cargarEstudiantes();
        }
      } catch (e) {
        console.error('Error al verificar datos de sesión:', e);
      }
    }
  }

  cargarEstudiantes(): void {
    this.cargandoEstudiantes.set(true); 
    this.estudianteService.obtenerEstudiantes(1, 1000)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.listaEstudiantes.set(data.contenido || []);
          this.cargandoEstudiantes.set(false);
        },
        error: () => {
          this.errorMessage.set('No se pudo cargar la lista de estudiantes para el formulario.');
          this.cargandoEstudiantes.set(false); 
        }
      });
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

    this.cargando.set(true);
    this.errorMessage.set('');
    this.infoMessage.set('');
    this.listaHistorial.set([]);

    const { idEstudiante } = this.busquedaForm.value;

    this.historialService.obtenerHistorial(idEstudiante)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: HistorialPago[]) => {
          const resultados = data || [];
          this.listaHistorial.set(resultados);
          if (resultados.length === 0) {
            this.infoMessage.set('El estudiante seleccionado no posee pagos o recibos registrados en el sistema.');
          }
          this.cargando.set(false);
        },
        error: (err) => {
          this.errorMessage.set(err.error?.mensaje || 'Ocurrió un error al consultar el historial del estudiante.');
          this.listaHistorial.set([]);
          this.cargando.set(false);
        }
      });
  }

  descargarPdf(): void {
    if (this.busquedaForm.invalid || this.listaHistorial().length === 0) return;

    this.descargandoPdf.set(true);
    const { idEstudiante } = this.busquedaForm.value;

    this.historialService.obtenerPdfHistorial(idEstudiante)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (blobData: Blob) => {
          const fileURL = URL.createObjectURL(blobData);
          window.open(fileURL, '_blank');
          this.descargandoPdf.set(false);
        },
        error: () => {
          this.errorMessage.set('Ocurrió un error al generar la vista previa del PDF.');
          this.descargandoPdf.set(false);
        }
      });
  }
}