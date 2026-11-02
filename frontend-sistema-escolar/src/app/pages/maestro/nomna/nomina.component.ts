import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Location } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NominaService, NominaAlumno, RespuestaPaginada } from '../../../services/nomina.service';
import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { RestConstants } from '../../../shared/rest-api/rest-constants';

@Component({
  selector: 'app-nomina',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './nomina.component.html'
})
export class NominaComponent implements OnInit {
  nomina: NominaAlumno[] = [];
  grados: any[] = [];
  secciones: any[] = [];

  filtroForm!: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';
  cargando: boolean = false;

  paginaActual: number = 1;
  limite: number = 10;
  totalPaginas: number = 0;
  totalRegistros: number = 0;

  private restConstants = new RestConstants();

  constructor(
    private fb: FormBuilder,
    private nominaService: NominaService,
    private http: HttpClient,
    private router: Router,
    private location: Location,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarCatalogos();
  }

  private initForm(): void {
    const anioActual = new Date().getFullYear();
    this.filtroForm = this.fb.group({
      anio: [anioActual, [Validators.required, Validators.min(2000), Validators.max(2100)]],
      idGrado: [null],
      idSeccion: [null]
    });
  }

  private cargarCatalogos(): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<any>(`${this.restConstants.getApiURL()}/grados`, { headers, withCredentials: true })
      .subscribe({
        next: (data) => {
          this.ngZone.run(() => {
            // Extraer .contenido si viene paginado
            this.grados = data.contenido || data || [];
            this.cdr.detectChanges();
          });
        },
        error: (err) => console.error('Error al cargar grados:', err)
      });

    this.http.get<any>(`${this.restConstants.getApiURL()}/secciones`, { headers, withCredentials: true })
      .subscribe({
        next: (data) => {
          this.ngZone.run(() => {
            this.secciones = data.contenido || data || [];
            this.cdr.detectChanges();
          });
        },
        error: (err) => console.error('Error al cargar secciones:', err)
      });
  }
  onGradoChange(): void {
    this.filtroForm.get('idSeccion')?.setValue(null); 
  }

  esCampoInvalido(controlName: string): boolean {
    const control = this.filtroForm.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  buscarNuevaNomina(): void {
    this.consultarNomina(1);
  }

  consultarNomina(pagina: number): void {
    this.limpiarMensajes();

    if (this.filtroForm.invalid) {
      this.filtroForm.markAllAsTouched();
      this.errorMessage = 'Por favor ingrese un año lectivo válido.';
      return;
    }

    this.cargando = true;
    this.paginaActual = pagina;
    const formVal = this.filtroForm.value;

    this.nominaService.obtenerNomina(formVal.anio, formVal.idGrado, formVal.idSeccion, this.paginaActual, this.limite)
      .subscribe({
        next: (data: RespuestaPaginada<NominaAlumno>) => {
          this.ngZone.run(() => {
            this.nomina = data.contenido || [];
            this.totalRegistros = data.totalRegistros;
            this.totalPaginas = data.totalPaginas;

            if (this.nomina.length === 0) {
              this.successMessage = 'No se encontraron alumnos activos para los criterios seleccionados.';
            }
            this.cargando = false;
            this.cdr.detectChanges();
          });
        },
        error: (err) => {
          this.ngZone.run(() => {
            this.nomina = [];
            this.totalRegistros = 0;
            this.totalPaginas = 0;
            this.errorMessage = this.obtenerTextoRespuesta(err);
            this.cargando = false;
            this.cdr.detectChanges();
          });
        }
      });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.consultarNomina(nuevaPagina);
    }
  }

  volverDashboard(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/maestro/dashboard']);
    }
  }

  private limpiarMensajes(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }

  private obtenerTextoRespuesta(res: any): string {
    if (res?.error?.mensaje) return res.error.mensaje;
    if (typeof res === 'string') {
      try {
        const parsed = JSON.parse(res);
        return parsed.mensaje || res;
      } catch { return res; }
    }
    return 'Error al procesar la solicitud.';
  }
}