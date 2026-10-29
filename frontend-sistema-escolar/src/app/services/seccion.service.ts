import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Grado {
  idGrado?: number;
  nombre: string;
  nivel: string;
}

export interface Seccion {
  idSeccion?: number;
  idGrado?: number;
  idAnioLectivo?: number;
  idAnoLectivo?: number;
  nombre?: string;
  capacidad?: number;
  estado?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SeccionService {
  private restConstants = new RestConstants();
  private httpOptions = { withCredentials: true };

  constructor(private http: HttpClient) {}

  obtenerSecciones(): Observable<Seccion[]> {
    return this.http.get<Seccion[]>(`${this.restConstants.getApiURL()}/secciones`, this.httpOptions);
  }

  buscarPorId(id: number): Observable<Seccion> {
    return this.http.get<Seccion>(`${this.restConstants.getApiURL()}/secciones/${id}`, this.httpOptions);
  }

  registrarSeccion(seccion: Seccion): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/secciones`, seccion, this.httpOptions);
  }

  actualizarSeccion(id: number, seccion: Seccion): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/secciones/${id}`, seccion, this.httpOptions);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/secciones/${id}/estado`, { estado }, this.httpOptions);
  }
}