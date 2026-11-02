import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Prestamo {
  idPrestamo?: number;
  idLibro: number;
  idPersona: number;
  nombrePersona?: string; 
  tituloLibro?: string; // <-- Campo agregado para resolver el error de compilación
  fechaPrestamo: string;
  fechaDevolucionEsperada: string;
  fechaDevolucionReal?: string;
  estado?: string;
}

export interface PersonaSeleccionable {
  idPersona: number;
  nombreCompleto: string;
  dpi?: string;
  tipoPersona?: string;
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

  obtenerTodos(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Prestamo>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<Prestamo>>(`${this.restConstants.getApiURL()}/prestamos`, {
      headers: this.getHeaders(),
      params,
      withCredentials: true
    });
  }

  // Obtener personas activas para el selector
  obtenerPersonasActivas(): Observable<PersonaSeleccionable[]> {
    return this.http.get<PersonaSeleccionable[]>(`${this.restConstants.getApiURL()}/personas/activas`, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

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