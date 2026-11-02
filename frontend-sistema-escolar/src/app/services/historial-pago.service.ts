import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface HistorialPago {
  idPago: number;
  idEstudiante: number;
  codigoEstudiante: string;
  nombres: string;
  apellidos: string;
  idComprobante: number;
  numeroComprobante: string;
  concepto: string;
  monto: number;
  fechaPago: string;
  estado: string;
}

@Injectable({
  providedIn: 'root'
})
export class HistorialPagoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = `${this.restConstants.getApiURL()}/historial-pagos`;

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

  obtenerHistorial(idEstudiante: number): Observable<HistorialPago[]> {
    return this.http.get<HistorialPago[]>(
      `${this.baseUrl}/estudiante/${idEstudiante}`,
      {
        headers: this.getHeaders(),
        withCredentials: true
      }
    );
  }

  obtenerPdfHistorial(idEstudiante: number): Observable<Blob> {
    return this.http.get(
      `${this.baseUrl}/pdf/estudiante/${idEstudiante}`,
      {
        headers: this.getHeaders(false),
        responseType: 'blob',
        withCredentials: true
      }
    );
  }
}