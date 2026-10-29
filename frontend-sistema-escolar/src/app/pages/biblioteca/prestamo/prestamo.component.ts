import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PrestamoService, Prestamo } from '../../../services/prestamo.service';
import { LibroService, Libro } from '../../../services/libro.service';

@Component({
  selector: 'app-prestamo',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, RouterLink],
  templateUrl: './prestamo.component.html'
})
export class PrestamoComponent implements OnInit {
  prestamos: Prestamo[] = [];
  librosActivos: Libro[] = [];
  prestamoForm!: FormGroup;

  cargando: boolean = false;
  guardando: boolean = false;

  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private prestamoService: PrestamoService,
    private libroService: LibroService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.cargarLibrosDisponibles();
    this.cargarPrestamos();
  }

  initForm(): void {
    const hoy = new Date().toISOString().split('T')[0];
    this.prestamoForm = this.fb.group({
      idLibro: [null, [Validators.required]],
      idPersona: [null, [Validators.required, Validators.min(1)]],
      fechaPrestamo: [hoy, [Validators.required]],
      fechaDevolucionEsperada: ['', [Validators.required]]
    });
  }

  cargarLibrosDisponibles(): void {
    this.libroService.obtenerCatalogo().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.librosActivos = (data || []).filter(l => l.cantidadDisponible > 0);
          this.cdr.detectChanges();
        });
      },
      error: () => console.error('Error al cargar libros para préstamos.')
    });
  }

  cargarPrestamos(): void {
    this.cargando = true;
    this.prestamoService.obtenerTodos().subscribe({
      next: (data) => {
        this.ngZone.run(() => {
          this.prestamos = data || [];
          this.cargando = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al obtener historial de préstamos.';
          this.cargando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  guardarPrestamo(): void {
    if (this.prestamoForm.invalid) {
      this.prestamoForm.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.guardando = true;

    const formVal = this.prestamoForm.value;

    const prestamoData: Prestamo = {
      idLibro: Number(formVal.idLibro),
      idPersona: Number(formVal.idPersona),
      fechaPrestamo: formVal.fechaPrestamo,
      fechaDevolucionEsperada: formVal.fechaDevolucionEsperada
    };

    this.prestamoService.registrar(prestamoData).subscribe({
      next: (res) => {
        this.ngZone.run(() => {
          this.successMessage = res?.mensaje || 'Préstamo registrado correctamente.';
          this.guardando = false;
          this.initForm();
          this.cargarLibrosDisponibles();
          this.cargarPrestamos();
        });
      },
      error: (err) => {
        this.ngZone.run(() => {
          this.errorMessage = err?.error?.mensaje || 'Error al registrar el préstamo.';
          this.guardando = false;
          this.cdr.detectChanges();
        });
      }
    });
  }
}