import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface Persona {
  idPersona?: number;
  nombres: string;
  apellidos: string;
  dpi: string;
  telefono?: string;
  correo?: string;
  direccion?: string;
  estado?: boolean;
}

export interface Empleado {
  idEmpleado?: number;
  persona: Persona;
  puesto: string;
  fechaContratacion: string;
  salario: number;
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
export class EmpleadoService {
  private http = inject(HttpClient);
  private restConstants = new RestConstants();
  private readonly baseUrl = `${this.restConstants.getApiURL()}/empleados`;

  obtenerEmpleados(pagina: number = 1, limite: number = 10): Observable<RespuestaPaginada<Empleado>> {
    const params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());

    return this.http.get<RespuestaPaginada<Empleado>>(this.baseUrl, { params });
  }

  buscarPorId(id: number): Observable<Empleado> {
    return this.http.get<Empleado>(`${this.baseUrl}/${id}`);
  }

  registrarEmpleado(empleado: Empleado): Observable<any> {
    return this.http.post(this.baseUrl, empleado);
  }

  actualizarEmpleado(id: number, empleado: Empleado): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, empleado);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/estado`, { estado });
  }
}