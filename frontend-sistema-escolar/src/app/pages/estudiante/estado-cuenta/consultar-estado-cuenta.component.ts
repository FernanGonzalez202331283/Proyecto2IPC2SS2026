import { Component, ChangeDetectorRef, NgZone, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EstadoCuentaService, EstadoCuenta } from '../../../services/estado-cuenta.service';
import { AuthService } from '../../../core/services/auth'; // <-- IMPORTANTE: Inyectamos el AuthService

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
  
  // Variable para saber si ocultamos el cuadro de ID
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
      idAnioLectivo: [1, [Validators.required, Validators.min(1)]] // Por defecto año 1
    });
  }

  ngOnInit(): void {
    // Leemos quién inició sesión directamente de localStorage
    const rawUser = localStorage.getItem('usuario') || sessionStorage.getItem('usuario');
    
    if (rawUser) {
      try {
        const parsed = JSON.parse(rawUser);
        const rolUsuario = parsed.rol || parsed.usuario?.rol || parsed.role || parsed.authorities;
        const stringRol = JSON.stringify(rolUsuario).toUpperCase();
        
        // Si el usuario logueado es estudiante...
        if (stringRol.includes('ESTUDIANTE')) {
          this.esEstudiante = true; // Esto ocultará el campo "ID Estudiante" en el HTML
          
          // Sacamos su ID
          const miId = this.authService.getIdPersona() || parsed.idPersona || parsed.usuario?.idPersona; 
          
          if (miId) {
            // Llenamos el formulario invisible con su ID
            this.consultaForm.patchValue({ idEstudiante: miId });
            
            // Hacemos la consulta automáticamente sin que el estudiante toque nada
            this.consultar();
          }
        }
      } catch (e) {
        console.error('Error al leer los datos del usuario', e);
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

    // Usamos getRawValue para obtener el ID aunque el campo no esté visible
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