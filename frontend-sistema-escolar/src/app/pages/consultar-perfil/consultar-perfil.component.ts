import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, NgClass, Location } from '@angular/common';
import { PerfilService, Perfil } from '../../services/perfil.service';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-consultar-perfil',
  standalone: true,
  imports: [CommonModule, NgClass],
  templateUrl: './consultar-perifl.component.html'
})
export class ConsultarPerfilComponent implements OnInit {
  perfil: Perfil | null = null;
  cargando: boolean = true;
  errorMessage: string = '';

  constructor(
    private perfilService: PerfilService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.cargarPerfil();
  }

  volver(): void {
    this.location.back();
  }

  cargarPerfil(): void {
    this.cargando = true;
    this.errorMessage = '';

    const idUsuario = this.authService.getIdUsuario();

    if (!idUsuario || idUsuario <= 0) {
      this.errorMessage = 'No se encontró una sesión activa válida.';
      this.cargando = false;
      this.cdr.detectChanges();
      return;
    }

    this.perfilService.obtenerPerfilPorUsuario(idUsuario).subscribe({
      next: (res: Perfil) => {
        this.ngZone.run(() => {
          this.perfil = res;
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err.error?.mensaje || 'Error al cargar la información del perfil.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }
}