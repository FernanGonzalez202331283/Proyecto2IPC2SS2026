import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { LoginRequest } from '../../../models/auth/auth';
import { UserRole } from '../../../models/auth/user';
import { ChangeDetectorRef } from '@angular/core';
import { NgZone } from '@angular/core'; 
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html'
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  errorMessage: string = '';
  procesando: boolean = false; 

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone 
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      contraseña: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.procesando = true;
    const credentials: LoginRequest = this.loginForm.value;

    this.authService.login(credentials).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.procesando = false;
          this.redirectByRole(res.rol);
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.procesando = false;
          // Muestra "El usuario se encuentra desactivado." o "Usuario o contraseña incorrectos."
          this.errorMessage = this.obtenerTextoError(err);
          this.cdr.detectChanges(); // Renderizado al instante
        });
      }
    });
  }

  private redirectByRole(role: UserRole): void {
    switch (role) {
      case 'SUPER_ADMIN':
        this.router.navigate(['/superadmin/dashboard']);
        break;
      case 'ADMIN':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'SECRETARIA':
        this.router.navigate(['/secretaria/dashboard']);
        break;
      case 'MAESTRO':
        this.router.navigate(['/maestro/dashboard']);
        break;
      case 'BIBLIOTECARIO':
        this.router.navigate(['/biblioteca/dashboard']);
        break;
      case 'ESTUDIANTE':
        this.router.navigate(['/estudiante/dashboard']);
        break;
      default:
        this.router.navigate(['/login']);
        break;
    }
  }

  private obtenerTextoError(err: any): string {
    if (typeof err.error === 'string') {
      try {
        const parsed = JSON.parse(err.error);
        return parsed.mensaje || err.error;
      } catch {
        return err.error;
      }
    }
    return err.error?.mensaje || 'Credenciales inválidas o servicio no disponible.';
  }
}