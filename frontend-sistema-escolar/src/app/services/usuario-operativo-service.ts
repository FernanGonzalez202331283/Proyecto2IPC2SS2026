import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface PersonaOperativo {
  idPersona?: number;
  nombres: string;
  apellidos: string;
  dpi: string;
  telefono?: string;
  correo?: string;
  direccion?: string;
  estado?: boolean;
}

export interface EmpleadoOperativo {
  idEmpleado?: number;
  puesto: string;
  fechaContratacion: string;
  salario: number;
  estado?: boolean;
}

export interface UsuarioOperativo {
  idUsuario?: number;
  username: string;
  contraseña?: string;
  idPersona?: number;
  rol: 'SECRETARIA' | 'MAESTRO' | 'BIBLIOTECARIO' | 'PERSONAL' | string;
  estado?: boolean;
  fechaCreacion?: string;
  persona?: PersonaOperativo;
}

export interface UsuarioOperativoDTO {
  persona: PersonaOperativo;
  empleado: EmpleadoOperativo;
  usuario?: UsuarioOperativo;
  crearCuentaUsuario: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioOperativoService {
  private restConstants = new RestConstants();
  private httpOptions = { withCredentials: true };

  constructor(private http: HttpClient) {}

  obtenerUsuarios(): Observable<UsuarioOperativo[]> {
    return this.http.get<UsuarioOperativo[]>(`${this.restConstants.getApiURL()}/usuarios`, this.httpOptions);
  }

  buscarPorId(id: number): Observable<UsuarioOperativo> {
    return this.http.get<UsuarioOperativo>(`${this.restConstants.getApiURL()}/usuarios/${id}`, this.httpOptions);
  }

  registrarUsuarioOperativo(dto: UsuarioOperativoDTO): Observable<any> {
    return this.http.post<any>(`${this.restConstants.getApiURL()}/usuarios-operativos`, dto, this.httpOptions);
  }

  actualizarUsuario(id: number, usuario: UsuarioOperativo): Observable<any> {
    return this.http.put<any>(`${this.restConstants.getApiURL()}/usuarios/${id}`, usuario, this.httpOptions);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put<any>(`${this.restConstants.getApiURL()}/usuarios/${id}/estado`, { estado }, this.httpOptions);
  }
}