import { Component, OnInit, inject, DestroyRef } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Location } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EmpleadoService, Empleado, RespuestaPaginada } from '../../../services/empleado.service';

@Component({
  selector: 'app-empleados',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './empleado.component.html'
})
export class EmpleadosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private empleadoService = inject(EmpleadoService);
  private router = inject(Router);
  private location = inject(Location);
  private destroyRef = inject(DestroyRef);

  empleados: Empleado[] = [];
  empleadoForm!: FormGroup;
  modoEdicion = false;
  idSeleccionado: number | null = null;
  idPersonaSeleccionada: number | null = null;

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
    this.cargarEmpleados();
  }

  private initForm(): void {
    const fechaHoy = new Date().toISOString().substring(0, 10);
    this.empleadoForm = this.fb.group({
      puesto: ['', [Validators.required, Validators.maxLength(100)]],
      fechaContratacion: [fechaHoy, [Validators.required]],
      salario: [null, [Validators.required, Validators.min(0.01)]],
      estado: [true, [Validators.required]],
      persona: this.fb.group({
        idPersona: [null],
        nombres: ['', [Validators.required, Validators.maxLength(100)]],
        apellidos: ['', [Validators.required, Validators.maxLength(100)]],
        dpi: ['', [Validators.required, Validators.maxLength(20)]],
        telefono: ['', [Validators.required, Validators.maxLength(20)]],
        correo: ['', [Validators.email]],
        direccion: ['', [Validators.maxLength(255)]]
      })
    });
  }

  esCampoInvalido(controlName: string, parentGroup: string = ''): boolean {
    let control;
    if (parentGroup) {
      control = this.empleadoForm.get(parentGroup)?.get(controlName);
    } else {
      control = this.empleadoForm.get(controlName);
    }
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarEmpleados(): void {
    this.cargando = true;

    this.empleadoService.obtenerEmpleados(this.paginaActual, this.limite)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: RespuestaPaginada<Empleado> | any) => {
          if (resp && Array.isArray(resp.contenido)) {
            this.empleados = resp.contenido;
            this.paginaActual = resp.paginaActual || 1;
            this.totalPaginas = resp.totalPaginas || 1;
            this.totalRegistros = resp.totalRegistros || 0;
          } else if (Array.isArray(resp)) {
            this.empleados = resp;
            this.totalRegistros = resp.length;
            this.totalPaginas = 1;
          } else {
            this.empleados = [];
            this.totalRegistros = 0;
            this.totalPaginas = 1;
          }
          this.cargando = false;
        },
        error: (err) => {
          this.empleados = [];
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.paginaActual = nuevaPagina;
      this.cargarEmpleados();
    }
  }

  guardarEmpleado(): void {
    this.limpiarMensajes();

    if (this.empleadoForm.invalid) {
      this.empleadoForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    const formVal = this.empleadoForm.value;

    const datos: Empleado = {
      puesto: formVal.puesto,
      fechaContratacion: formVal.fechaContratacion,
      salario: Number(formVal.salario),
      estado: formVal.estado,
      persona: {
        nombres: formVal.persona.nombres,
        apellidos: formVal.persona.apellidos,
        dpi: formVal.persona.dpi,
        telefono: formVal.persona.telefono,
        correo: formVal.persona.correo,
        direccion: formVal.persona.direccion
      }
    };

    let operacion$;

    if (this.modoEdicion && this.idSeleccionado) {
      datos.idEmpleado = this.idSeleccionado;
      if (this.idPersonaSeleccionada) {
        datos.persona.idPersona = this.idPersonaSeleccionada;
      }
      operacion$ = this.empleadoService.actualizarEmpleado(this.idSeleccionado, datos);
    } else {
      operacion$ = this.empleadoService.registrarEmpleado(datos);
    }

    operacion$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (res) => {
        const mensajeDefault = this.modoEdicion ? 'Empleado actualizado correctamente.' : 'Empleado registrado correctamente.';
        this.successMessage = this.obtenerTextoRespuesta(res) || mensajeDefault;
        this.procesando = false;
        this.resetForm();
        this.cargarEmpleados();
      },
      error: (err) => {
        this.errorMessage = this.obtenerTextoRespuesta(err);
        this.procesando = false;
      }
    });
  }

  editarEmpleado(emp: Empleado): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = emp.idEmpleado || null;
    this.idPersonaSeleccionada = emp.persona?.idPersona || null;

    this.empleadoForm.patchValue({
      puesto: emp.puesto,
      fechaContratacion: emp.fechaContratacion,
      salario: emp.salario,
      estado: emp.estado,
      persona: {
        idPersona: emp.persona?.idPersona || null,
        nombres: emp.persona?.nombres || '',
        apellidos: emp.persona?.apellidos || '',
        dpi: emp.persona?.dpi || '',
        telefono: emp.persona?.telefono || '',
        correo: emp.persona?.correo || '',
        direccion: emp.persona?.direccion || ''
      }
    });
  }

  cambiarEstadoEmpleado(emp: Empleado, nuevoEstado: boolean): void {
    if (!emp.idEmpleado) return;

    this.limpiarMensajes();

    this.empleadoService.cambiarEstado(emp.idEmpleado, nuevoEstado)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado de empleado actualizado.';
          emp.estado = nuevoEstado;
        },
        error: (err) => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
        }
      });
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
    this.idPersonaSeleccionada = null;
    const fechaHoy = new Date().toISOString().substring(0, 10);
    this.empleadoForm.reset({
      puesto: '',
      fechaContratacion: fechaHoy,
      salario: null,
      estado: true,
      persona: {
        idPersona: null,
        nombres: '',
        apellidos: '',
        dpi: '',
        telefono: '',
        correo: '',
        direccion: ''
      }
    });
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