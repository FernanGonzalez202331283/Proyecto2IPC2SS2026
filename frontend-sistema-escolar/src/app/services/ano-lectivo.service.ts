import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface AnoLectivo {
  idAnoLectivo?: number;
  anio: number;
  fechaInicio: string;
  fechaFin: string;
  estado: boolean;
}

export interface RespuestaPaginada<T> {
  contenido: T[];
  paginaActual: number;
  totalPaginas: number;
  totalRegistros: number;
}

@Injectable({
  providedIn: 'root'
})
export class AnoLectivoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  obtenerAnosLectivos(pagina: number = 1, limite: number = 50): Observable<RespuestaPaginada<AnoLectivo> | AnoLectivo[]> {
    return this.http.get<RespuestaPaginada<AnoLectivo> | AnoLectivo[]>(
      `${this.restConstants.getApiURL()}/anos-lectivos?pagina=${pagina}&limite=${limite}`,
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  buscarPorId(id: number): Observable<AnoLectivo> {
    return this.http.get<AnoLectivo>(
      `${this.restConstants.getApiURL()}/anos-lectivos/${id}`,
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  registrarAnoLectivo(ano: AnoLectivo): Observable<any> {
    return this.http.post(
      `${this.restConstants.getApiURL()}/anos-lectivos`, 
      ano, 
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  actualizarAnoLectivo(id: number, ano: AnoLectivo): Observable<any> {
    return this.http.put(
      `${this.restConstants.getApiURL()}/anos-lectivos/${id}`, 
      ano, 
      { headers: this.getHeaders(), withCredentials: true }
    );
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(
      `${this.restConstants.getApiURL()}/anos-lectivos/${id}/estado`, 
      { estado }, 
      { headers: this.getHeaders(), withCredentials: true }
    );
  }
}