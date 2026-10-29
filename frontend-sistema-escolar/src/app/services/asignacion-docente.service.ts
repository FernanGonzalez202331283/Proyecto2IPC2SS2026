import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

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

export interface Seccion {
  idSeccion?: number;
  nombre?: string;
  idGrado?: number;
  estado?: boolean;
}

export interface AsignacionDocente {
  idAsignacion?: number;
  empleado: Empleado;
  curso: Curso;
  seccion: Seccion;
  estado?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AsignacionDocenteService {
  private restConstants = new RestConstants();
  private httpOptions = { withCredentials: true };

  constructor(private http: HttpClient) {}

  obtenerAsignaciones(): Observable<AsignacionDocente[]> {
    return this.http.get<AsignacionDocente[]>(
      `${this.restConstants.getApiURL()}/asignaciones-docentes`,
      this.httpOptions
    );
  }

  buscarPorId(id: number): Observable<AsignacionDocente> {
    return this.http.get<AsignacionDocente>(
      `${this.restConstants.getApiURL()}/asignaciones-docentes/${id}`,
      this.httpOptions
    );
  }

  registrarAsignacion(asignacion: AsignacionDocente): Observable<any> {
    return this.http.post(
      `${this.restConstants.getApiURL()}/asignaciones-docentes`,
      asignacion,
      this.httpOptions
    );
  }

  actualizarAsignacion(id: number, asignacion: AsignacionDocente): Observable<any> {
    return this.http.put(
      `${this.restConstants.getApiURL()}/asignaciones-docentes/${id}`,
      asignacion,
      this.httpOptions
    );
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(
      `${this.restConstants.getApiURL()}/asignaciones-docentes/${id}/estado`,
      { estado },
      this.httpOptions
    );
  }
}