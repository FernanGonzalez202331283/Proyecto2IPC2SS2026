import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';
import { EstudianteEncargado } from './estudiante.service';

export interface Inscripcion {
  idInscripcion?: number;
  idEstudiante: number;
  idSeccion: number;
  fechaInscripcion?: string;
  estado?: string;
  encargados?: EstudianteEncargado[];
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
export class InscripcionService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = `${this.restConstants.getApiURL()}/inscripciones`;
  private httpOptions = { withCredentials: true };

  obtenerInscripciones(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Inscripcion>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<Inscripcion>>(this.baseUrl, {
      ...this.httpOptions,
      params
    });
  }

  buscarPorId(id: number): Observable<Inscripcion> {
    return this.http.get<Inscripcion>(`${this.baseUrl}/${id}`, this.httpOptions);
  }

  registrarInscripcion(inscripcion: Inscripcion): Observable<any> {
    return this.http.post(this.baseUrl, inscripcion, this.httpOptions);
  }

  actualizarInscripcion(id: number, inscripcion: Inscripcion): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, inscripcion, this.httpOptions);
  }

  cambiarEstado(id: number, estado: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/estado`, { estado }, this.httpOptions);
  }
}