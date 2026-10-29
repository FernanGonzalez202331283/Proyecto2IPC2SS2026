import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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

@Injectable({
  providedIn: 'root'
})
export class EstudianteService {
  private restConstants = new RestConstants();

  private httpOptions = {
    withCredentials: true
  };

  constructor(private http: HttpClient) {}

  obtenerEstudiantes(): Observable<Estudiante[]> {
    return this.http.get<Estudiante[]>(`${this.restConstants.getApiURL()}/estudiantes`, this.httpOptions);
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