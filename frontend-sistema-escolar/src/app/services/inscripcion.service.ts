import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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

@Injectable({
  providedIn: 'root'
})
export class InscripcionService {
  private restConstants = new RestConstants();
  private httpOptions = { withCredentials: true };

  constructor(private http: HttpClient) {}

  obtenerInscripciones(): Observable<Inscripcion[]> {
    return this.http.get<Inscripcion[]>(`${this.restConstants.getApiURL()}/inscripciones`, this.httpOptions);
  }

  buscarPorId(id: number): Observable<Inscripcion> {
    return this.http.get<Inscripcion>(`${this.restConstants.getApiURL()}/inscripciones/${id}`, this.httpOptions);
  }

  registrarInscripcion(inscripcion: Inscripcion): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/inscripciones`, inscripcion, this.httpOptions);
  }

  actualizarInscripcion(id: number, inscripcion: Inscripcion): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/inscripciones/${id}`, inscripcion, this.httpOptions);
  }

  cambiarEstado(id: number, estado: string): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/inscripciones/${id}/estado`, { estado }, this.httpOptions);
  }
}