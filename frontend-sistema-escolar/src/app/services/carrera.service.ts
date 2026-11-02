import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Carrera {
  idCarrera?: number;
  nombre: string;
  descripcion?: string;
  estado: boolean;
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
export class CarreraService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = `${this.restConstants.getApiURL()}/carreras`;

  obtenerCarreras(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Carrera>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<Carrera>>(this.baseUrl, { params });
  }

  obtenerCarrerasActivas(): Observable<Carrera[]> {
    return this.http.get<Carrera[]>(`${this.baseUrl}/activas`);
  }

  buscarPorId(id: number): Observable<Carrera> {
    return this.http.get<Carrera>(`${this.baseUrl}/${id}`);
  }

  registrarCarrera(carrera: Carrera): Observable<any> {
    return this.http.post(this.baseUrl, carrera);
  }

  actualizarCarrera(id: number, carrera: Carrera): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, carrera);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/estado`, { estado });
  }
}