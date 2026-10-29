import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth/auth-guard';

export const routes: Routes = [
  // Rutas públicas
  {
    path: 'login',
    loadComponent: () => import('./pages/auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'recuperar-password',
    loadComponent: () => import('./pages/auth/recuperar-password/recuperar-password.component').then(m => m.RecuperarPasswordComponent)
  },

  // ==========================================
  // MÓDULO DE BIBLIOTECA
  // ==========================================
  {
    path: 'biblioteca/dashboard',
    loadComponent: () => import('./pages/biblioteca/biblioteca-dashboard/biblioteca-dashboard.component').then(m => m.BibliotecaDashboardComponent),
    canActivate: [authGuard(['BIBLIOTECARIO', 'SUPER_ADMIN'])]
  },
  {
    path: 'biblioteca/libros',
    loadComponent: () => import('./pages/biblioteca/libros/libros.component').then(m => m.LibrosComponent),
    canActivate: [authGuard(['BIBLIOTECARIO', 'SUPER_ADMIN'])]
  },
  {
    path: 'biblioteca/catalogo',
    loadComponent: () => import('./pages/biblioteca/catalogo/catalogo.component').then(m => m.CatalogoComponent),
    canActivate: [authGuard(['BIBLIOTECARIO', 'SUPER_ADMIN', 'ESTUDIANTE'])]
  },
  {
    path: 'biblioteca/prestamos',
    loadComponent: () => import('./pages/biblioteca/prestamo/prestamo.component').then(m => m.PrestamoComponent),
    canActivate: [authGuard(['BIBLIOTECARIO', 'SUPER_ADMIN'])]
  },
  {
    path: 'estudiante/consultar-estado',
    loadComponent: () => import('./pages/estudiante/consultar-estado-cuenta/consultar-estado-cuenta.component').then(m => m.ConsultarEstadoCuentaComponent),
    canActivate: [authGuard(['BIBLIOTECARIO','ESTUDIANTE'])]
  },
  {
    path: 'reporte/solvencia',
    loadComponent: () => import('./pages/secretaria/reporte-moroso/reporte-solvencia.component').then(m => m.ReporteSolvenciaComponent),
    canActivate: [authGuard(['SECRETARIA'])]
  },
  {
    path: 'reporte/rendimiento',
    loadComponent: () => import('./pages/admin/reporte-rendimiento/reporte-rendimiento.component').then(m => m.ReporteRendimientoComponent),
    canActivate: [authGuard(['ADMIN'])]
  },
  {
    path: 'reporte/historial',
    loadComponent: () => import('./pages/secretaria/reporte-historial-pago/historial-pago.component').then(m => m.HistorialPagoComponent),
    canActivate: [authGuard(['SECRETARIA'])]
  },
  {
    path: 'reporte/ingresos',
    loadComponent: () => import('./pages/admin/reporte-ingresos/reporte-ingresos.component').then(m => m.ReporteIngresosComponent),
    canActivate: [authGuard(['ADMIN', 'SUPER_ADMIN'])]
  },
  {
    path: 'reporte/prestamo-mora',
    loadComponent: () => import('./pages/biblioteca/reporte-mora/reporte-prestamo-mora.component').then(m => m.ReportePrestamoMoraComponent),
    canActivate: [authGuard(['BIBLIOTECARIO'])]
  },
  {
    path: 'biblioteca/devoluciones',
    loadComponent: () => import('./pages/biblioteca/devolucion/devolucion.component').then(m => m.DevolucionComponent),
    canActivate: [authGuard(['BIBLIOTECARIO', 'SUPER_ADMIN'])]
  },
  {
    path: 'biblioteca/multas',
    loadComponent: () => import('./pages/biblioteca/multa/multa.component').then(m => m.MultaComponent),
    canActivate: [authGuard(['BIBLIOTECARIO', 'SUPER_ADMIN'])]
  },

  {
    path: 'biblioteca/mis-prestamos',
    loadComponent: () => import('./pages/estudiante/mi-prestamo/mis-prestamos.component').then(m => m.MisPrestamosComponent),
    canActivate: [authGuard(['ESTUDIANTE', 'MAESTRO'])]
  },
  {
  path: 'secretaria/registrar-pago',
  loadComponent: () => import('./pages/secretaria/pagos/registrar-pago.component').then(m => m.RegistrarPagoComponent),
  canActivate: [authGuard(['SECRETARIA'])]
  },
  
  {
    path: 'admin/dashboard',
    loadComponent: () => import('./pages/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
    canActivate: [authGuard(['SUPER_ADMIN', 'ADMIN'])]
  },
  {
    path: 'admin/carreras',
    loadComponent: () => import('./pages/admin/carreras/carrera.component').then(m => m.CarrerasComponent),
    canActivate: [authGuard(['ADMIN'])]
  },
  {
    path: 'admin/curso',
    loadComponent: () => import('./pages/admin/curso/curso.component').then(m => m.CursosComponent),
    canActivate: [authGuard(['ADMIN'])]
  },
  {
    path: 'admin/curriculo',
    loadComponent: () => import('./pages/admin/curriculo/curriculo.component').then(m => m.CurriculoComponent),
    canActivate: [authGuard(['ADMIN', 'SUPER_ADMIN'])] 
  },
  {
    path: 'admin/usuarios-operativos',
    loadComponent: () => import('./pages/admin/usuarios-operativos/usuarios-operativos.component').then(m => m.UsuariosOperativosComponent),
    canActivate: [authGuard(['ADMIN'])]
  },
  {
    path: 'admin/grados',
    loadComponent: () => import('./pages/admin/grados/grados.component').then(m => m.GradosComponent),
    canActivate: [authGuard(['ADMIN'])]
  },
  {
    path: 'admin/secciones',
    loadComponent: () => import('./pages/admin/secciones/secciones.component').then(m => m.SeccionesComponent),
    canActivate: [authGuard(['ADMIN'])]
  },
  {
    path: 'admin/asignacion-docente',
    loadComponent: () => import('./pages/admin/asignacion-docente/asignacion-docente.component').then(m => m.AsignacionDocenteComponent),
    canActivate: [authGuard(['ADMIN'])]
  },
  {
    path: 'admin/empleados',
    loadComponent: () => import('./pages/admin/empleados/empleado.component').then(m => m.EmpleadosComponent),
    canActivate: [authGuard(['ADMIN'])]
  },
  {
    path: 'admin/usuarios-admin',
    loadComponent: () => import('./pages/admin/usuarioos-admin/usuarios-admin.component').then(m => m.UsuariosAdminComponent),
    canActivate: [authGuard(['SUPER_ADMIN', 'ADMIN'])]
  },
  {
    path: 'superadmin/dashboard',
    loadComponent: () => import('./pages/superadmin/superadmin-dashboard/superadmin-dashboard.component').then(m => m.SuperAdminDashboardComponent),
    canActivate: [authGuard(['SUPER_ADMIN'])]
  },
  {
    path: 'superadmin/configuracion-grados',
    loadComponent: () => import('./pages/superadmin/configuracion-grados/configuracion-grados.component').then(m => m.ConfiguracionGradosComponent),
    canActivate: [authGuard(['SUPER_ADMIN'])]
  },
  {
    path: 'super-admin/anos-lectivos',
    loadComponent: () => import('./pages/superadmin/ano-lectivo/anos-lectivos.component').then(m => m.AnosLectivosComponent),
    canActivate: [authGuard(['SUPER_ADMIN'])]
  },
  {
    path: 'secretaria/dashboard',
    loadComponent: () => import('./pages/secretaria/secretaria-dashboard/secretaria-dashboard.component').then(m => m.SecretariaDashboardComponent),
    canActivate: [authGuard(['SECRETARIA', 'SUPER_ADMIN'])]
  },
  {
    path: 'secretaria/inscripcion',
    loadComponent: () => import('./pages/secretaria/inscripcion/inscripcion.component').then(m => m.InscripcionesComponent),
    canActivate: [authGuard(['SECRETARIA', 'SUPER_ADMIN'])]
  },
  {
    path: 'secretaria/estudiantes',
    loadComponent: () => import('./pages/secretaria/estudiantes/estudiantes.component').then(m => m.EstudiantesComponent),
    canActivate: [authGuard(['SECRETARIA', 'SUPER_ADMIN'])]
  },
  {
    path: 'secretaria/expedientes',
    loadComponent: () => import('./pages/secretaria/expedientes/expediente.component').then(m => m.ExpedienteComponent),
    canActivate: [authGuard(['SECRETARIA'])]
  },
  { 
    path: 'secretaria/estudiantes',
    loadComponent: ()=> import('./pages/secretaria/estudiantes/estudiantes.component').then(m =>m.EstudiantesComponent),
    canActivate: [authGuard(['SECRETARIA'])]

  },
  {
    path:'secretaria/solvencia',
    loadComponent: () => import('./pages/secretaria/solvencia/generar-solvencia.component').then(m=>m.GenerarSolvenciaComponent),
    canActivate: [authGuard(['SECRETARIA'])]
  },
  {
    path:'secretaria/estado-cuenta',
    loadComponent: () => import('./pages/estudiante/estado-cuenta/consultar-estado-cuenta.component').then(m=>m.ConsultarEstadoCuentaComponent),
    canActivate: [authGuard(['SECRETARIA', 'ESTUDIANTE'])]
  },
  {
    path:'usuarios/consultar-perfil',
    loadComponent: () => import('./pages/consultar-perfil/consultar-perfil.component').then(m=>m.ConsultarPerfilComponent),
    canActivate: [authGuard(['SECRETARIA', 'ESTUDIANTE', 'ADMIN', 'BIBLIOTECARIO', 'MAESTRO', 'PERSONAL','SUPER_ADMIN'])]
  },
  {
    path:'estudiante/historial-academico',
    loadComponent: () => import('./pages/estudiante/historial-academico/historial-academico.component').then(m=>m.HistorialAcademicoComponent),
    canActivate: [authGuard(['ESTUDIANTE'])]
  },
  {
    path: 'maestro/dashboard',
    loadComponent: () => import('./pages/maestro/maestro-dashboard/maestro-dashboard.component').then(m => m.MaestroDashboardComponent),
    canActivate: [authGuard(['MAESTRO', 'SUPER_ADMIN'])]
  },
  {
    path: 'maestro/nomina-alumnos',
    loadComponent: () => import('./pages/maestro/nomna/nomina.component').then(m => m.NominaComponent),
    canActivate: [authGuard(["MAESTRO"])]
  },
  {
    path: 'maestro/actividades',
    loadComponent: () => import('./pages/maestro/actividad/actividad.component').then(m => m.ActicidadComponent),
    canActivate: [authGuard(['MAESTRO'])]
  },
  {
    path: 'maestro/registrar-notas',
    loadComponent: () => import('./pages/maestro/notas/nominas/nomina-alumnos.component').then(m => m.NominaAlumnosComponent),
    canActivate: [authGuard(['MAESTRO'])]
  },
  {
    path: 'maestro/boleta-calificaciones/:idInscripcion',
    loadComponent: () => import('./pages/maestro/notas/boleta/boleta-calificaciones.component').then(m => m.BoletaCalificacionesComponent),
    canActivate: [authGuard(['MAESTRO'])]
  },
  {
    path: 'maestro/asistencia',
    loadComponent: () => import('./pages/maestro/asistencia/asistencia.component').then(m => m.AsistenciaComponent),
    canActivate: [authGuard(['MAESTRO'])]
  },
  {
    path: 'maestro/cursos-asignados',
    loadComponent: () => import('./pages/maestro/cursos-asignados/curso-asignado.component').then(m => m.CursosAsignadosComponent),
    canActivate: [authGuard(['MAESTRO'])]
  },
  {
  path: 'estudiante/dashboard',
  loadComponent: () => import('./pages/estudiante/estudiante-dashboard/estudiante-dashboard.components').then(m => m.EstudianteDashboardComponent),
  canActivate: [authGuard(['ESTUDIANTE'])]
},
  
  {
    path: 'academico/carreras',
    loadComponent: () => import('./pages/academico/carreras/carreras').then(m => m.Carreras),
    canActivate: [authGuard(['SUPER_ADMIN', 'ADMIN'])]
  },
  {
    path: 'usuarios',
    loadComponent: () => import('./pages/usuarios/usuarios-list/usuarios-list').then(m => m.UsuariosList),
    canActivate: [authGuard(['SUPER_ADMIN', 'ADMIN'])]
  },

  // Redirecciones por defecto
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];