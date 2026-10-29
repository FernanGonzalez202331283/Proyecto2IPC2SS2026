import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Carrera {
  idCarrera?: number;
  nombre: string;
  descripcion?: string;
  estado: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CarreraService {
  private restConstants = new RestConstants();

  private httpOptions = {
    withCredentials: true
  };

  constructor(private http: HttpClient) {}

  obtenerCarreras(): Observable<Carrera[]> {
    return this.http.get<Carrera[]>(`${this.restConstants.getApiURL()}/carreras`, this.httpOptions);
  }

  buscarPorId(id: number): Observable<Carrera> {
    return this.http.get<Carrera>(`${this.restConstants.getApiURL()}/carreras/${id}`, this.httpOptions);
  }

  registrarCarrera(carrera: Carrera): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/carreras`, carrera, this.httpOptions);
  }

  actualizarCarrera(id: number, carrera: Carrera): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/carreras/${id}`, carrera, this.httpOptions);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/carreras/${id}/estado`, { estado }, this.httpOptions);
  }
}