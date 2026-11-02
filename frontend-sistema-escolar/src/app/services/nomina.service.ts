import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

// Asegúrate de que los campos coincidan con los que devuelve tu backend en Java
export interface NominaAlumno {
  idInscripcion: number;
  idEstudiante: number;
  codigoEstudiante: string;
  nombres: string;
  apellidos: string;
  dpi?: string;
  fechaInscripcion?: string;
  estadoInscripcion?: string;
  estadoEstudiante?: string;
  idSeccion: number;
  nombreSeccion?: string;
  idGrado: number;
  nombreGrado?: string;
  nivelGrado?: string;
  idAñoLectivo?: number;
  añoLectivo?: number;
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
export class NominaService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = `${this.restConstants.getApiURL()}/nominas`; // La ruta de tu Resource en Java

  // Fíjate que recibimos año, idGrado, idSeccion, pagina y limite
  obtenerNomina(
    anio: number, 
    idGrado: number, 
    idSeccion: number, 
    pagina: number = 1, 
    limite: number = 10
  ): Observable<RespuestaPaginada<NominaAlumno>> {
    
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${token}` });

    // Armamos los parámetros exactamente como los espera el @QueryParam en Java
    let params = new HttpParams()
      .set('año', anio.toString()) // Java espera "año"
      .set('idGrado', idGrado ? idGrado.toString() : '0')
      .set('idSeccion', idSeccion ? idSeccion.toString() : '0')
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<NominaAlumno>>(this.baseUrl, { 
      headers, 
      params, 
      withCredentials: true 
    });
  }
}