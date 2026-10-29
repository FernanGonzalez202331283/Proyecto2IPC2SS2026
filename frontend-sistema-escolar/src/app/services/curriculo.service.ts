import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Curriculo {
  idCurriculo?: number;
  idGrado: number;
  idCurso: number;
  idAnioLectivo: number;
  estado: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CurriculoService {
  private restConstants = new RestConstants();

  private httpOptions = {
    withCredentials: true
  };

  constructor(private http: HttpClient) {}

  obtenerCurriculos(): Observable<Curriculo[]> {
    return this.http.get<Curriculo[]>(`${this.restConstants.getApiURL()}/curriculos`, this.httpOptions);
  }

  buscarPorId(id: number): Observable<Curriculo> {
    return this.http.get<Curriculo>(`${this.restConstants.getApiURL()}/curriculos/${id}`, this.httpOptions);
  }

  registrarCurriculo(curriculo: Curriculo): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/curriculos`, curriculo, this.httpOptions);
  }

  actualizarCurriculo(id: number, curriculo: Curriculo): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/curriculos/${id}`, curriculo, this.httpOptions);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/curriculos/${id}/estado`, { estado }, this.httpOptions);
  }
}