import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EstudianteService, Estudiante } from '../../../services/estudiante.service';
import { ExpedienteService, Expediente } from '../../../services/expediente.service';
import { SeccionService, Seccion } from '../../../services/seccion.service';
import { GradoService, Grado } from '../../../services/grado.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-expediente',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './expediente.component.html'
})
export class ExpedienteComponent implements OnInit {
  estudiantesLista: Estudiante[] = [];
  seccionesLista: Seccion[] = [];
  gradosLista: Grado[] = [];

  idEstudianteSeleccionado: number | null = null;
  expediente: Expediente | null = null;

  cargando: boolean = false;
  errorMessage: string = '';

  constructor(
    private estudianteService: EstudianteService,
    private expedienteService: ExpedienteService,
    private seccionService: SeccionService,
    private gradoService: GradoService,
    private location: Location,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.cargarEstudiantes();
    this.cargarSecciones();
    this.cargarGrados();
  }

  cargarEstudiantes(): void {
    this.estudianteService.obtenerEstudiantes().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.estudiantesLista = data || [];
          this.cdr.detectChanges();
        });
      },
      error: (err) => console.error('Error al obtener lista de estudiantes:', err)
    });
  }

  cargarSecciones(): void {
    this.seccionService.obtenerSecciones().subscribe({
      next: (data) => { this.seccionesLista = data || []; },
      error: (err) => console.error('Error al obtener secciones:', err)
    });
  }

  cargarGrados(): void {
    this.gradoService.obtenerGrados().subscribe({
      next: (data) => { this.gradosLista = data || []; },
      error: (err) => console.error('Error al obtener grados:', err)
    });
  }

  consultarExpediente(): void {
    if (!this.idEstudianteSeleccionado) return;

    this.errorMessage = '';
    this.expediente = null;
    this.cargando = true;

    this.expedienteService.consultarExpediente(Number(this.idEstudianteSeleccionado)).subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.expediente = data;
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.cargando = false;
          if (err.status === 404) {
            this.errorMessage = err.error?.mensaje || 'No existe información registrada para el estudiante solicitado.';
          } else if (err.status === 403) {
            this.errorMessage = 'No tiene permisos para consultar expedientes.';
          } else {
            this.errorMessage = 'Error al consultar el expediente. Verifique la conexión con el servidor.';
          }
          this.cdr.detectChanges();
        });
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
    this.location.back();
  }
}