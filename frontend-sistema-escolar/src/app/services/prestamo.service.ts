import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Prestamo {
  idPrestamo?: number;
  idLibro: number;
  idPersona: number;
  fechaPrestamo: string;
  fechaDevolucionEsperada: string;
  fechaDevolucionReal?: string;
  estado?: string;
}

export interface RespuestaServidor {
  mensaje: string;
}

@Injectable({
  providedIn: 'root'
})
export class PrestamoService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerTodos(): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(`${this.restConstants.getApiURL()}/prestamos`, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  // MÉTODO AGREGADO PARA EL CASO DE USO CU0028
  obtenerPorPersona(idPersona: number): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(`${this.restConstants.getApiURL()}/prestamos/persona/${idPersona}`, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  registrar(prestamo: Prestamo): Observable<RespuestaServidor> {
    return this.http.post<RespuestaServidor>(`${this.restConstants.getApiURL()}/prestamos`, prestamo, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  registrarDevolucion(idPrestamo: number): Observable<RespuestaServidor> {
    return this.http.put<RespuestaServidor>(`${this.restConstants.getApiURL()}/prestamos/${idPrestamo}/devolucion`, {}, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }
}