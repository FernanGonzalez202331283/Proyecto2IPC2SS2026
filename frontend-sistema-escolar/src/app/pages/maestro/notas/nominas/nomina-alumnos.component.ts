import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NominaService, NominaAlumno, RespuestaPaginada } from '../../../../services/nomina.service';
import { RestConstants } from '../../../../shared/rest-api/rest-constants';

@Component({
  selector: 'app-nomina-alumnos',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './nomina-alumnos.component.html'
})
export class NominaAlumnosComponent implements OnInit {
  misAsignaciones: any[] = [];
  alumnos: NominaAlumno[] = [];
  filtroForm!: FormGroup;

  cargandoAlumnos: boolean = false;
  errorMessage: string = '';

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
    this.filtroForm = this.fb.group({
      idAsignacion: [null, [Validators.required]],
      anio: [new Date().getFullYear()]
    });

    this.cargarMisAsignaciones();
  }

  cargarMisAsignaciones(): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    this.http.get<any>(`${this.restConstants.getApiURL()}/asistencias/mis-asignaciones`, { headers, withCredentials: true })
      .subscribe({
        next: (data) => {
          this.ngZone.run(() => {
            this.misAsignaciones = data.contenido || data || [];
            this.cdr.detectChanges();
          });
        },
        error: (err) => console.error('Error al cargar asignaciones:', err)
      });
  }

  buscarNuevosEstudiantes(): void {
    this.cargarEstudiantes(1);
  }

  cargarEstudiantes(pagina: number): void {
    this.errorMessage = '';
    const formVal = this.filtroForm.value;

    if (!formVal.idAsignacion) {
      this.errorMessage = 'Seleccione una sección / grado.';
      return;
    }

    const asig = this.misAsignaciones.find(a => a.idAsignacion === Number(formVal.idAsignacion));
    this.cargandoAlumnos = true;
    this.paginaActual = pagina;

    this.nominaService.obtenerNomina(formVal.anio, asig?.seccion?.idGrado, asig?.seccion?.idSeccion, this.paginaActual, this.limite).subscribe({
      next: (data: RespuestaPaginada<NominaAlumno>) => {
        this.ngZone.run(() => {
          this.alumnos = data.contenido || [];
          this.totalRegistros = data.totalRegistros;
          this.totalPaginas = data.totalPaginas;
          
          this.cargandoAlumnos = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.alumnos = [];
          this.totalRegistros = 0;
          this.totalPaginas = 0;
          this.errorMessage = err?.error?.mensaje || 'Error al obtener nómina de alumnos.';
          this.cargandoAlumnos = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas) {
      this.cargarEstudiantes(nuevaPagina);
    }
  }

  irABoleta(alumno: NominaAlumno): void {
    const idAsignacion = this.filtroForm.get('idAsignacion')?.value;
    this.router.navigate(['/maestro/boleta-calificaciones', alumno.idInscripcion], {
      queryParams: { idAsignacionRef: idAsignacion }
    });
  }

  // MÉTODO PARA REGRESAR A LA PÁGINA ANTERIOR O AL DASHBOARD
  volver(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/maestro/dashboard']);
    }
  }
}