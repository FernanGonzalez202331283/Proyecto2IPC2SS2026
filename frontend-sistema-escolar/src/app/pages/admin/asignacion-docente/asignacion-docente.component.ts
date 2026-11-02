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

import { AsignacionDocenteService, AsignacionDocente } from '../../../services/asignacion-docente.service';
import { EmpleadoService, Empleado } from '../../../services/empleado.service';
import { SeccionService, Seccion } from '../../../services/seccion.service';
import { GradoService, Grado } from '../../../services/grado.service';
import { CursoService, Curso } from '../../../services/curso.services';

@Component({
  selector: 'app-asignacion-docente',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './asignacion-docente.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AsignacionDocenteComponent implements OnInit {
  private fb = inject(FormBuilder);
  private asignacionService = inject(AsignacionDocenteService);
  private empleadoService = inject(EmpleadoService);
  private cursoService = inject(CursoService);
  private seccionService = inject(SeccionService);
  private gradoService = inject(GradoService);
  private location = inject(Location);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  asignaciones: AsignacionDocente[] = [];
  maestrosLista: Empleado[] = [];
  cursosLista: Curso[] = [];
  seccionesLista: Seccion[] = [];
  gradosLista: Grado[] = [];

  asignacionForm!: FormGroup;
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
    this.cargarCatalogos();
    this.cargarAsignaciones();
  }

  private initForm(): void {
    this.asignacionForm = this.fb.group({
      idEmpleado: [null, [Validators.required]],
      idCurso: [null, [Validators.required]],
      idSeccion: [null, [Validators.required]]
    });
  }

  esCampoInvalido(controlName: string): boolean {
    const control = this.asignacionForm.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarCatalogos(): void {
    this.empleadoService.obtenerEmpleados(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) ? resp : (resp?.contenido || []);
          this.maestrosLista = lista.filter((e: any) => e.estado !== false);
          this.cdr.markForCheck();
        },
        error: (err) => console.error('Error al cargar empleados:', err)
      });

    this.cursoService.obtenerCursos(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) ? resp : (resp?.contenido || []);
          this.cursosLista = lista.filter((c: any) => c.estado !== false);
          this.cdr.markForCheck();
        },
        error: (err) => console.error('Error al cargar cursos:', err)
      });

    this.seccionService.obtenerSecciones(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) ? resp : (resp?.contenido || []);
          this.seccionesLista = lista.filter((s: any) => s.estado !== false);
          this.cdr.markForCheck();
        },
        error: (err) => console.error('Error al cargar secciones:', err)
      });

    this.gradoService.obtenerGrados(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          this.gradosLista = Array.isArray(resp) ? resp : (resp?.contenido || []);
          this.cdr.markForCheck();
        },
        error: (err) => console.error('Error al cargar grados:', err)
      });
  }

  cargarAsignaciones(): void {
    this.cargando = true;
    this.cdr.markForCheck();

    this.asignacionService.obtenerAsignaciones(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          if (resp && Array.isArray(resp.contenido)) {
            this.asignaciones = resp.contenido;
            this.paginaActual = resp.paginaActual || 1;
            this.totalPaginas = resp.totalPaginas || 1;
            this.totalRegistros = resp.totalRegistros || 0;
          } else if (Array.isArray(resp)) {
            this.asignaciones = resp;
            this.totalRegistros = resp.length;
            this.totalPaginas = 1;
          } else {
            this.asignaciones = [];
            this.totalRegistros = 0;
            this.totalPaginas = 1;
          }
          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.asignaciones = [];
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarAsignaciones();
    }
  }

  guardarAsignacion(): void {
    this.limpiarMensajes();

    if (this.asignacionForm.invalid) {
      this.asignacionForm.markAllAsTouched();
      this.errorMessage = 'Seleccione todos los campos obligatorios (*) marcados en rojo.';
      this.cdr.markForCheck();
      return;
    }

    const formVal = this.asignacionForm.value;
    const idCursoNum = Number(formVal.idCurso);
    const idSeccionNum = Number(formVal.idSeccion);

    const yaExiste = this.asignaciones.some(asig => 
      asig.estado &&
      asig.curso?.idCurso === idCursoNum &&
      asig.seccion?.idSeccion === idSeccionNum &&
      (!this.modoEdicion || asig.idAsignacion !== this.idSeleccionado)
    );

    if (yaExiste) {
      this.errorMessage = 'El curso seleccionado ya se encuentra asignado a esta sección.';
      this.cdr.markForCheck();
      return;
    }

    this.procesando = true;
    this.cdr.markForCheck();

    const payload: AsignacionDocente = {
      empleado: { idEmpleado: Number(formVal.idEmpleado) },
      curso: { idCurso: idCursoNum },
      seccion: { idSeccion: idSeccionNum }
    };

    if (this.modoEdicion && this.idSeleccionado) {
      payload.idAsignacion = this.idSeleccionado;

      this.asignacionService.actualizarAsignacion(this.idSeleccionado, payload)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Asignación docente actualizada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarAsignaciones();
            this.cdr.markForCheck();
          },
          error: (err) => {
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.procesando = false;
            this.cdr.markForCheck();
          }
        });
    } else {
      this.asignacionService.registrarAsignacion(payload)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Asignación docente registrada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarAsignaciones();
            this.cdr.markForCheck();
          },
          error: (err) => {
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.procesando = false;
            this.cdr.markForCheck();
          }
        });
    }
  }

  editarAsignacion(item: AsignacionDocente): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = item.idAsignacion || null;

    this.asignacionForm.patchValue({
      idEmpleado: item.empleado?.idEmpleado || null,
      idCurso: item.curso?.idCurso || null,
      idSeccion: item.seccion?.idSeccion || null
    });
    this.cdr.markForCheck();
  }

  cambiarEstadoAsignacion(item: AsignacionDocente, nuevoEstado: boolean): void {
    if (!item.idAsignacion) return;

    this.limpiarMensajes();

    this.asignacionService.cambiarEstado(item.idAsignacion, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado de asignación actualizado.';
          item.estado = nuevoEstado;
          this.cargarAsignaciones();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cdr.markForCheck();
        }
      });
  }

  obtenerDetalleSeccion(sec?: Seccion | null): string {
    if (!sec) return 'Sin sección';
    if (sec.nombreGrado) {
      return `${sec.nombreGrado} - Sección ${sec.nombre || ''}`;
    }
    const idGradoBusqueda = sec.idGrado;
    const grado = this.gradosLista.find(g => Number(g.idGrado) === Number(idGradoBusqueda));
    const nombreGrado = grado ? grado.nombre : '';
    return nombreGrado ? `${nombreGrado} - Sección ${sec.nombre || ''}` : `Sección ${sec.nombre || ''}`;
  }

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/admin/dashboard']);
    }
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idSeleccionado = null;
    this.asignacionForm.reset({
      idEmpleado: null,
      idCurso: null,
      idSeccion: null
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