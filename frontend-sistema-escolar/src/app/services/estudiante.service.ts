import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Persona {
  idPersona?: number;
  nombres: string;
  apellidos: string;
  dpi: string;
  telefono?: string;
  correo?: string;
  direccion?: string;
  estado?: boolean;
}

export interface Encargado {
  idEncargado?: number;
  nombres: string;
  apellidos: string;
  telefono?: string;
  correo?: string;
  direccion?: string;
  estado?: boolean;
}

export interface EstudianteEncargado {
  idEstudiante?: number;
  idEncargado: number;
  parentesco: string;
  responsablePrincipal: boolean;
  encargado?: Encargado;
}

export interface Estudiante {
  idEstudiante?: number;
  idPersona?: number;
  codigoEstudiante: string;
  informacionMedica?: string;
  estado: string; 
  persona: Persona;
  encargados?: EstudianteEncargado[];
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
export class EstudianteService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();

  private httpOptions = {
    withCredentials: true
  };

  obtenerEstudiantes(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Estudiante>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<Estudiante>>(
      `${this.restConstants.getApiURL()}/estudiantes`,
      { ...this.httpOptions, params }
    );
  }

  buscarPorId(id: number): Observable<Estudiante> {
    return this.http.get<Estudiante>(`${this.restConstants.getApiURL()}/estudiantes/${id}`, this.httpOptions);
  }

  registrarEstudiante(estudiante: Estudiante): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/estudiantes`, estudiante, this.httpOptions);
  }

  actualizarEstudiante(id: number, estudiante: Estudiante): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/estudiantes/${id}`, estudiante, this.httpOptions);
  }

  cambiarEstado(id: number, estado: string): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/estudiantes/${id}/estado`, { estado }, this.httpOptions);
  }
}