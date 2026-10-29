import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface ReporteSolvencia {
  idEstudiante: number;
  codigoEstudiante: string;
  nombres: string;
  apellidos: string;
  idAnioLectivo: number;
  anioLectivo: number;
  grado: string;
  seccion: string;
  totalObligaciones: number;
  totalPagado: number;
  saldoPendiente: number;
  estadoSolvencia: string; 
}

@Injectable({
  providedIn: 'root'
})
export class ReporteSolvenciaService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Obtiene el JSON para mostrar la tabla interactiva
  obtenerReporte(idAnioLectivo: number, estado?: string): Observable<ReporteSolvencia[]> {
    let params = new HttpParams().set('anio', idAnioLectivo.toString());
    if (estado && estado !== 'TODOS') {
      params = params.set('estado', estado);
    }

    return this.http.get<ReporteSolvencia[]>(
      `${this.restConstants.getApiURL()}/reportes/solvencia`,
      {
        headers: this.getHeaders(),
        params: params,
        withCredentials: true
      }
    );
  }

  // Obtiene el archivo PDF binario generado por JasperReports
  obtenerPdfReporte(idAnioLectivo: number, estado?: string): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    let params = new HttpParams().set('anio', idAnioLectivo.toString());
    if (estado && estado !== 'TODOS') {
      params = params.set('estado', estado);
    }

    return this.http.get(
      `${this.restConstants.getApiURL()}/reportes/solvencia/pdf`,
      {
        headers: headers,
        params: params,
        responseType: 'blob',
        withCredentials: true
      }
    );
  }
}