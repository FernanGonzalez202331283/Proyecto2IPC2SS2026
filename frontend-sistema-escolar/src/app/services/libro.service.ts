import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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

  // Obtener todos los libros (para administradores y bibliotecarios)
  obtenerTodos(): Observable<Libro[]> {
    return this.http.get<Libro[]>(`${this.restConstants.getApiURL()}/libros`, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  // Obtener catálogo de libros activos
  obtenerCatalogo(): Observable<Libro[]> {
    return this.http.get<Libro[]>(`${this.restConstants.getApiURL()}/libros/catalogo`, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  // Registrar un nuevo libro
  registrar(libro: Libro): Observable<RespuestaServidor> {
    return this.http.post<RespuestaServidor>(`${this.restConstants.getApiURL()}/libros`, libro, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  // Actualizar un libro existente
  actualizar(id: number, libro: Libro): Observable<RespuestaServidor> {
    return this.http.put<RespuestaServidor>(`${this.restConstants.getApiURL()}/libros/${id}`, libro, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  // Cambiar estado (activar/desactivar)
  cambiarEstado(id: number, estado: boolean): Observable<RespuestaServidor> {
    return this.http.put<RespuestaServidor>(`${this.restConstants.getApiURL()}/libros/${id}/estado?estado=${estado}`, {}, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }
}