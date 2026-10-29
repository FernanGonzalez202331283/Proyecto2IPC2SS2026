import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface ReporteIngresos {
  cantidadPagos: number;
  totalIngresos: number;
  totalInscripciones: number;
  totalColegiaturas: number;
  totalActividades: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteIngresosService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerReporte(): Observable<ReporteIngresos> {
    return this.http.get<ReporteIngresos>(
      `${this.restConstants.getApiURL()}/reportes/ingresos`,
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  obtenerPdfReporte(): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get(
      `${this.restConstants.getApiURL()}/reportes/ingresos/pdf`,
      {
        headers,
        responseType: 'blob',
        withCredentials: true
      }
    );
  }
}