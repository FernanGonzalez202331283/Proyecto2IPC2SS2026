import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Libro {
  idLibro?: number;
  titulo: string;
  autor: string;
  editorial?: string;
  cantidadTotal: number;
  cantidadDisponible: number;
  estado?: boolean;
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
export class LibroService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Obtener libros paginados
  obtenerTodos(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Libro>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<Libro>>(`${this.restConstants.getApiURL()}/libros`, {
      headers: this.getHeaders(),
      params,
      withCredentials: true
    });
  }

  // Obtener catálogo completo de activos
  obtenerCatalogo(): Observable<Libro[]> {
    return this.http.get<Libro[]>(`${this.restConstants.getApiURL()}/libros/catalogo`, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  registrar(libro: Libro): Observable<RespuestaServidor> {
    return this.http.post<RespuestaServidor>(`${this.restConstants.getApiURL()}/libros`, libro, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  actualizar(id: number, libro: Libro): Observable<RespuestaServidor> {
    return this.http.put<RespuestaServidor>(`${this.restConstants.getApiURL()}/libros/${id}`, libro, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  cambiarEstado(id: number, estado: boolean): Observable<RespuestaServidor> {
    return this.http.put<RespuestaServidor>(`${this.restConstants.getApiURL()}/libros/${id}/estado?estado=${estado}`, {}, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }
}