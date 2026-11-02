import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { UserRole } from '../../models/auth/user';
import { LoginRequest, LoginResponse } from '../../models/auth/auth';
import { RestConstants } from '../../shared/rest-api/rest-constants';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private restConstants = new RestConstants();
  currentUserRole = signal<UserRole | null>(this.getStoredRole());

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.restConstants.getApiURL()}/login`, credentials).pipe(
      tap((response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.rol);

        const payload = this.decodeToken(response.token);

        const idUser = response.idUsuario || (payload ? payload.idUsuario : null);
        if (idUser) {
          localStorage.setItem('idUsuario', idUser.toString());
        }

        const idPers = response.idPersona || (payload ? payload.idPersona : null);
        if (idPers) {
          localStorage.setItem('idPersona', idPers.toString());
        }

        this.currentUserRole.set(response.rol);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('idUsuario');
    localStorage.removeItem('idPersona');
    this.currentUserRole.set(null);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  getUserRole(): UserRole | null {
    return this.currentUserRole();
  }

  getIdUsuario(): number | null {
    const id = localStorage.getItem('idUsuario');
    if (id) {
      return Number(id);
    }

    const token = localStorage.getItem('token');
    if (token) {
      const payload = this.decodeToken(token);
      if (payload && payload.idUsuario) {
        localStorage.setItem('idUsuario', payload.idUsuario.toString());
        return Number(payload.idUsuario);
      }
    }

    return null;
  }

  getIdPersona(): number | null {
    const id = localStorage.getItem('idPersona');
    if (id) {
      return Number(id);
    }

    const token = localStorage.getItem('token');
    if (token) {
      const payload = this.decodeToken(token);
      if (payload && payload.idPersona) {
        localStorage.setItem('idPersona', payload.idPersona.toString());
        return Number(payload.idPersona);
      }
    }

    return null;
  }

  private getStoredRole(): UserRole | null {
    return (localStorage.getItem('role') as UserRole) || null;
  }

  private decodeToken(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (e) {
      console.error('Error al decodificar el token JWT', e);
      return null;
    }
  }
}