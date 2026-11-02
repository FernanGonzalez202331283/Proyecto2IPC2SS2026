import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface CursoAsignado {
  idAsignacion?: number;
  idEmpleado?: number;
  nombresMaestro?: string;
  apellidosMaestro?: string;
  idCurso?: number;
  nombreCurso?: string;
  idSeccion?: number;
  nombreSeccion?: string;
  idGrado?: number;
  nombreGrado?: string;
  nivelGrado?: string;
  idAñoLectivo?: number;
  anioLectivo?: number;
  estado?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CursoAsignadoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerTodos(): Observable<CursoAsignado[]> {
    return this.http.get<CursoAsignado[]>(
      `${this.restConstants.getApiURL()}/cursos-asignados`,
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  obtenerPorEmpleado(idEmpleado: number): Observable<CursoAsignado[]> {
    return this.http.get<CursoAsignado[]>(
      `${this.restConstants.getApiURL()}/cursos-asignados/empleado/${idEmpleado}`,
      { headers: this.getHeaders(), withCredentials: true }
    );
  }
}