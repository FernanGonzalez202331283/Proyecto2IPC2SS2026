import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NominaService, NominaAlumno } from '../../../../services/nomina.service';
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

  private restConstants = new RestConstants();

  constructor(
    private fb: FormBuilder,
    private nominaService: NominaService,
    private http: HttpClient,
    private router: Router,
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

    this.http.get<any[]>(`${this.restConstants.getApiURL()}/asistencias/mis-asignaciones`, { headers, withCredentials: true })
      .subscribe({
        next: (data) => {
          this.ngZone.run(() => {
            this.misAsignaciones = data || [];
            this.cdr.detectChanges();
          });
        },
        error: (err) => console.error('Error al cargar asignaciones:', err)
      });
  }

  cargarEstudiantes(): void {
    this.errorMessage = '';
    const formVal = this.filtroForm.value;

    if (!formVal.idAsignacion) {
      this.errorMessage = 'Seleccione una sección / grado.';
      return;
    }

    const asig = this.misAsignaciones.find(a => a.idAsignacion === Number(formVal.idAsignacion));
    this.cargandoAlumnos = true;

    this.nominaService.obtenerNomina(formVal.anio, asig?.seccion?.idGrado, asig?.seccion?.idSeccion).subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.alumnos = data || [];
          this.cargandoAlumnos = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al obtener nómina de alumnos.';
          this.cargandoAlumnos = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  irABoleta(alumno: NominaAlumno): void {
    const idAsignacion = this.filtroForm.get('idAsignacion')?.value;
    // Navegación limpia al segundo componente
    this.router.navigate(['/maestro/boleta-calificaciones', alumno.idInscripcion], {
      queryParams: { idAsignacionRef: idAsignacion }
    });
  }
}