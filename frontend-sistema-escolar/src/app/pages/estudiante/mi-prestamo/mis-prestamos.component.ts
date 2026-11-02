import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { PrestamoService, Prestamo } from '../../../services/prestamo.service';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-mis-prestamos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mis-prestamos.component.html'
})
export class MisPrestamosComponent implements OnInit {
  misPrestamos: Prestamo[] = [];
  cargando: boolean = false;
  errorMessage: string = '';

  constructor(
    private prestamoService: PrestamoService,
    private authService: AuthService,
    private location: Location,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    const idPersona = this.authService.getIdPersona(); 
    
    if (idPersona) {
      this.cargarPrestamos(idPersona);
    } else {
      this.errorMessage = 'No se pudo identificar tu sesión. Vuelve a iniciar sesión.';
    }
  }

  cargarPrestamos(idPersona: number): void {
    this.cargando = true;
    this.errorMessage = '';

    this.prestamoService.obtenerPorPersona(idPersona).subscribe({
      next: (prestamos) => {
        this.ngZone.run(() => {
          this.misPrestamos = prestamos || [];
          this.cargando = false;
          this.cdr.detectChanges(); 
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al consultar el historial de préstamos.';
          this.cargando = false;
          this.cdr.detectChanges(); 
        });
      }
    });
  }

  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/']);
    }
  }
}