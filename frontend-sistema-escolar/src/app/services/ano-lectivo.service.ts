import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface AnoLectivo {
  idAnoLectivo?: number; // Coincide exactamente con getIdAnoLectivo() en Java
  anio: number;          // Coincide exactamente con getAnio() en Java
  fechaInicio: string;
  fechaFin: string;
  estado: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AnoLectivoService {
  private restConstants = new RestConstants();

  // Permite enviar las cookies de sesión activa hacia la validación de roles en Java
  private httpOptions = {
    withCredentials: true
  };

  constructor(private http: HttpClient) {}

  obtenerAnosLectivos(): Observable<AnoLectivo[]> {
    return this.http.get<AnoLectivo[]>(`${this.restConstants.getApiURL()}/anos-lectivos`, this.httpOptions);
  }

  buscarPorId(id: number): Observable<AnoLectivo> {
    return this.http.get<AnoLectivo>(`${this.restConstants.getApiURL()}/anos-lectivos/${id}`, this.httpOptions);
  }

  registrarAnoLectivo(ano: AnoLectivo): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/anos-lectivos`, ano, this.httpOptions);
  }

  actualizarAnoLectivo(id: number, ano: AnoLectivo): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/anos-lectivos/${id}`, ano, this.httpOptions);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/anos-lectivos/${id}/estado`, { estado }, this.httpOptions);
  }
}