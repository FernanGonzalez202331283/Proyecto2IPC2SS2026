import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { PrestamoService, Prestamo } from '../../../services/prestamo.service';
import { MultaService, Multa, RespuestaPaginada } from '../../../services/multa.service';

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
    // 1. VERIFICAR SI DEBE CAMBIAR CONTRASEÑA
    this.verificarCambioPassword();

    const idPersona = this.authService.getIdPersona();
    if (idPersona) {
      this.cargarDatos(idPersona);
    } else {
      this.errorMessage = 'No se pudo identificar la sesión del estudiante.';
    }
  }

  verificarCambioPassword(): void {
    const rawUser = localStorage.getItem('usuario') || sessionStorage.getItem('usuario');
    if (rawUser) {
      try {
        const parsed = JSON.parse(rawUser);
        const passCambiada = parsed.passwordCambiada ?? parsed.usuario?.passwordCambiada;
        
        if (passCambiada === false) {
          this.debeCambiarPassword = true;
        }
      } catch (e) {
        console.error('Error al analizar los datos del usuario', e);
      }
    }
  }

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

            // Se pasa limite=100 para garantizar que recupere todas las multas al buscar las del usuario
            this.multaService.obtenerTodas(1, 100).subscribe({
              next: (res: RespuestaPaginada<Multa> | any) => {
                this.ngZone.run(() => {
                  // Si res tiene 'contenido', lo usa; si viene como array plano, lo asigna directo
                  const listaMultas: Multa[] = res?.contenido ? res.contenido : (Array.isArray(res) ? res : []);
                  this.misMultas = listaMultas.filter((m) => idsPropios.includes(m.idPrestamo));
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