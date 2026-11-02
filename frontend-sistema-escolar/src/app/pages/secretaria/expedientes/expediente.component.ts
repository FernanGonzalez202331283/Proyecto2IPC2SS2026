import { 
  Component, 
  OnInit, 
  inject, 
  DestroyRef, 
  ChangeDetectorRef, 
  ChangeDetectionStrategy 
} from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { EstudianteService, Estudiante } from '../../../services/estudiante.service';
import { ExpedienteService, Expediente } from '../../../services/expediente.service';
import { SeccionService, Seccion } from '../../../services/seccion.service';
import { GradoService, Grado } from '../../../services/grado.service';

@Component({
  selector: 'app-expediente',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './expediente.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExpedienteComponent implements OnInit {
  private estudianteService = inject(EstudianteService);
  private expedienteService = inject(ExpedienteService);
  private seccionService = inject(SeccionService);
  private gradoService = inject(GradoService);
  private location = inject(Location);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  estudiantesLista: Estudiante[] = [];
  seccionesLista: Seccion[] = [];
  gradosLista: Grado[] = [];

  idEstudianteSeleccionado: number | null = null;
  expediente: Expediente | null = null;

  cargando = false;
  errorMessage = '';

  ngOnInit(): void {
    this.cargarEstudiantes();
    this.cargarSecciones();
    this.cargarGrados();
  }

  cargarEstudiantes(): void {
    this.estudianteService.obtenerEstudiantes(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: any) => {
          this.estudiantesLista = Array.isArray(data) ? data : (data?.contenido || []);
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Error al obtener lista de estudiantes:', err);
          this.cdr.markForCheck();
        }
      });
  }

  cargarSecciones(): void {
    this.seccionService.obtenerSecciones(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: any) => {
          this.seccionesLista = Array.isArray(data) ? data : (data?.contenido || []);
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Error al obtener secciones:', err);
          this.cdr.markForCheck();
        }
      });
  }

  cargarGrados(): void {
    this.gradoService.obtenerGrados(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: any) => {
          this.gradosLista = Array.isArray(data) ? data : (data?.contenido || []);
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Error al obtener grados:', err);
          this.cdr.markForCheck();
        }
      });
  }

  consultarExpediente(): void {
    if (!this.idEstudianteSeleccionado) return;

    this.errorMessage = '';
    this.expediente = null;
    this.cargando = true;
    this.cdr.markForCheck();

    this.expedienteService.consultarExpediente(Number(this.idEstudianteSeleccionado))
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.expediente = data;
          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.cargando = false;
          if (err.status === 404) {
            this.errorMessage = err.error?.mensaje || 'No existe información registrada para el estudiante solicitado.';
          } else if (err.status === 403) {
            this.errorMessage = 'No tiene permisos para consultar expedientes.';
          } else {
            this.errorMessage = 'Error al consultar el expediente. Verifique la conexión con el servidor.';
          }
          this.cdr.markForCheck();
        }
      });
  }

  obtenerDetalleSeccion(idSeccion: number): string {
    const sec = this.seccionesLista.find(s => Number(s.idSeccion) === Number(idSeccion));
    if (!sec) return `Sección #${idSeccion}`;
    const grado = this.gradosLista.find(g => Number(g.idGrado) === Number(sec.idGrado));
    const nombreGrado = grado ? grado.nombre : `Grado #${sec.idGrado}`;
    return `${nombreGrado} - Sección ${sec.nombre}`;
  }

  volver(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/secretaria/dashboard']);
    }
  }
}