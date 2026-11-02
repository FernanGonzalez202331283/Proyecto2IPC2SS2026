import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Pago {
  idPago?: number;
  idObligacion: number;
  idUsuario?: number;
  concepto: string;
  monto: number;
  fechaPago?: string;
  estado?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  registrarPago(pago: Pago): Observable<Pago> {
    return this.http.post<Pago>(`${this.restConstants.getApiURL()}/pagos`, pago, {
      headers: this.getHeaders(),
      withCredentials: true 
    });
  }
}