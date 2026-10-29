import { Component, ChangeDetectorRef, NgZone, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EstadoCuentaService, EstadoCuenta } from '../../../services/estado-cuenta.service';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-consultar-estado-cuenta',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './consultar-estado-cuenta.component.html'
})
export class ConsultarEstadoCuentaComponent implements OnInit {
  consultaForm: FormGroup;
  cargando: boolean = false;
  estadoCuenta: EstadoCuenta | null = null;
  errorMessage: string = '';
  
  esEstudiante: boolean = false;

  constructor(
    private fb: FormBuilder,
    private estadoCuentaService: EstadoCuentaService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private location: Location,
    private authService: AuthService
  ) {
    this.consultaForm = this.fb.group({
      idEstudiante: ['', [Validators.required, Validators.min(1)]],
      idAnioLectivo: [1, [Validators.required, Validators.min(1)]] 
    });
  }

  ngOnInit(): void {
    const rawUser = localStorage.getItem('usuario') || sessionStorage.getItem('usuario');
    
    if (rawUser) {
      try {
        const parsed = JSON.parse(rawUser);
        const rolUsuario = parsed.rol || parsed.usuario?.rol || '';
        const stringRol = JSON.stringify(rolUsuario).toUpperCase();
        
        if (stringRol.includes('ESTUDIANTE') || stringRol.includes('CLIENTE')) {
          this.esEstudiante = true;
          
          const miId = parsed.idPersona || this.authService.getIdPersona() || parsed.id;
          if (miId) {
            this.consultaForm.patchValue({ idEstudiante: miId });
            this.consultar();
          }
        }
      } catch (e) {
        console.error('Error al leer los datos de sesión:', e);
      }
    }
  }

  volver(): void {
    this.location.back();
  }

  consultar(): void {
    if (this.consultaForm.invalid) return;

    this.cargando = true;
    this.estadoCuenta = null;
    this.errorMessage = '';
    const { idEstudiante, idAnioLectivo } = this.consultaForm.getRawValue();

    this.estadoCuentaService.consultarEstadoCuenta(idEstudiante, idAnioLectivo).subscribe({
      next: (res: EstadoCuenta) => {
        this.ngZone.run(() => {
          this.estadoCuenta = res;
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err.error?.mensaje || 'No se pudo obtener el estado de cuenta.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }
}