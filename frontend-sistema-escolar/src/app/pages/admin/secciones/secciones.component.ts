import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SeccionService, Seccion } from '../../../services/seccion.service';
import { GradoService, Grado } from '../../../services/grado.service';
import { AnoLectivoService, AnoLectivo } from '../../../services/ano-lectivo.service';

@Component({
  selector: 'app-secciones',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './secciones.component.html'
})
export class SeccionesComponent implements OnInit {
  secciones: Seccion[] = [];
  grados: Grado[] = [];
  anosLectivos: AnoLectivo[] = [];

  seccionForm!: FormGroup;
  modoEdicion: boolean = false;
  idSeleccionado: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private seccionService: SeccionService,
    private gradoService: GradoService,
    private anoLectivoService: AnoLectivoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarDatosIniciales();
  }

  private initForm(): void {
    this.seccionForm = this.fb.group({
      idGrado: [null, [Validators.required]],
      idAnioLectivo: [null, [Validators.required]],
      nombre: ['', [Validators.required, Validators.maxLength(50)]],
      capacidad: [30, [Validators.required, Validators.min(1)]],
      estado: [true]
    });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.seccionForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarDatosIniciales(): void {
    this.cargando = true;

    // Cargar Grados
    this.gradoService.obtenerGrados().subscribe({
      next: (dataGrados) => {
        this.grados = dataGrados || [];
      }
    });

    // Cargar Años Lectivos
    this.anoLectivoService.obtenerAnosLectivos().subscribe({
      next: (dataAnos) => {
        this.anosLectivos = (dataAnos || []).filter(a => a.estado);
        if (this.anosLectivos.length > 0 && !this.seccionForm.get('idAnioLectivo')?.value) {
          const ultimoAno = this.anosLectivos[this.anosLectivos.length - 1];
          const idAno = ultimoAno.idAnoLectivo ?? (ultimoAno as any).idAnioLectivo;
          this.seccionForm.patchValue({
            idAnioLectivo: idAno
          });
        }
      }
    });

    // Cargar Secciones
    this.cargarSecciones();
  }

  cargarSecciones(): void {
    this.seccionService.obtenerSecciones().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.secciones = data || [];
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

  guardarSeccion(): void {
    this.limpiarMensajes();

    if (this.seccionForm.invalid) {
      this.seccionForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    const datos: Seccion = this.seccionForm.value;

    if (this.modoEdicion && this.idSeleccionado) {
      this.seccionService.actualizarSeccion(this.idSeleccionado, datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Sección actualizada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarSecciones();
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
      this.seccionService.registrarSeccion(datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Sección registrada correctamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarSecciones();
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

  toggleEstado(seccion: Seccion): void {
    if (!seccion.idSeccion) return;

    this.limpiarMensajes();
    const nuevoEstado = !seccion.estado;

    this.seccionService.cambiarEstado(seccion.idSeccion, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
          seccion.estado = nuevoEstado;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cdr.detectChanges();
        });
      }
    });
  }

  editarSeccion(seccion: Seccion): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = seccion.idSeccion || null;

    this.seccionForm.patchValue({
      idGrado: seccion.idGrado,
      idAnioLectivo: seccion.idAnioLectivo ?? seccion.idAnoLectivo,
      nombre: seccion.nombre,
      capacidad: seccion.capacidad,
      estado: seccion.estado
    });
  }

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  obtenerNombreGrado(idGrado: number | undefined): string {
    if (!idGrado) return '-';
    const grado = this.grados.find(g => g.idGrado === idGrado);
    return grado ? `${grado.nombre} (${grado.nivel})` : `#${idGrado}`;
  }

  obtenerAnioLectivo(idAnio: number | undefined): string {
    if (!idAnio) return '-';
    const ano = this.anosLectivos.find(a => (a.idAnoLectivo ?? (a as any).idAnioLectivo) === idAnio);
    return ano ? `${ano.anio}` : `#${idAnio}`;
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idSeleccionado = null;
    const ultimoAnoObj = this.anosLectivos.length > 0 ? this.anosLectivos[this.anosLectivos.length - 1] : null;
    const ultimoAno = ultimoAnoObj ? (ultimoAnoObj.idAnoLectivo ?? (ultimoAnoObj as any).idAnioLectivo) : null;

    this.seccionForm.reset({
      idGrado: null,
      idAnioLectivo: ultimoAno,
      nombre: '',
      capacidad: 30,
      estado: true
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