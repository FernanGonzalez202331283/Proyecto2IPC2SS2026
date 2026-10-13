/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author fernan
 */
public class AsignacionDocenteService {
    
    private AsignacionDocenteDAO asignacionDAO;
    private EmpleadoDAO empleadoDAO;
    private CursoDAO cursoDAO;
    private SeccionDAO seccionDAO;

    // Constructor
    public AsignacionDocenteService() {

        asignacionDAO = new AsignacionDocenteDAO();
        empleadoDAO = new EmpleadoDAO();
        cursoDAO = new CursoDAO();
        seccionDAO = new SeccionDAO();
    }

    // Registrar una asignación docente
    public String registrar(AsignacionDocente asignacion) {

        // Validar que la asignación no sea nula
        if (asignacion == null) {
            return "Los datos de la asignación son obligatorios.";
        }

        // Validar que se hayan seleccionado las entidades
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

        // Buscar empleado
        Empleado empleado = empleadoDAO.buscarPorId(idEmpleado);

        if (empleado == null) {
            return "El empleado que seleccionó no existe.";
        }

        // Validar que sea maestro
        if (empleado.getPuesto() == null
                || !empleado.getPuesto().equalsIgnoreCase("MAESTRO")) {

            return "El empleado seleccionado no tiene el puesto de MAESTRO.";
        }

        // Validar estado del empleado
        if (!empleado.isEstado()) {
            return "No se puede asignar un maestro inactivo.";
        }

        // Buscar curso
        Curso curso = cursoDAO.buscarPorId(idCurso);

        if (curso == null) {
            return "El curso seleccionado no existe.";
        }

        if (!curso.isEstado()) {
            return "No se puede asignar un curso inactivo.";
        }

        // Buscar sección
        Seccion seccion = seccionDAO.buscarPorId(idSeccion);

        if (seccion == null) {
            return "La sección seleccionada no existe.";
        }

        if (!seccion.isEstado()) {
            return "No se puede asignar una sección inactiva.";
        }

        // Comprobar que no exista una asignación duplicada
        ArrayList<AsignacionDocente> asignaciones =
                asignacionDAO.obtenerTodos();

        for (AsignacionDocente actual : asignaciones) {

            if (actual.getEmpleado().getIdEmpleado() == idEmpleado
                    && actual.getCurso().getIdCurso() == idCurso
                    && actual.getSeccion().getIdSeccion() == idSeccion) {

                return "Esta asignación ya existe. "
                        + "No se puede registrar nuevamente.";
            }
        }

        // Preparar asignación
        asignacion.setEmpleado(empleado);
        asignacion.setCurso(curso);
        asignacion.setSeccion(seccion);
        asignacion.setEstado(true);

        // Registrar
        if (asignacionDAO.registrar(asignacion)) {
            return "Asignación docente registrada correctamente.";
        }

        return "No fue posible registrar la asignación docente.";
    }

    // Buscar una asignación por ID
    public AsignacionDocente buscarPorId(int idAsignacion) {

        if (idAsignacion <= 0) {
            return null;
        }

        return asignacionDAO.buscarPorId(idAsignacion);
    }

    // Obtener todas las asignaciones
    public ArrayList<AsignacionDocente> obtenerTodos() {

        return asignacionDAO.obtenerTodos();
    }

    // Actualizar una asignación
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

        // Comprobar que la asignación exista
        AsignacionDocente existente =
                asignacionDAO.buscarPorId(asignacion.getIdAsignacion());

        if (existente == null) {
            return "La asignación docente que desea actualizar no existe.";
        }

        // Buscar empleado
        Empleado empleado = empleadoDAO.buscarPorId(idEmpleado);

        if (empleado == null) {
            return "El empleado seleccionado no existe.";
        }

        if (empleado.getPuesto() == null
                || !empleado.getPuesto().equalsIgnoreCase("MAESTRO")) {

            return "El empleado seleccionado no tiene el puesto de MAESTRO.";
        }

        if (!empleado.isEstado()) {
            return "No se puede asignar un maestro inactivo.";
        }

        // Buscar curso
        Curso curso = cursoDAO.buscarPorId(idCurso);

        if (curso == null) {
            return "El curso seleccionado no existe.";
        }

        if (!curso.isEstado()) {
            return "No se puede asignar un curso inactivo.";
        }

        // Buscar sección
        Seccion seccion = seccionDAO.buscarPorId(idSeccion);

        if (seccion == null) {
            return "La sección seleccionada no existe.";
        }

        if (!seccion.isEstado()) {
            return "No se puede asignar una sección inactiva.";
        }

        // Comprobar duplicados, excluyendo la asignación actual
        ArrayList<AsignacionDocente> asignaciones =
                asignacionDAO.obtenerTodos();

        for (AsignacionDocente actual : asignaciones) {

            if (actual.getIdAsignacion() != asignacion.getIdAsignacion()
                    && actual.getEmpleado().getIdEmpleado() == idEmpleado
                    && actual.getCurso().getIdCurso() == idCurso
                    && actual.getSeccion().getIdSeccion() == idSeccion) {

                return "Ya existe otra asignación con esos datos.";
            }
        }

        // Asignar objetos encontrados
        asignacion.setEmpleado(empleado);
        asignacion.setCurso(curso);
        asignacion.setSeccion(seccion);

        if (asignacionDAO.actualizar(asignacion)) {
            return "Asignación docente actualizada correctamente.";
        }

        return "No fue posible actualizar la asignación docente.";
    }

    // Cambiar estado de una asignación
    public String cambiarEstado(int idAsignacion, boolean estado) {

        if (idAsignacion <= 0) {
            return "El identificador de la asignación no es válido.";
        }

        // Comprobar que exista
        AsignacionDocente asignacion =
                asignacionDAO.buscarPorId(idAsignacion);

        if (asignacion == null) {
            return "La asignación docente no existe.";
        }

        // Comprobar si ya tiene el estado solicitado
        if (asignacion.isEstado() == estado) {

            if (estado) {
                return "La asignación docente ya se encuentra activa.";
            } else {
                return "La asignación docente ya se encuentra inactiva.";
            }
        }

        if (asignacionDAO.cambiarEstado(idAsignacion, estado)) {

            if (estado) {
                return "Asignación docente activada correctamente.";
            } else {
                return "Asignación docente desactivada correctamente.";
            }
        }

        return "No fue posible cambiar el estado de la asignación docente.";
    }
}
