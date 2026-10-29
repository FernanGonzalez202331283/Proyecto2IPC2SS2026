import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Obligacion {
  idObligacion: number;
  idEstudiante: number;
  idAñoLectivo: number;
  concepto: string;
  descripcion: string;
  monto: number;
  fechaVencimiento: string;
  estado: string;
}

@Injectable({
  providedIn: 'root'
})
export class ObligacionService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Llama al endpoint de tu backend que trae las obligaciones de un estudiante
  obtenerPorEstudiante(idEstudiante: number): Observable<Obligacion[]> {
    return this.http.get<Obligacion[]>(`${this.restConstants.getApiURL()}/obligaciones/estudiante/${idEstudiante}`, {
      headers: this.getHeaders(),
      withCredentials: true 
    });
  }
}