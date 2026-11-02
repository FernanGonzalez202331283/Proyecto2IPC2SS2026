import {UserRole} from './user';

export interface LoginRequest {
    username: string;
    contraseña: string;   
}

export interface LoginResponse {
    idUsuario: number;
    username: string;
    idPersona: number;
    rol: UserRole;
    estado: boolean;
    token: string;
}
