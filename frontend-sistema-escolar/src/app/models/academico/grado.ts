export interface Grado {
    id: number;
    nombre: string;
    nivel: 'PRE_PRIMARIA' | 'PRIMARIA' | 'BASICO' | 'DIVERDIFICADO';
    estado: boolean;
}
