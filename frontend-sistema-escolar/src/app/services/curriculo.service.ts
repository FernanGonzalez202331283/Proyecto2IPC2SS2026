import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Curriculo {
  idCurriculo?: number;
  idGrado: number;
  idCurso: number;
  idAnioLectivo: number;
  descripcion?: string;
  estado: boolean;
  
  nombreGrado?: string;
  nivelGrado?: string;
  nombreCarrera?: string;
  nombreCurso?: string;
  anioLectivo?: number;
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
export class CurriculoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = `${this.restConstants.getApiURL()}/curriculos`;

  obtenerCurriculos(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Curriculo>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<Curriculo>>(this.baseUrl, { params });
  }

  buscarPorId(id: number): Observable<Curriculo> {
    return this.http.get<Curriculo>(`${this.baseUrl}/${id}`);
  }

  registrarCurriculo(curriculo: Curriculo): Observable<any> {
    return this.http.post(this.baseUrl, curriculo);
  }

  actualizarCurriculo(id: number, curriculo: Curriculo): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, curriculo);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/estado`, { estado });
  }
}