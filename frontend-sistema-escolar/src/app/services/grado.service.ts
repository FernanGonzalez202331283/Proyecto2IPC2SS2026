import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Grado {
  idGrado?: number;
  nivel: 'PREPRIMARIA' | 'PRIMARIA' | 'BASICO' | 'DIVERSIFICADO' | string;
  nombre: string;
  descripcion?: string;
}

@Injectable({
  providedIn: 'root'
})
export class GradoService {
  private restConstants = new RestConstants();

  private httpOptions = {
    withCredentials: true
  };

  constructor(private http: HttpClient) {}

  obtenerGrados(): Observable<Grado[]> {
    return this.http.get<Grado[]>(`${this.restConstants.getApiURL()}/grados`, this.httpOptions);
  }

  buscarPorId(id: number): Observable<Grado> {
    return this.http.get<Grado>(`${this.restConstants.getApiURL()}/grados/${id}`, this.httpOptions);
  }

  registrarGrado(grado: Grado): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/grados`, grado, this.httpOptions);
  }

  actualizarGrado(id: number, grado: Grado): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/grados/${id}`, grado, this.httpOptions);
  }
}