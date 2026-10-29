import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Perfil {
  idUsuario: number;
  idPersona: number;
  nombreUsuario: string;
  rol: string;
  nombres: string;
  apellidos: string;
  dpi: string;
  telefono: string;
  correo: string;
  direccion: string;
  estadoPersona: boolean;
  tipoPerfil: 'ESTUDIANTE' | 'EMPLEADO' | 'ADMINISTRATIVO';
  codigoEstudiante?: string;
  puesto?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PerfilService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  // Obtener el perfil mediante el ID del usuario
  obtenerPerfilPorUsuario(idUsuario: number): Observable<Perfil> {
    return this.http.get<Perfil>(
      `${this.restConstants.getApiURL()}/perfil/usuario/${idUsuario}`,
      { headers: this.getHeaders(), withCredentials: true }
    );
  }
}