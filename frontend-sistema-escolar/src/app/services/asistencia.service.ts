import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Asistencia {
  idAsistencia?: number;
  idInscripcion: number;
  idAsignacion: number;
  fecha: string;
  estado: 'PRESENTE' | 'AUSENTE' | 'TARDE' | 'JUSTIFICADO';
  observacion?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AsistenciaService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  registrarAsistencia(asistencia: Asistencia): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/asistencias`, asistencia, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  obtenerAsistencias(): Observable<Asistencia[]> {
    return this.http.get<Asistencia[]>(`${this.restConstants.getApiURL()}/asistencias`, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  actualizarAsistencia(id: number, asistencia: Asistencia): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/asistencias/${id}`, asistencia, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }
}