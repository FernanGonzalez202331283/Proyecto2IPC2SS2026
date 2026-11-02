import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Multa {
  idMulta?: number;
  idPrestamo: number;
  diasAtraso: number;
  monto: number;
  fechaGeneracion?: string;
  estado: string; 
}

export interface RespuestaPaginada<T> {
  contenido: T[];
  paginaActual: number;
  totalPaginas: number;
  totalRegistros: number;
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

  obtenerTodas(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Multa>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<Multa>>(`${this.restConstants.getApiURL()}/multas`, {
      headers: this.getHeaders(),
      params,
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