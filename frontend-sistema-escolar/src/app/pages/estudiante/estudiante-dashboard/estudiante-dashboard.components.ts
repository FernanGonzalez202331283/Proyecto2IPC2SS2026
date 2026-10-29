import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { PrestamoService, Prestamo } from '../../../services/prestamo.service';
import { MultaService, Multa } from '../../../services/multa.service';

@Component({
  selector: 'app-estudiante-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './estudiante-dashboard.component.html',
})
export class EstudianteDashboardComponent implements OnInit {
  misPrestamos: Prestamo[] = [];
  misMultas: Multa[] = [];

  cargandoPrestamos: boolean = false;
  cargandoMultas: boolean = false;

  errorMessage: string = '';
  
  debeCambiarPassword: boolean = false;

  constructor(
    private authService: AuthService,
    private prestamoService: PrestamoService,
    private multaService: MultaService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    // 2. VERIFICAR SI DEBE CAMBIAR CONTRASEÑA
    this.verificarCambioPassword();

    const idPersona = this.authService.getIdPersona();
    if (idPersona) {
      this.cargarDatos(idPersona);
    } else {
      this.errorMessage = 'No se pudo identificar la sesión del estudiante.';
    }
  }

  // 3. NUEVO MÉTODO PARA LEER EL USUARIO Y MOSTRAR LA ALERTA
  verificarCambioPassword(): void {
    const rawUser = localStorage.getItem('usuario') || sessionStorage.getItem('usuario');
    if (rawUser) {
      try {
        const parsed = JSON.parse(rawUser);
        // Busca la propiedad en el JSON (ajusta "passwordCambiada" si tu backend lo llama diferente, por ej: "password_cambiada")
        const passCambiada = parsed.passwordCambiada ?? parsed.usuario?.passwordCambiada;
        
        if (passCambiada === false) {
          this.debeCambiarPassword = true;
        }
      } catch (e) {
        console.error('Error al analizar los datos del usuario', e);
      }
    }
  }

  // 4. NUEVO MÉTODO PARA OCULTAR LA ALERTA
  ocultarAlerta(): void {
    this.debeCambiarPassword = false;
  }

  cargarDatos(idPersona: number): void {
    this.cargandoPrestamos = true;
    this.cargandoMultas = true;
    this.errorMessage = '';

    this.prestamoService.obtenerPorPersona(idPersona).subscribe({
      next: (prestamos) => {
        this.ngZone.run(() => {
          this.misPrestamos = prestamos || [];
          this.cargandoPrestamos = false;

          if (this.misPrestamos.length > 0) {
            const idsPropios = this.misPrestamos
              .map((p) => p.idPrestamo)
              .filter((id): id is number => id !== undefined);

            this.multaService.obtenerTodas().subscribe({
              next: (multas) => {
                this.ngZone.run(() => {
                  this.misMultas = (multas || []).filter((m) => idsPropios.includes(m.idPrestamo));
                  this.cargandoMultas = false;
                  this.cdr.detectChanges();
                });
              },
              error: () => {
                this.ngZone.run(() => {
                  this.cargandoMultas = false;
                  this.cdr.detectChanges();
                });
              }
            });
          } else {
            this.cargandoMultas = false;
          }

          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al consultar sus préstamos.';
          this.cargandoPrestamos = false;
          this.cargandoMultas = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}