package seguridad.iteracion2;

import Dao.CursoDAO;
import Dao.SeccionDAO;
import Dao.iteracion2.AsignacionDocenteDAO;
import Dao.iteracion2.EmpleadoDAO;
import java.util.ArrayList;
import modelo.Curso;
import modelo.Seccion;
import modelo.iteracion2.AsignacionDocente;
import modelo.iteracion2.Empleado;

public class AsignacionDocenteService {
    
    private AsignacionDocenteDAO asignacionDAO;
    private EmpleadoDAO empleadoDAO;
    private CursoDAO cursoDAO;
    private SeccionDAO seccionDAO;

    public AsignacionDocenteService() {
        asignacionDAO = new AsignacionDocenteDAO();
        empleadoDAO = new EmpleadoDAO();
        cursoDAO = new CursoDAO();
        seccionDAO = new SeccionDAO();
    }

    public String registrar(AsignacionDocente asignacion) {

        if (asignacion == null) {
            return "Los datos de la asignación son obligatorios.";
        }

        if (asignacion.getEmpleado() == null
                || asignacion.getCurso() == null
                || asignacion.getSeccion() == null) {

            return "Debe seleccionar un maestro, un curso y una sección.";
        }

        int idEmpleado = asignacion.getEmpleado().getIdEmpleado();
        int idCurso = asignacion.getCurso().getIdCurso();
        int idSeccion = asignacion.getSeccion().getIdSeccion();

        if (idEmpleado <= 0 || idCurso <= 0 || idSeccion <= 0) {
            return "Los identificadores deben ser válidos.";
        }

        Empleado empleado = empleadoDAO.buscarPorId(idEmpleado);

        if (empleado == null) {
            return "El empleado que seleccionó no existe.";
        }

        if (!empleado.isEstado()) {
            return "No se puede asignar un maestro inactivo.";
        }

        Curso curso = cursoDAO.buscarPorId(idCurso);

        if (curso == null) {
            return "El curso seleccionado no existe.";
        }

        if (!curso.isEstado()) {
            return "No se puede asignar un curso inactivo.";
        }

        Seccion seccion = seccionDAO.buscarPorId(idSeccion);

        if (seccion == null) {
            return "La sección seleccionada no existe.";
        }

        if (!seccion.isEstado()) {
            return "No se puede asignar una sección inactiva.";
        }

        // VALIDACIÓN: Verificar que el curso no esté asignado previamente a esa sección
        ArrayList<AsignacionDocente> asignaciones = asignacionDAO.obtenerTodos();

        for (AsignacionDocente actual : asignaciones) {
            if (actual.isEstado()
                    && actual.getCurso().getIdCurso() == idCurso
                    && actual.getSeccion().getIdSeccion() == idSeccion) {

                return "El curso seleccionado ya se encuentra asignado a esta sección.";
            }
        }

        asignacion.setEmpleado(empleado);
        asignacion.setCurso(curso);
        asignacion.setSeccion(seccion);
        asignacion.setEstado(true);

        if (asignacionDAO.registrar(asignacion)) {
            return "Asignación docente registrada correctamente.";
        }

        return "No fue posible registrar la asignación docente.";
    }

    public AsignacionDocente buscarPorId(int idAsignacion) {

        if (idAsignacion <= 0) {
            return null;
        }

        return asignacionDAO.buscarPorId(idAsignacion);
    }

    public ArrayList<AsignacionDocente> obtenerTodos() {

        return asignacionDAO.obtenerTodos();
    }

    public String actualizar(AsignacionDocente asignacion) {

        if (asignacion == null) {
            return "Los datos de la asignación son obligatorios.";
        }

        if (asignacion.getIdAsignacion() <= 0) {
            return "El identificador de la asignación no es válido.";
        }

        if (asignacion.getEmpleado() == null
                || asignacion.getCurso() == null
                || asignacion.getSeccion() == null) {

            return "Debe seleccionar un maestro, un curso y una sección.";
        }

        int idEmpleado = asignacion.getEmpleado().getIdEmpleado();
        int idCurso = asignacion.getCurso().getIdCurso();
        int idSeccion = asignacion.getSeccion().getIdSeccion();

        if (idEmpleado <= 0 || idCurso <= 0 || idSeccion <= 0) {
            return "Los identificadores deben ser válidos.";
        }

        AsignacionDocente existente = asignacionDAO.buscarPorId(asignacion.getIdAsignacion());

        if (existente == null) {
            return "La asignación docente que desea actualizar no existe.";
        }

        Empleado empleado = empleadoDAO.buscarPorId(idEmpleado);

        if (empleado == null) {
            return "El empleado seleccionado no existe.";
        }

        if (!empleado.isEstado()) {
            return "No se puede asignar un maestro inactivo.";
        }

        Curso curso = cursoDAO.buscarPorId(idCurso);

        if (curso == null) {
            return "El curso seleccionado no existe.";
        }

        if (!curso.isEstado()) {
            return "No se puede asignar un curso inactivo.";
        }

        Seccion seccion = seccionDAO.buscarPorId(idSeccion);

        if (seccion == null) {
            return "La sección seleccionada no existe.";
        }

        if (!seccion.isEstado()) {
            return "No se puede asignar una sección inactiva.";
        }

        // VALIDACIÓN: Verificar duplicados en la misma sección excluyendo el registro en edición
        ArrayList<AsignacionDocente> asignaciones = asignacionDAO.obtenerTodos();

        for (AsignacionDocente actual : asignaciones) {
            if (actual.getIdAsignacion() != asignacion.getIdAsignacion()
                    && actual.isEstado()
                    && actual.getCurso().getIdCurso() == idCurso
                    && actual.getSeccion().getIdSeccion() == idSeccion) {

                return "El curso seleccionado ya se encuentra asignado a esta sección.";
            }
        }

        asignacion.setEmpleado(empleado);
        asignacion.setCurso(curso);
        asignacion.setSeccion(seccion);

        if (asignacionDAO.actualizar(asignacion)) {
            return "Asignación docente actualizada correctamente.";
        }

        return "No fue posible actualizar la asignación docente.";
    }

    public String cambiarEstado(int idAsignacion, boolean estado) {

        if (idAsignacion <= 0) {
            return "El identificador de la asignación no es válido.";
        }

        AsignacionDocente asignacion = asignacionDAO.buscarPorId(idAsignacion);

        if (asignacion == null) {
            return "La asignación docente no existe.";
        }

        if (asignacion.isEstado() == estado) {
            return estado ? "La asignación docente ya se encuentra activa." : "La asignación docente ya se encuentra inactiva.";
        }

        if (asignacionDAO.cambiarEstado(idAsignacion, estado)) {
            return estado ? "Asignación docente activada correctamente." : "Asignación docente desactivada correctamente.";
        }

        return "No fue posible cambiar el estado de la asignación docente.";
    }
}