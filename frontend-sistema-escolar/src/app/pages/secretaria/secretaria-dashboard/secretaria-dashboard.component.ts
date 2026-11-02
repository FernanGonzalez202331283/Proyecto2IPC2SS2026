import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
@Component({
  selector: 'app-secretaria-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './secretaria-dashboard.component.html',
})
export class SecretariaDashboardComponent {
  constructor(  
    private authService: AuthService,
    private router: Router
  ) { }
  
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}