import { Injectable } from '@angular/core';
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
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerHistorial(idEstudiante: number): Observable<HistorialPago[]> {
    return this.http.get<HistorialPago[]>(
      `${this.restConstants.getApiURL()}/historial-pagos/estudiante/${idEstudiante}`,
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  obtenerPdfHistorial(idEstudiante: number): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get(
      `${this.restConstants.getApiURL()}/historial-pagos/pdf/estudiante/${idEstudiante}`,
      {
        headers,
        responseType: 'blob',
        withCredentials: true
      }
    );
  }
}