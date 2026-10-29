import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GradoService, Grado } from '../../../services/grado.service';
import { ConfiguracionGradoService, ConfiguracionGrado } from '../../../services/configuracion-grado.service';

@Component({
  selector: 'app-grados',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './grados.component.html'
})
export class GradosComponent implements OnInit {
  grados: Grado[] = [];
  configuraciones: ConfiguracionGrado[] = [];
  configuracionesFiltradas: ConfiguracionGrado[] = [];
  gradoForm!: FormGroup;
  modoEdicion: boolean = false;
  idSeleccionado: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private gradoService: GradoService,
    private configuracionService: ConfiguracionGradoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarGrados();
    this.cargarConfiguracionesBase();
  }

  private initForm(): void {
    this.gradoForm = this.fb.group({
      nivel: ['PRIMARIA', [Validators.required]],
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['']
    });

    this.gradoForm.get('nivel')?.valueChanges.subscribe((nivelSeleccionado) => {
      this.filtrarConfiguracionesPorNivel(nivelSeleccionado);
    });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.gradoForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarGrados(): void {
    this.cargando = true;
    this.gradoService.obtenerGrados().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.grados = data || [];
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  cargarConfiguracionesBase(): void {
    this.configuracionService.obtenerConfiguraciones().subscribe({
      next: (data) => {
        this.configuraciones = (data || []).filter(c => c.estado);
        this.filtrarConfiguracionesPorNivel(this.gradoForm.get('nivel')?.value);
      },
      error: () => {}
    });
  }

  filtrarConfiguracionesPorNivel(nivel: string): void {
    if (!nivel) {
      this.configuracionesFiltradas = [];
      return;
    }
    this.configuracionesFiltradas = this.configuraciones.filter(c => c.nivel === nivel);
  }

  seleccionarPlantillaGrado(nombrePlantilla: string): void {
    this.gradoForm.patchValue({ nombre: nombrePlantilla });
  }

  guardarGrado(): void {
    this.limpiarMensajes();

    if (this.gradoForm.invalid) {
      this.gradoForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    const datos: Grado = this.gradoForm.value;

    if (this.modoEdicion && this.idSeleccionado) {
      this.gradoService.actualizarGrado(this.idSeleccionado, datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Grado actualizado correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarGrados();
            this.cdr.detectChanges();
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.procesando = false;
            this.cdr.detectChanges();
          });
        }
      });
    } else {
      this.gradoService.registrarGrado(datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Grado registrado correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarGrados();
            this.cdr.detectChanges();
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.procesando = false;
            this.cdr.detectChanges();
          });
        }
      });
    }
  }

  editarGrado(grado: Grado): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = grado.idGrado || null;

    this.gradoForm.patchValue({
      nivel: grado.nivel,
      nombre: grado.nombre,
      descripcion: grado.descripcion || ''
    });
  }

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idSeleccionado = null;
    this.gradoForm.reset({
      nivel: 'PRIMARIA',
      nombre: '',
      descripcion: ''
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