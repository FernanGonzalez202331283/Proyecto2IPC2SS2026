export type UserRole = 
'SUPER_ADMIN' 
| 'ADMIN' 
| 'SECRETARIA' 
| 'MAESTRO' 
| 'BIBLIOTECARIO' 
| 'ESTUDIANTE'
| 'PERSONAL';


export interface User {
  id?: number;
  username: string;
  role: UserRole;
  activo: boolean;
}