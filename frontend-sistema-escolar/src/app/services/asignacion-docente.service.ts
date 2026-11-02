import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';
import { Seccion } from './seccion.service';

export interface Persona {
  idPersona?: number;
  nombres?: string;
  apellidos?: string;
  dpi?: string;
}

export interface Empleado {
  idEmpleado?: number;
  puesto?: string;
  persona?: Persona;
  estado?: boolean;
}

export interface Curso {
  idCurso?: number;
  nombre?: string;
  estado?: boolean;
}

export interface AsignacionDocente {
  idAsignacion?: number;
  empleado: Empleado;
  curso: Curso;
  seccion: Seccion;
  estado?: boolean;
}

export interface RespuestaPaginada<T> {
  contenido: T[];
  paginaActual: number;
  totalPaginas: number;
  totalRegistros: number;
}

@Injectable({
  providedIn: 'root'
})
export class AsignacionDocenteService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = this.restConstants.getApiURL();

  private httpOptions = {
    withCredentials: true
  };

  obtenerAsignaciones(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<AsignacionDocente>> {
    return this.http.get<RespuestaPaginada<AsignacionDocente>>(
      `${this.baseUrl}/asignaciones-docentes?pagina=${pagina}&limite=${limite}`,
      this.httpOptions
    );
  }

  buscarPorId(id: number): Observable<AsignacionDocente> {
    return this.http.get<AsignacionDocente>(
      `${this.baseUrl}/asignaciones-docentes/${id}`,
      this.httpOptions
    );
  }

  registrarAsignacion(asignacion: AsignacionDocente): Observable<any> {
    return this.http.post<any>(
      `${this.baseUrl}/asignaciones-docentes`,
      asignacion,
      this.httpOptions
    );
  }

  actualizarAsignacion(id: number, asignacion: AsignacionDocente): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/asignaciones-docentes/${id}`,
      asignacion,
      this.httpOptions
    );
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/asignaciones-docentes/${id}/estado`,
      { estado },
      this.httpOptions
    );
  }
}