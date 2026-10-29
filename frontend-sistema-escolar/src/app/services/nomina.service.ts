import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface NominaAlumno {
  idInscripcion: number;
  idEstudiante: number;
  codigoEstudiante: string;
  nombres: string;
  apellidos: string;
  dpi: string;
  fechaInscripcion: string;
  estadoInscripcion: string;
  estadoEstudiante: string;
  idSeccion: number;
  nombreSeccion: string;
  idGrado: number;
  nombreGrado: string;
  nivelGrado: string;
  idAñoLectivo: number;
  añoLectivo: number;
}

@Injectable({
  providedIn: 'root'
})
export class NominaService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  obtenerNomina(anio: number, idGrado?: number, idSeccion?: number): Observable<NominaAlumno[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    let params = new HttpParams().set('año', anio.toString());
    
    if (idGrado) {
      params = params.set('idGrado', idGrado.toString());
    }
    if (idSeccion) {
      params = params.set('idSeccion', idSeccion.toString());
    }

    return this.http.get<NominaAlumno[]>(`${this.restConstants.getApiURL()}/nominas`, {
      headers,
      params,
      withCredentials: true
    });
  }
}