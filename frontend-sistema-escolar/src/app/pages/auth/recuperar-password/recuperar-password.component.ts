import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { RecuperarContrasenaService, RecuperarContrasenaRequest } from '../../../services/recuperar-contrasena.service';

@Component({
  selector: 'app-recuperar-password',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './recuperar-password.html'
})
export class RecuperarPasswordComponent implements OnInit {
  recuperarForm!: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  procesando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private recuperarService: RecuperarContrasenaService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.recuperarForm = this.fb.group({
      username: ['', [Validators.required]],
      passwordAnterior: ['', [Validators.required]],
      nuevoPassword: ['', [Validators.required, Validators.minLength(4)]]
    });
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.recuperarForm.invalid) {
      this.recuperarForm.markAllAsTouched();
      return;
    }

    this.procesando = true;
    const request: RecuperarContrasenaRequest = this.recuperarForm.value;

    this.recuperarService.recuperarContraseña(request).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.procesando = false;
          this.successMessage = res.mensaje || 'Contraseña actualizada correctamente.';
          this.recuperarForm.reset();
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.procesando = false;
          this.errorMessage = this.obtenerTextoError(err);
          this.cdr.detectChanges();
        });
      }
    });
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
    return err.error?.mensaje || 'Error al procesar la solicitud.';
  }
}