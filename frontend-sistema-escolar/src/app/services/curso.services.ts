import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Curso {
  idCurso?: number;
  nombre: string;
  descripcion?: string;
  estado: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CursoService {
  private restConstants = new RestConstants();

  private httpOptions = {
    withCredentials: true
  };

  constructor(private http: HttpClient) {}

  obtenerCursos(): Observable<Curso[]> {
    return this.http.get<Curso[]>(`${this.restConstants.getApiURL()}/cursos`, this.httpOptions);
  }

  buscarPorId(id: number): Observable<Curso> {
    return this.http.get<Curso>(`${this.restConstants.getApiURL()}/cursos/${id}`, this.httpOptions);
  }

  registrarCurso(curso: Curso): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/cursos`, curso, this.httpOptions);
  }

  actualizarCurso(id: number, curso: Curso): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/cursos/${id}`, curso, this.httpOptions);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/cursos/${id}/estado`, { estado }, this.httpOptions);
  }
}