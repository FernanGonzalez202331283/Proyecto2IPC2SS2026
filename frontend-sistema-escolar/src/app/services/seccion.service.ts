import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Seccion {
  idSeccion?: number;
  idGrado?: number;
  idAnioLectivo?: number;
  nombre?: string;
  capacidad?: number;
  estado?: boolean;
  nombreGrado?: string;
  anioLectivo?: string | number;
}

export interface GradoOption {
  idGrado: number;
  nombre: string;
}

export interface AnioLectivoOption {
  idAnoLectivo?: number;
  idAnioLectivo?: number;
  anio: string | number;
}

export interface RespuestaPaginada<T> {
  contenido: T[];
  paginaActual: number;
  totalPaginas: number;
  totalRegistros: number;
}

@Injectable({
  providedIn: 'root'
})
export class SeccionService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = this.restConstants.getApiURL();

  obtenerSecciones(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Seccion>> {
    return this.http.get<RespuestaPaginada<Seccion>>(
      `${this.baseUrl}/secciones?pagina=${pagina}&limite=${limite}`
    );
  }

  buscarPorId(id: number): Observable<Seccion> {
    return this.http.get<Seccion>(`${this.baseUrl}/secciones/${id}`);
  }

  registrarSeccion(seccion: Seccion): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/secciones`, seccion);
  }

  actualizarSeccion(id: number, seccion: Seccion): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/secciones/${id}`, seccion);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/secciones/${id}/estado`, { estado });
  }

  obtenerGrados(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/grados?pagina=1&limite=100`);
  }

  obtenerAniosLectivos(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/anos-lectivos?pagina=1&limite=100`);
  }
}