import { Injectable, inject } from '@angular/core';
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
  usuario?: any;
}

export interface UsuarioOperativoDTO {
  persona: PersonaOperativo;
  empleado: EmpleadoOperativo;
  usuario?: UsuarioOperativo;
  crearCuentaUsuario: boolean;
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
export class UsuarioOperativoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = this.restConstants.getApiURL();

  obtenerUsuarios(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<UsuarioOperativo>> {
    return this.http.get<RespuestaPaginada<UsuarioOperativo>>(
      `${this.baseUrl}/usuarios-operativos?pagina=${pagina}&limite=${limite}`
    );
  }

  buscarPorId(id: number): Observable<UsuarioOperativo> {
    return this.http.get<UsuarioOperativo>(`${this.baseUrl}/usuarios/${id}`);
  }

  registrarUsuarioOperativo(dto: UsuarioOperativoDTO): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/usuarios-operativos`, dto);
  }

  actualizarUsuario(id: number, usuario: UsuarioOperativo): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/usuarios/${id}`, usuario);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/usuarios/${id}/estado`, { estado });
  }
}