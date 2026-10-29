import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface DetalleCalificacion {
  idNota: number;
  nombreActividad: string;
  descripcion: string;
  porcentaje: number;
  calificacion: number;
  fecha: string;
}

export interface HistorialCurso {
  idInscripcion: number;
  idAsignacion: number;
  idCurso: number;
  nombreCurso: string;
  anioLectivo: number; // Renombrado sin 'ñ' para evitar errores sintácticos
  grado: string;
  seccion: string;
  promedio: number;
  calificaciones: DetalleCalificacion[];
}

export interface HistorialAcademico {
  idEstudiante: number;
  codigoEstudiante: string;
  nombres: string;
  apellidos: string;
  cursos: HistorialCurso[];
}

@Injectable({
  providedIn: 'root'
})
export class HistorialAcademicoService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  obtenerHistorialEstudiante(idEstudiante: number): Observable<HistorialAcademico> {
    return this.http.get<HistorialAcademico>(
      `${this.restConstants.getApiURL()}/historial-academico/estudiante/${idEstudiante}`,
      { headers: this.getHeaders() }
    );
  }
}