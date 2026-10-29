import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
  private restConstants = new RestConstants();
  private httpOptions = { withCredentials: true };

  constructor(private http: HttpClient) {}

  obtenerTodos(): Observable<CursoAsignado[]> {
    return this.http.get<CursoAsignado[]>(
      `${this.restConstants.getApiURL()}/cursos-asignados`,
      this.httpOptions
    );
  }

  obtenerPorEmpleado(idEmpleado: number): Observable<CursoAsignado[]> {
    return this.http.get<CursoAsignado[]>(
      `${this.restConstants.getApiURL()}/cursos-asignados/empleado/${idEmpleado}`,
      this.httpOptions
    );
  }
}