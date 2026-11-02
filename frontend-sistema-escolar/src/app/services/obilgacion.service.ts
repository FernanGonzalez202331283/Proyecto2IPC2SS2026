import { Injectable, inject } from '@angular/core';
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
  idPago?: number; // Propiedad clave agregada para el botón del comprobante PDF
}

@Injectable({
  providedIn: 'root'
})
export class ObligacionService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerPorEstudiante(idEstudiante: number): Observable<Obligacion[]> {
    return this.http.get<Obligacion[]>(`${this.restConstants.getApiURL()}/obligaciones/estudiante/${idEstudiante}`, {
      headers: this.getHeaders(),
      withCredentials: true 
    });
  }
}