import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CursoAsignadoService, CursoAsignado } from '../../../services/curso-asignado.service';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-curso-asignado',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './curso-asignado.component.html'
})
export class CursosAsignadosComponent implements OnInit {
  cursosAsignados: CursoAsignado[] = [];
  cursosFiltrados: CursoAsignado[] = [];

  filtroTexto: string = '';
  cargando: boolean = false;
  errorMessage: string = '';

  constructor(
    private cursoAsignadoService: CursoAsignadoService,
    private location: Location,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    // Solo cargamos los cursos. El backend sabrá por el Token quién es el maestro y le dará los suyos.
    this.cargarMisCursos();
  }

  cargarMisCursos(): void {
    this.cargando = true;
    this.errorMessage = '';

    this.cursoAsignadoService.obtenerTodos().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.cursosAsignados = data || [];
          this.aplicarFiltro();
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

  aplicarFiltro(): void {
    const txt = this.filtroTexto.trim().toLowerCase();
    if (!txt) {
      this.cursosFiltrados = [...this.cursosAsignados];
      return;
    }

    this.cursosFiltrados = this.cursosAsignados.filter(item => 
      (item.nombreCurso && item.nombreCurso.toLowerCase().includes(txt)) ||
      (item.nombreGrado && item.nombreGrado.toLowerCase().includes(txt)) ||
      (item.nombreSeccion && item.nombreSeccion.toLowerCase().includes(txt))
    );
  }

  limpiarFiltros(): void {
    this.filtroTexto = '';
    this.aplicarFiltro();
  }

  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/maestro/dashboard']); // Ajustado para que vuelva al dashboard del maestro
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