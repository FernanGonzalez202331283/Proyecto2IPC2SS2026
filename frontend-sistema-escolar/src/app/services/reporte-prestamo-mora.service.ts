import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface ReportePrestamoMora {
  idPrestamo: number;
  idPersona: number;
  nombres: string;
  apellidos: string;
  idLibro: number;
  titulo: string;
  autor: string;
  fechaPrestamo: string;
  fechaDevolucionEsperada: string;
  fechaDevolucionReal?: string;
  diasAtraso: number;
  montoMulta: number;
  estadoPrestamo: string;
  estadoMulta: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReportePrestamoMoraService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerReporte(): Observable<ReportePrestamoMora[]> {
    return this.http.get<ReportePrestamoMora[]>(
      `${this.restConstants.getApiURL()}/reportes/prestamos-mora`,
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  obtenerPdfReporte(): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get(
      `${this.restConstants.getApiURL()}/reportes/prestamos-mora/pdf`,
      {
        headers,
        responseType: 'blob',
        withCredentials: true
      }
    );
  }
}