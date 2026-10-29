import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants'; // Ajusta la ruta a tus constantes

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
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Registra la emisión del comprobante en BD
  emitirComprobante(idPago: number): Observable<ComprobantePago> {
    return this.http.post<ComprobantePago>(
      `${this.restConstants.getApiURL()}/comprobantes-pago`,
      { idPago },
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  // Obtiene el PDF en formato de Array de bytes / Blob para visualización/descarga
  obtenerPdfComprobante(idPago: number): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get(
      `${this.restConstants.getApiURL()}/comprobantes-pago/pdf/${idPago}`,
      {
        headers: headers,
        responseType: 'blob', // Especificamos que la respuesta es un archivo PDF binario
        withCredentials: true
      }
    );
  }
}