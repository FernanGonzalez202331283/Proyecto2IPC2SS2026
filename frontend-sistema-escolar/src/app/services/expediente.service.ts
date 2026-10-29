import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants'; 

export interface EncargadoPersona {
  idPersona?: number;
  nombres: string;
  apellidos: string;
  dpi?: string;
  telefono?: string;
  correo?: string;
  direccion?: string;
}

export interface EstudianteEncargado {
  idEncargado?: number;
  idEstudiante?: number;
  parentesco: string;
  responsablePrincipal: boolean;
  encargado: EncargadoPersona;
}

export interface Inscripcion {
  idInscripcion: number;
  idEstudiante: number;
  idSeccion: number;
  fechaInscripcion: string;
  estado: string;
}

export interface Expediente {
  idEstudiante: number;
  codigoEstudiante: string;
  nombres: string;
  apellidos: string;
  dpi: string;
  telefono: string;
  correo: string;
  direccion: string;
  informacionMedica: string;
  estado: string;
  encargados: EstudianteEncargado[];
  historialAcademico: Inscripcion[];
}

@Injectable({
  providedIn: 'root'
})
export class ExpedienteService {
  private restConstants = new RestConstants();
  private httpOptions = { withCredentials: true };

  constructor(private http: HttpClient) {}

  consultarExpediente(idEstudiante: number): Observable<Expediente> {
    return this.http.get<Expediente>(
      `${this.restConstants.getApiURL()}/expedientes/${idEstudiante}`,
      this.httpOptions
    );
  }
}