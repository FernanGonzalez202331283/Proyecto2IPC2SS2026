import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Multa {
  idMulta?: number;
  idPrestamo: number;
  diasAtraso: number;
  monto: number;
  fechaGeneracion?: string;
  estado: string; // 'PENDIENTE', 'PAGADA', 'ANULADA'
}

export interface RespuestaServidor {
  mensaje: string;
}

@Injectable({
  providedIn: 'root'
})
export class MultaService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerTodas(): Observable<Multa[]> {
    return this.http.get<Multa[]>(`${this.restConstants.getApiURL()}/multas`, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  actualizarEstado(idMulta: number, estado: string): Observable<RespuestaServidor> {
    return this.http.put<RespuestaServidor>(`${this.restConstants.getApiURL()}/multas/${idMulta}/estado`, { estado }, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }
}