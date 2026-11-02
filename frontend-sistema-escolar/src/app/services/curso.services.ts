import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Curso {
  idCurso?: number;
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
export class CursoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = `${this.restConstants.getApiURL()}/cursos`;

  obtenerCursos(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Curso>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<Curso>>(this.baseUrl, { params });
  }

  buscarPorId(id: number): Observable<Curso> {
    return this.http.get<Curso>(`${this.baseUrl}/${id}`);
  }

  registrarCurso(curso: Curso): Observable<any> {
    return this.http.post(this.baseUrl, curso);
  }

  actualizarCurso(id: number, curso: Curso): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, curso);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/estado`, { estado });
  }
}