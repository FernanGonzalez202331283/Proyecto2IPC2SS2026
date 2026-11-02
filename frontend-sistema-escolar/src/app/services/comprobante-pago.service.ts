import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface ComprobantePago {
  idComprobante?: number;
  idPago: number;
  numeroComprobante?: string;
  fechaEmision?: string;
  tipo?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ComprobantePagoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  emitirComprobante(idPago: number): Observable<ComprobantePago> {
    return this.http.post<ComprobantePago>(
      `${this.restConstants.getApiURL()}/comprobantes-pago`,
      { idPago },
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  obtenerPdfComprobante(idPago: number): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get(
      `${this.restConstants.getApiURL()}/comprobantes-pago/pdf/${idPago}`,
      {
        headers: headers,
        responseType: 'blob',
        withCredentials: true
      }
    );
  }
}