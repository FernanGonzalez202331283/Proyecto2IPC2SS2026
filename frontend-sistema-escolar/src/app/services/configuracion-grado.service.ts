import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface ConfiguracionGrado {
  idConfiguracion?: number;
  nivel: 'PREPRIMARIA' | 'PRIMARIA' | 'BASICO' | 'DIVERSIFICADO' | string;
  nombreGrado: string;
  orden: number;
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
export class ConfiguracionGradoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = `${this.restConstants.getApiURL()}/configuracion-grados`;

  obtenerConfiguraciones(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<ConfiguracionGrado>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<ConfiguracionGrado>>(this.baseUrl, { params });
  }

  buscarPorId(id: number): Observable<ConfiguracionGrado> {
    return this.http.get<ConfiguracionGrado>(`${this.baseUrl}/${id}`);
  }

  registrarConfiguracion(configuracion: ConfiguracionGrado): Observable<any> {
    return this.http.post(this.baseUrl, configuracion);
  }

  actualizarConfiguracion(id: number, configuracion: ConfiguracionGrado): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, configuracion);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/estado`, { estado });
  }
}