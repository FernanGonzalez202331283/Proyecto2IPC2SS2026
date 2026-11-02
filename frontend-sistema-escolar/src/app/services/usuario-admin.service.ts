import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';
import { UserRole } from '../models/auth/user';

export interface PersonaAdmin {
  idPersona: number;
  nombres: string;
  apellidos: string;
  dpi: string;
  telefono?: string;
  correo?: string;
  direccion?: string;
  estado?: boolean;
}

export interface UsuarioAdmin {
  idUsuario?: number;
  username: string;
  contraseña?: string;
  idPersona: number;
  rol: UserRole;
  estado: boolean;
  fechaCreacion?: string;
  persona?: PersonaAdmin;
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
export class UsuarioAdminService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  obtenerUsuarios(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<UsuarioAdmin>> {
    return this.http.get<RespuestaPaginada<UsuarioAdmin>>(
      `${this.restConstants.getApiURL()}/usuarios?pagina=${pagina}&limite=${limite}`
    );
  }

  buscarPorId(id: number): Observable<UsuarioAdmin> {
    return this.http.get<UsuarioAdmin>(`${this.restConstants.getApiURL()}/usuarios/${id}`);
  }

  registrarUsuario(usuario: UsuarioAdmin): Observable<UsuarioAdmin> {
    return this.http.post<UsuarioAdmin>(`${this.restConstants.getApiURL()}/usuarios`, usuario);
  }

  actualizarUsuario(id: number, usuario: UsuarioAdmin): Observable<UsuarioAdmin> {
    return this.http.put<UsuarioAdmin>(`${this.restConstants.getApiURL()}/usuarios/${id}`, usuario);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put<any>(`${this.restConstants.getApiURL()}/usuarios/${id}/estado`, { estado });
  }
}