import { 
  Component, 
  OnInit, 
  inject, 
  DestroyRef, 
  ChangeDetectorRef, 
  ChangeDetectionStrategy 
} from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { InscripcionService, Inscripcion } from '../../../services/inscripcion.service';
import { EstudianteService, Estudiante } from '../../../services/estudiante.service';
import { SeccionService, Seccion } from '../../../services/seccion.service';
import { GradoService, Grado } from '../../../services/grado.service';

@Component({
  selector: 'app-inscripciones',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './inscripcion.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InscripcionesComponent implements OnInit {
  private fb = inject(FormBuilder);
  private inscripcionService = inject(InscripcionService);
  private estudianteService = inject(EstudianteService);
  private seccionService = inject(SeccionService);
  private gradoService = inject(GradoService);
  private location = inject(Location);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  inscripciones: Inscripcion[] = [];
  estudiantesActivos: Estudiante[] = [];
  gradosLista: Grado[] = [];
  seccionesActivas: Seccion[] = [];
  seccionesFiltradas: Seccion[] = [];

  inscripcionForm!: FormGroup;
  modoEdicion = false;
  idSeleccionado: number | null = null;

  paginaActual = 1;
  limite = 10;
  totalPaginas = 1;
  totalRegistros = 0;

  successMessage = '';
  errorMessage = '';
  cargando = false;
  procesando = false;

  ngOnInit(): void {
    this.initForm();
    this.cargarInscripciones();
    this.cargarEstudiantes();
    this.cargarGrados();
    this.cargarSecciones();
  }

  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/secretaria/dashboard']);
    }
  }

  private initForm(): void {
    const fechaHoy = new Date().toISOString().substring(0, 10);
    this.inscripcionForm = this.fb.group({
      idEstudiante: [null, [Validators.required]],
      idGradoAux: [null, [Validators.required]],
      idSeccion: [null, [Validators.required]],
      fechaInscripcion: [fechaHoy, [Validators.required]],
      estado: ['ACTIVA', [Validators.required]]
    });

    this.inscripcionForm.get('idGradoAux')?.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((idGrado) => {
        this.filtrarSeccionesPorGrado(idGrado);
        this.inscripcionForm.patchValue({ idSeccion: null }, { emitEvent: false });
        this.cdr.markForCheck();
      });
  }

  esCampoInvalido(controlName: string): boolean {
    const control = this.inscripcionForm.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
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
          this.errorMessage = 'Error al cargar grados.';
          this.cdr.markForCheck();
        }
      });
  }

  cargarSecciones(): void {
    this.seccionService.obtenerSecciones(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: any) => {
          const lista: Seccion[] = Array.isArray(data) ? data : (data?.contenido || []);
          this.seccionesActivas = lista.filter(s => s.estado);
          
          const idGradoActual = this.inscripcionForm.get('idGradoAux')?.value;
          if (idGradoActual) {
            this.filtrarSeccionesPorGrado(idGradoActual);
          }
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Error al obtener secciones:', err);
          this.errorMessage = 'Error al cargar secciones.';
          this.cdr.markForCheck();
        }
      });
  }

  private filtrarSeccionesPorGrado(idGrado: any): void {
    if (idGrado === null || idGrado === undefined) {
      this.seccionesFiltradas = [];
      return;
    }
    const idGradoNum = Number(idGrado);
    this.seccionesFiltradas = this.seccionesActivas.filter(s => Number(s.idGrado) === idGradoNum);
  }

  cargarInscripciones(): void {
    this.cargando = true;
    this.cdr.markForCheck();

    this.inscripcionService.obtenerInscripciones(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          if (resp && Array.isArray(resp.contenido)) {
            this.inscripciones = resp.contenido;
            this.paginaActual = resp.paginaActual || 1;
            this.totalPaginas = resp.totalPaginas || 1;
            this.totalRegistros = resp.totalRegistros || 0;
          } else if (Array.isArray(resp)) {
            this.inscripciones = resp;
            this.totalRegistros = resp.length;
            this.totalPaginas = 1;
          } else {
            this.inscripciones = [];
            this.totalRegistros = 0;
            this.totalPaginas = 1;
          }
          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.inscripciones = [];
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarInscripciones();
    }
  }

  cargarEstudiantes(): void {
    this.estudianteService.obtenerEstudiantes(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: any) => {
          const lista: Estudiante[] = Array.isArray(data) ? data : (data?.contenido || []);
          this.estudiantesActivos = lista.filter(e => e.estado === 'ACTIVO');
          this.cdr.markForCheck();
        },
        error: (err) => console.error('Error al obtener estudiantes:', err)
      });
  }

  guardarInscripcion(): void {
    this.limpiarMensajes();

    if (this.inscripcionForm.invalid) {
      this.inscripcionForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      this.cdr.markForCheck();
      return;
    }

    this.procesando = true;
    this.cdr.markForCheck();
    const formVal = this.inscripcionForm.value;

    const datos: Inscripcion = {
      idEstudiante: Number(formVal.idEstudiante),
      idSeccion: Number(formVal.idSeccion),
      fechaInscripcion: formVal.fechaInscripcion,
      estado: formVal.estado
    };

    let operacion$;

    if (this.modoEdicion && this.idSeleccionado) {
      datos.idInscripcion = this.idSeleccionado;
      operacion$ = this.inscripcionService.actualizarInscripcion(this.idSeleccionado, datos);
    } else {
      operacion$ = this.inscripcionService.registrarInscripcion(datos);
    }

    operacion$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (res) => {
        const mensajeDefault = this.modoEdicion ? 'Inscripción actualizada correctamente.' : 'Inscripción registrada correctamente.';
        this.successMessage = this.obtenerTextoRespuesta(res) || mensajeDefault;
        this.procesando = false;
        this.resetForm();
        this.cargarInscripciones();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage = this.obtenerTextoRespuesta(err);
        this.procesando = false;
        this.cdr.markForCheck();
      }
    });
  }

  editarInscripcion(i: Inscripcion): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = i.idInscripcion || null;

    const sec = this.seccionesActivas.find(s => Number(s.idSeccion) === Number(i.idSeccion));
    const idGrado = sec ? sec.idGrado : null;

    if (idGrado) {
      this.filtrarSeccionesPorGrado(idGrado);
    }

    this.inscripcionForm.get('idGradoAux')?.setValue(idGrado, { emitEvent: false });

    this.inscripcionForm.patchValue({
      idEstudiante: i.idEstudiante,
      idSeccion: i.idSeccion,
      fechaInscripcion: i.fechaInscripcion,
      estado: i.estado || 'ACTIVA'
    });

    this.cdr.markForCheck();
  }

  cambiarEstadoInscripcion(i: Inscripcion, nuevoEstado: string): void {
    if (!i.idInscripcion) return;

    this.limpiarMensajes();

    this.inscripcionService.cambiarEstado(i.idInscripcion, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado de inscripción actualizado.';
          i.estado = nuevoEstado;
          this.cargarInscripciones();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cdr.markForCheck();
        }
      });
  }

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  obtenerNombreEstudiante(idEstudiante: number): string {
    const est = this.estudiantesActivos.find(e => Number(e.idEstudiante) === Number(idEstudiante));
    if (!est || !est.persona) return `ID Estudiante: ${idEstudiante}`;
    return `${est.codigoEstudiante ? est.codigoEstudiante + ' - ' : ''}${est.persona.nombres} ${est.persona.apellidos}`;
  }

  obtenerNombreSeccion(idSeccion: number): string {
    const sec = this.seccionesActivas.find(s => Number(s.idSeccion) === Number(idSeccion));
    if (!sec) return `ID Sección: ${idSeccion}`;
    const grado = this.gradosLista.find(g => Number(g.idGrado) === Number(sec.idGrado));
    const gradoNombre = grado ? grado.nombre : `Grado #${sec.idGrado}`;
    return `Sección ${sec.nombre} (${gradoNombre})`;
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idSeleccionado = null;
    this.seccionesFiltradas = [];
    const fechaHoy = new Date().toISOString().substring(0, 10);
    this.inscripcionForm.reset({
      idEstudiante: null,
      idGradoAux: null,
      idSeccion: null,
      fechaInscripcion: fechaHoy,
      estado: 'ACTIVA'
    });
    this.cdr.markForCheck();
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