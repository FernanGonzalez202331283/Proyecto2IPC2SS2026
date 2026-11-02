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

import { CursoAsignadoService, CursoAsignado } from '../../../services/curso-asignado.service';

@Component({
  selector: 'app-curso-asignado',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './curso-asignado.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CursosAsignadosComponent implements OnInit {
  private cursoAsignadoService = inject(CursoAsignadoService);
  private location = inject(Location);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private destroyRef = inject(DestroyRef);

  cursosAsignados: CursoAsignado[] = [];
  cursosFiltrados: CursoAsignado[] = [];

  filtroTexto: string = '';
  cargando: boolean = false;
  errorMessage: string = '';

  ngOnInit(): void {
    this.cargarMisCursos();
  }

  cargarMisCursos(): void {
    this.cargando = true;
    this.errorMessage = '';
    this.cdr.markForCheck();

    this.cursoAsignadoService.obtenerTodos()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data: CursoAsignado[]) => {
          this.cursosAsignados = Array.isArray(data) ? data : [];
          this.aplicarFiltro();
          this.cargando = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Error al cargar cursos asignados:', err);
          this.errorMessage = this.obtenerTextoRespuesta(err);
          this.cursosAsignados = [];
          this.cursosFiltrados = [];
          this.cargando = false;
          this.cdr.markForCheck();
        }
      });
  }

  aplicarFiltro(): void {
    const txt = this.filtroTexto.trim().toLowerCase();
    if (!txt) {
      this.cursosFiltrados = [...this.cursosAsignados];
    } else {
      this.cursosFiltrados = this.cursosAsignados.filter(item => 
        (item.nombreCurso && item.nombreCurso.toLowerCase().includes(txt)) ||
        (item.nombreGrado && item.nombreGrado.toLowerCase().includes(txt)) ||
        (item.nombreSeccion && item.nombreSeccion.toLowerCase().includes(txt)) ||
        (item.nivelGrado && item.nivelGrado.toLowerCase().includes(txt))
      );
    }
    this.cdr.markForCheck();
  }

  limpiarFiltros(): void {
    this.filtroTexto = '';
    this.aplicarFiltro();
  }

  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/maestro/dashboard']);
    }
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
    return res?.mensaje || res?.error?.mensaje || res?.error || 'Error al consultar mis cursos asignados.';
  }
}