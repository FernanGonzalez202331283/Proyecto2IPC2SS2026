import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './admin-dashboard.components.html',
})
export class AdminDashboardComponent {

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  logout(): void {
   if (this.authService && typeof this.authService.logout === 'function') {
      this.authService.logout();
    } else {
      localStorage.clear();
      sessionStorage.clear();
    }
    
    this.router.navigate(['/login']);
  }
}