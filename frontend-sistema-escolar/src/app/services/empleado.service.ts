import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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

@Injectable({
  providedIn: 'root'
})
export class EmpleadoService {
  private restConstants = new RestConstants();
  private httpOptions = { withCredentials: true };

  constructor(private http: HttpClient) {}

  obtenerEmpleados(): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(`${this.restConstants.getApiURL()}/empleados`, this.httpOptions);
  }

  buscarPorId(id: number): Observable<Empleado> {
    return this.http.get<Empleado>(`${this.restConstants.getApiURL()}/empleados/${id}`, this.httpOptions);
  }

  registrarEmpleado(empleado: Empleado): Observable<any> {
    return this.http.post(`${this.restConstants.getApiURL()}/empleados`, empleado, this.httpOptions);
  }

  actualizarEmpleado(id: number, empleado: Empleado): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/empleados/${id}`, empleado, this.httpOptions);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.restConstants.getApiURL()}/empleados/${id}/estado`, { estado }, this.httpOptions);
  }
}