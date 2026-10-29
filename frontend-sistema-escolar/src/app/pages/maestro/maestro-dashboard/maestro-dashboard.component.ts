import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-maestro-dashboard',
  imports: [RouterLink],
  standalone: true,
  templateUrl: './maestro-dashboard.component.html',
})
export class MaestroDashboardComponent {
  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}