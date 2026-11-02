import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Grado {
  idGrado?: number;
  nivel: string;
  idCarrera?: number | null;
  nombre: string;
  descripcion?: string;
  nombreCarrera?: string;
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
export class GradoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = `${this.restConstants.getApiURL()}/grados`;

  obtenerGrados(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Grado>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<Grado>>(this.baseUrl, { params });
  }

  buscarPorId(id: number): Observable<Grado> {
    return this.http.get<Grado>(`${this.baseUrl}/${id}`);
  }

  registrarGrado(grado: Grado): Observable<any> {
    return this.http.post(this.baseUrl, grado);
  }

  actualizarGrado(id: number, grado: Grado): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, grado);
  }
}