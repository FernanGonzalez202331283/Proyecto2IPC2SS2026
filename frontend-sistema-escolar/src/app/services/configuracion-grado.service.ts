import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface ConfiguracionGrado {
  idConfiguracion?: number;
  nivel: 'PREPRIMARIA' | 'PRIMARIA' | 'BASICO' | 'DIVERSIFICADO' | string;
  nombreGrado: string;
  orden: number;
  estado: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionGradoService {
  private restConstants = new RestConstants();

  private httpOptions = {
    withCredentials: true
  };

  constructor(private http: HttpClient) {}

  obtenerConfiguraciones(): Observable<ConfiguracionGrado[]> {
    return this.http.get<ConfiguracionGrado[]>(`${this.restConstants.getApiURL()}/configuracion-grados`, this.httpOptions);
  }

  buscarPorId(id: number): Observable<ConfiguracionGrado> {
    return this.http.get<ConfiguracionGrado>(`${this.restConstants.getApiURL()}/configuracion-grados/${id}`, this.httpOptions);
  }

  registrarConfiguracion(configuracion: ConfiguracionGrado): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/configuracion-grados`, configuracion, this.httpOptions);
  }

  actualizarConfiguracion(id: number, configuracion: ConfiguracionGrado): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/configuracion-grados/${id}`, configuracion, this.httpOptions);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/configuracion-grados/${id}/estado`, { estado }, this.httpOptions);
  }
}