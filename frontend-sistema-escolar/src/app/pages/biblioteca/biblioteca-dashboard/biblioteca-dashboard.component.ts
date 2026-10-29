import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-biblioteca-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './biblioteca-dashboard.component.html',
})
export class BibliotecaDashboardComponent {

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}