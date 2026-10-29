import {UserRole} from './user';

export interface LoginRequest {
    username: string;
    contraseña: string;   
}

export interface LoginResponse {
    idUsuario: number;
    username: string;
    idPersona: number;
    rol: UserRole; // Cambiado de 'role' a 'rol' para coincidir con Java
    estado: boolean;
    token: string;
}
