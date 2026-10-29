import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';

export interface RecuperarContrasenaRequest {
  username: string;
  passwordAnterior: string; 
  nuevoPassword: string;   
}

@Injectable({
  providedIn: 'root'
})
export class RecuperarContrasenaService {
  private restConstants = new RestConstants();

  constructor(private http: HttpClient) {}

  recuperarContraseña(datos: RecuperarContrasenaRequest): Observable<any> {
    return this.http.post<any>(`${this.restConstants.getApiURL()}/recuperar-password`, datos);
  }
}