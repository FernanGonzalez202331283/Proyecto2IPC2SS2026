import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../shared/rest-api/rest-constants';
import { Encargado } from './estudiante.service';

@Injectable({
  providedIn: 'root'
})
export class EncargadoService {
  private restConstants = new RestConstants();
  private httpOptions = { withCredentials: true };

  constructor(private http: HttpClient) {}

  obtenerEncargados(): Observable<Encargado[]> {
    return this.http.get<Encargado[]>(`${this.restConstants.getApiURL()}/encargados`, this.httpOptions);
  }
}