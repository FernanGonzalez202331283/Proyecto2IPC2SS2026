import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Actividad {
  idActividad?: number;
  idAsignacion: number;
  nombre: string;
  descripcion?: string;
  porcentaje: number;
  fecha: string;
  estado: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ActividadService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerPorAsignacion(idAsignacion: number): Observable<Actividad[]> {
    return this.http.get<Actividad[]>(`${this.restConstants.getApiURL()}/actividades/asignacion/${idAsignacion}`, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  registrar(actividad: Actividad): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/actividades`, actividad, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  actualizar(id: number, actividad: Actividad): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/actividades/${id}`, actividad, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/actividades/${id}/estado?estado=${estado}`, {}, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }
}