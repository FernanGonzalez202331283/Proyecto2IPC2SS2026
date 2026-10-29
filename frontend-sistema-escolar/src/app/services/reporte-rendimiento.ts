import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface ReporteRendimiento {
  idEstudiante: number;
  codigoEstudiante: string;
  nombres: string;
  apellidos: string;
  idAnioLectivo: number;
  anioLectivo: number;
  grado: string;
  seccion: string;
  idCurso: number;
  nombreCurso: string;
  promedio: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteRendimientoService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerReporte(idAnioLectivo: number): Observable<ReporteRendimiento[]> {
    const params = new HttpParams().set('anio', idAnioLectivo.toString());

    return this.http.get<ReporteRendimiento[]>(
      `${this.restConstants.getApiURL()}/reportes/rendimiento`,
      {
        headers: this.getHeaders(),
        params,
        withCredentials: true
      }
    );
  }

  obtenerPdfReporte(idAnioLectivo: number): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const params = new HttpParams().set('anio', idAnioLectivo.toString());

    return this.http.get(
      `${this.restConstants.getApiURL()}/reportes/rendimiento/pdf`,
      {
        headers,
        params,
        responseType: 'blob',
        withCredentials: true
      }
    );
  }
}