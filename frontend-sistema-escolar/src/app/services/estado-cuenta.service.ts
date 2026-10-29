import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface DetalleEstadoCuenta {
  idObligacion: number;
  concepto: string;
  descripcion: string;
  monto: number;
  fechaVencimiento: string;
  estado: 'PAGADA' | 'PENDIENTE' | 'VENCIDA' | 'ANULADA';
  idPago: number;
  montoPagado: number;
  fechaPago: string | null;
}

export interface EstadoCuenta {
  idEstudiante: number;
  idAñoLectivo: number;
  totalObligaciones: number;
  totalPagado: number;
  saldoPendiente: number;
  obligaciones: DetalleEstadoCuenta[];
}

@Injectable({
  providedIn: 'root'
})
export class EstadoCuentaService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  consultarEstadoCuenta(idEstudiante: number, idAnioLectivo: number): Observable<EstadoCuenta> {
    return this.http.get<EstadoCuenta>(
      `${this.restConstants.getApiURL()}/estados-cuenta/estudiante/${idEstudiante}/anio-lectivo/${idAnioLectivo}`,
      { headers: this.getHeaders(), withCredentials: true }
    );
  }
}