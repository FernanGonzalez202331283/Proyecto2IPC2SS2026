import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Solvencia {
  idEstudiante: number;
  idAñoLectivo: number;
  estado: 'SOLVENTE' | 'NO_SOLVENTE';
  mensaje: string;
}

@Injectable({
  providedIn: 'root'
})
export class SolvenciaService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  // 1. Consultar estado de solvencia (corregido: anio-lectivo)
  consultarSolvencia(idEstudiante: number, idAñoLectivo: number): Observable<Solvencia> {
    return this.http.get<Solvencia>(
      `${this.restConstants.getApiURL()}/solvencias/estudiante/${idEstudiante}/anio-lectivo/${idAñoLectivo}`,
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  // 2. Descargar constancia PDF (corregido: anio-lectivo)
  descargarPdfSolvencia(idEstudiante: number, idAñoLectivo: number): Observable<Blob> {
    return this.http.get(
      `${this.restConstants.getApiURL()}/solvencias/pdf/estudiante/${idEstudiante}/anio-lectivo/${idAñoLectivo}`,
      {
        headers: this.getHeaders(),
        responseType: 'blob',
        withCredentials: true
      }
    );
  }
}