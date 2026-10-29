import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Nota {
  idNota?: number;
  idActividad: number;
  idInscripcion: number;
  calificacion: number;
  fechaRegistro?: string;
  fechaModificada?: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotaService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Obtener todas las notas registradas de un estudiante por su ID de inscripción
  obtenerPorEstudiante(idInscripcion: number): Observable<Nota[]> {
    return this.http.get<Nota[]>(`${this.restConstants.getApiURL()}/notas/estudiante/${idInscripcion}`, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }

  // Guardar o actualizar paquete masivo de notas para la boleta
  guardarMasivo(notas: Nota[]): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/notas/masivo`, notas, {
      headers: this.getHeaders(),
      withCredentials: true
    });
  }
}