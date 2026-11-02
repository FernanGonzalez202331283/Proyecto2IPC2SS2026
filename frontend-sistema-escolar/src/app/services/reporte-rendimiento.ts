import { Injectable, inject } from '@angular/core';
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
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = `${this.restConstants.getApiURL()}/reportes/rendimiento`;

  private getHeaders(incluirContentType = true): HttpHeaders {
    const token = localStorage.getItem('token');
    let headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    if (incluirContentType) {
      headers = headers.set('Content-Type', 'application/json');
    }

    return headers;
  }

  obtenerReporte(idAnioLectivo: number): Observable<ReporteRendimiento[]> {
    const params = new HttpParams().set('anio', idAnioLectivo.toString());

    return this.http.get<ReporteRendimiento[]>(this.baseUrl, {
      headers: this.getHeaders(),
      params,
      withCredentials: true
    });
  }

  obtenerPdfReporte(idAnioLectivo: number): Observable<Blob> {
    const params = new HttpParams().set('anio', idAnioLectivo.toString());

    return this.http.get(`${this.baseUrl}/pdf`, {
      headers: this.getHeaders(false),
      params,
      responseType: 'blob',
      withCredentials: true
    });
  }
}