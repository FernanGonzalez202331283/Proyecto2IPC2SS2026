import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
@Component({
  selector: 'app-superadmin-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './superadmin-dashboard.component.html',
})
export class SuperAdminDashboardComponent {
constructor(
  private authService: AuthService,
  private router: Router
) { }

logout(): void {
  this.authService.logout();
  this.router.navigate(['/login']); if (this.authService && typeof this.authService.logout === 'function') {
      this.authService.logout();
    } else {
      localStorage.clear();
      sessionStorage.clear();
    }

    // Redirección inmediata a la pantalla de inicio de sesión
    this.router.navigate(['/login']);
}
}