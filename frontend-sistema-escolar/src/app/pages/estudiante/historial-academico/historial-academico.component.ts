import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, NgClass, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HistorialAcademicoService, HistorialAcademico } from '../../../services/historial-academico.service';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-historial-academico',
  standalone: true,
  imports: [CommonModule, NgClass, FormsModule],
  templateUrl: './historial-academico.component.html'
})
export class HistorialAcademicoComponent implements OnInit {
  historial: HistorialAcademico | null = null;
  cargando: boolean = false;
  errorMessage: string = '';
  
  idEstudianteBusqueda: number = 0;
  esVistaEstudiante: boolean = false;

  constructor(
    private historialService: HistorialAcademicoService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private location: Location
  ) {}

  ngOnInit(): void {
    const rol = this.authService.getUserRole();
    
    // Si el usuario autenticado es ESTUDIANTE, consultar de forma automática
    if (rol === 'ESTUDIANTE') {
      this.esVistaEstudiante = true;
      const idUsuario = this.authService.getIdPersona() || this.authService.getIdUsuario();
      if (idUsuario) {
        this.idEstudianteBusqueda = idUsuario;
        this.consultar();
      }
    }
  }

  volver(): void {
    this.location.back();
  }

  consultar(): void {
    if (!this.idEstudianteBusqueda || this.idEstudianteBusqueda <= 0) {
      this.errorMessage = 'Ingrese un ID de estudiante válido.';
      return;
    }

    this.cargando = true;
    this.errorMessage = '';
    this.historial = null;

    this.historialService.obtenerHistorialEstudiante(this.idEstudianteBusqueda).subscribe({
      next: (data: HistorialAcademico) => {
        this.ngZone.run(() => {
          this.historial = data;
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err.error?.mensaje || 'No se encontró el historial académico para el estudiante indicado.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  obtenerBadgePromedio(promedio: number): string {
    if (promedio >= 70) return 'bg-success';
    if (promedio >= 60) return 'bg-warning text-dark';
    return 'bg-danger';
  }
}