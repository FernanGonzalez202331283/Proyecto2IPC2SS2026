import { 
  Component, 
  OnInit, 
  inject, 
  DestroyRef, 
  ChangeDetectorRef, 
  ChangeDetectionStrategy 
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EstudianteService, Estudiante } from '../../../services/estudiante.service';

@Component({
  selector: 'app-admin-estudiantes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminEstudiantesComponent implements OnInit {
  private estudianteService = inject(EstudianteService);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  estudiantes: Estudiante[] = [];

  paginaActual = 1;
  limite = 10;
  totalPaginas = 1;
  totalRegistros = 0;

  cargando = false;
  procesandoId: number | null = null;
  successMessage = '';
  errorMessage = '';

  readonly estadosDisponibles = ['ACTIVO', 'INACTIVO', 'GRADUADO'];

  ngOnInit(): void {
    this.cargarEstudiantes();
  }

  cargarEstudiantes(): void {
    this.cargando = true;
    this.cdr.markForCheck();

    this.estudianteService.obtenerEstudiantes(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) 
            ? resp 
            : (resp?.contenido || resp?.lista || resp?.datos || []);

          this.estudiantes = lista;
          this.paginaActual = resp?.paginaActual || 1;
          this.totalPaginas = resp?.totalPaginas || 1;
          this.totalRegistros = resp?.totalRegistros || this.estudiantes.length;

          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.estudiantes = [];
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarEstudiantes();
    }
  }

  cambiarEstado(estudiante: Estudiante, nuevoEstado: string): void {
    if (!estudiante.idEstudiante || estudiante.estado === nuevoEstado) return;

    this.limpiarMensajes();
    this.procesandoId = estudiante.idEstudiante;
    this.cdr.markForCheck();

    this.estudianteService.cambiarEstado(estudiante.idEstudiante, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || `Estado cambiado a ${nuevoEstado} correctamente.`;
          estudiante.estado = nuevoEstado;
          this.procesandoId = null;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.procesandoId = null;
          this.cdr.markForCheck();
        }
      });
  }

  obtenerBadgeClass(estado: string): string {
    switch (estado?.toUpperCase()) {
      case 'ACTIVO':
        return 'bg-success';
      case 'INACTIVO':
        return 'bg-secondary';
      case 'GRADUADO':
        return 'bg-primary';
      default:
        return 'bg-dark';
    }
  }

  private limpiarMensajes(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }

  private obtenerTextoRespuesta(res: any): string {
    if (typeof res === 'string') {
      try {
        const parsed = JSON.parse(res);
        return parsed.mensaje || res;
      } catch {
        return res;
      }
    }
    return res?.mensaje || res?.error?.mensaje || res?.error || 'Procesado correctamente.';
  }
}