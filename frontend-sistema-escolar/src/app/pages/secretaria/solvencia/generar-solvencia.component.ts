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
import { RouterLink, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { SolvenciaService, Solvencia } from '../../../services/solvencia.service';
import { EstudianteService, Estudiante } from '../../../services/estudiante.service';
import { AnoLectivoService, AnoLectivo } from '../../../services/ano-lectivo.service';

@Component({
  selector: 'app-generar-solvencia',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './generar-solvencia.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GenerarSolvenciaComponent implements OnInit {
  private fb = inject(FormBuilder);
  private solvenciaService = inject(SolvenciaService);
  private estudianteService = inject(EstudianteService);
  private anoLectivoService = inject(AnoLectivoService);
  private location = inject(Location);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  solvenciaForm: FormGroup = this.fb.group({
    idEstudiante: [null, [Validators.required]],
    idAnoLectivo: [null, [Validators.required]]
  });

  estudiantesLista: Estudiante[] = [];
  aniosLectivosLista: AnoLectivo[] = [];
  estudianteSeleccionado: Estudiante | null = null;

  cargandoCatalogos = false;
  consultando = false;
  descargandoPdf = false;

  resultadoSolvencia: Solvencia | null = null;
  errorMessage = '';

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  volver(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/secretaria/dashboard']);
    }
  }

  cargarCatalogos(): void {
    this.cargandoCatalogos = true;
    this.errorMessage = '';
    this.cdr.markForCheck();

    // 1. Cargar Estudiantes
    this.estudianteService.obtenerEstudiantes(1, 100)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          const lista = Array.isArray(resp) ? resp : (resp?.contenido || []);
          this.estudiantesLista = lista.filter((e: Estudiante) => e.estado === 'ACTIVO');
          this.cdr.markForCheck();
        },
        error: () => {
          this.errorMessage = 'Error al cargar la lista de estudiantes.';
          this.cdr.markForCheck();
        }
      });

    // 2. Cargar Años Lectivos
    this.anoLectivoService.obtenerAnosLectivos(1, 50)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resp: any) => {
          if (resp && Array.isArray(resp.contenido)) {
            this.aniosLectivosLista = resp.contenido;
          } else if (Array.isArray(resp)) {
            this.aniosLectivosLista = resp;
          } else {
            this.aniosLectivosLista = [];
          }
          
          // Seleccionar por defecto el año lectivo activo
          const anioActivo = this.aniosLectivosLista.find(a => a.estado === true);
          if (anioActivo && anioActivo.idAnoLectivo) {
            this.solvenciaForm.patchValue({ idAnoLectivo: anioActivo.idAnoLectivo });
          }

          this.cargandoCatalogos = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Error al cargar años lectivos:', err);
          this.errorMessage = 'No se pudo cargar el catálogo de años lectivos.';
          this.cargandoCatalogos = false;
          this.cdr.markForCheck();
        }
      });
  }

  onEstudianteChange(): void {
    const idEst = Number(this.solvenciaForm.value.idEstudiante);
    this.estudianteSeleccionado = this.estudiantesLista.find(e => Number(e.idEstudiante) === idEst) || null;
    this.resultadoSolvencia = null;
    this.errorMessage = '';
    this.cdr.markForCheck();
  }

  verificarSolvencia(): void {
    if (this.solvenciaForm.invalid) return;

    this.consultando = true;
    this.resultadoSolvencia = null;
    this.errorMessage = '';
    this.cdr.markForCheck();

    const { idEstudiante, idAnoLectivo } = this.solvenciaForm.value;

    this.solvenciaService.consultarSolvencia(Number(idEstudiante), Number(idAnoLectivo))
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res: Solvencia) => {
          this.resultadoSolvencia = res;
          this.consultando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = err.error?.mensaje || err?.mensaje || 'No se pudo verificar la solvencia del estudiante.';
          this.consultando = false;
          this.cdr.markForCheck();
        }
      });
  }

  generarConstanciaPDF(): void {
    if (!this.resultadoSolvencia || this.resultadoSolvencia.estado !== 'SOLVENTE') return;

    this.descargandoPdf = true;
    this.cdr.markForCheck();

    const { idEstudiante, idAnoLectivo } = this.solvenciaForm.value;

    this.solvenciaService.descargarPdfSolvencia(Number(idEstudiante), Number(idAnoLectivo))
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (blobData: Blob) => {
          const fileURL = URL.createObjectURL(blobData);
          window.open(fileURL, '_blank');
          this.descargandoPdf = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.errorMessage = 'Ocurrió un error al generar la constancia en PDF.';
          this.descargandoPdf = false;
          this.cdr.markForCheck();
        }
      });
  }
}