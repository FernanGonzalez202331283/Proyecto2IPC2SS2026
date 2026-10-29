import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants'; // Ajusta tu ruta

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
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  registrarPago(pago: Pago): Observable<any> {
    return this.http.post<any>(`${this.restConstants.getApiURL()}/pagos`, pago, {
      headers: this.getHeaders(),
      withCredentials: true 
    });
  }
}