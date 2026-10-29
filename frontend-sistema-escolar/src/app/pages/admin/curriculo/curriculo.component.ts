import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CurriculoService, Curriculo } from '../../../services/curriculo.service';
import { GradoService, Grado } from '../../../services/grado.service';
import { AnoLectivoService, AnoLectivo } from '../../../services/ano-lectivo.service';
import { CursoService, Curso } from '../../../services/curso.services';
@Component({
  selector: 'app-curriculo',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './curriculo.component.html'
})
export class CurriculoComponent implements OnInit {
  curriculos: Curriculo[] = [];
  grados: Grado[] = [];
  cursos: Curso[] = [];
  anosLectivos: AnoLectivo[] = [];

  curriculoForm!: FormGroup;
  modoEdicion: boolean = false;
  idSeleccionado: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private curriculoService: CurriculoService,
    private gradoService: GradoService,
    private cursoService: CursoService,
    private anoLectivoService: AnoLectivoService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarDatosIniciales();
  }

  private initForm(): void {
    this.curriculoForm = this.fb.group({
      idAnioLectivo: [null, [Validators.required]],
      idGrado: [null, [Validators.required]],
      idCurso: [null, [Validators.required]],
      estado: [true]
    });
  }

  esCampoInvalido(nombreControl: string): boolean {
    const control = this.curriculoForm.get(nombreControl);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  cargarDatosIniciales(): void {
    this.cargando = true;

    // Cargar Grados
    this.gradoService.obtenerGrados().subscribe({
      next: (data) => { this.grados = data || []; }
    });

    // Cargar Cursos activos
    this.cursoService.obtenerCursos().subscribe({
      next: (data) => { this.cursos = (data || []).filter(c => c.estado); }
    });

    // Cargar Años Lectivos
    this.anoLectivoService.obtenerAnosLectivos().subscribe({
      next: (data) => {
        this.anosLectivos = (data || []).filter(a => a.estado);
        if (this.anosLectivos.length > 0 && !this.curriculoForm.get('idAnioLectivo')?.value) {
          const ultimo = this.anosLectivos[this.anosLectivos.length - 1];
          this.curriculoForm.patchValue({ idAnioLectivo: ultimo.idAnoLectivo });
        }
      }
    });

    this.cargarCurriculos();
  }

  cargarCurriculos(): void {
    this.curriculoService.obtenerCurriculos().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.curriculos = data || [];
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

  guardarCurriculo(): void {
    this.limpiarMensajes();

    if (this.curriculoForm.invalid) {
      this.curriculoForm.markAllAsTouched();
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*) marcados en rojo.';
      return;
    }

    this.procesando = true;
    const datos: Curriculo = this.curriculoForm.value;

    if (this.modoEdicion && this.idSeleccionado) {
      this.curriculoService.actualizarCurriculo(this.idSeleccionado, datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Asignación de curso a currículo actualizada.';
            this.procesando = false;
            this.resetForm();
            this.cargarCurriculos();
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
      this.curriculoService.registrarCurriculo(datos).subscribe({
        next: (res) => {
          this.ngZone.run(() => {
            this.successMessage = this.obtenerTextoRespuesta(res) || 'Curso asignado al currículo del grado exitosamente.';
            this.procesando = false;
            this.resetForm();
            this.cargarCurriculos();
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

  toggleEstado(c: Curriculo): void {
    if (!c.idCurriculo) return;

    this.limpiarMensajes();
    const nuevoEstado = !c.estado;

    this.curriculoService.cambiarEstado(c.idCurriculo, nuevoEstado).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = this.obtenerTextoRespuesta(res) || 'Estado actualizado correctamente.';
          c.estado = nuevoEstado;
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

  editarCurriculo(c: Curriculo): void {
    this.limpiarMensajes();
    this.modoEdicion = true;
    this.idSeleccionado = c.idCurriculo || null;

    this.curriculoForm.patchValue({
      idAnioLectivo: c.idAnioLectivo,
      idGrado: c.idGrado,
      idCurso: c.idCurso,
      estado: c.estado
    });
  }

  cancelarEdicion(): void {
    this.limpiarMensajes();
    this.resetForm();
  }

  obtenerNombreGrado(idGrado: number): string {
    const grado = this.grados.find(g => g.idGrado === idGrado);
    return grado ? `${grado.nombre} (${grado.nivel})` : `#${idGrado}`;
  }

  obtenerNombreCurso(idCurso: number): string {
    const curso = this.cursos.find(c => c.idCurso === idCurso);
    return curso ? curso.nombre : `#${idCurso}`;
  }

  obtenerAnioLectivo(idAnio: number): string {
    const ano = this.anosLectivos.find(a => a.idAnoLectivo === idAnio);
    return ano ? `${ano.anio}` : `#${idAnio}`;
  }

  private resetForm(): void {
    this.modoEdicion = false;
    this.idSeleccionado = null;
    const ultimoAno = this.anosLectivos.length > 0 ? this.anosLectivos[this.anosLectivos.length - 1].idAnoLectivo : null;

    this.curriculoForm.reset({
      idAnioLectivo: ultimoAno,
      idGrado: null,
      idCurso: null,
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