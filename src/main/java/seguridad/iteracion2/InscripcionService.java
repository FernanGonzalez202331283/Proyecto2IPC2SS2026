/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad.iteracion2;

import Conexion.Conexion;
import Dao.iteracion2.EncargadoDAO;
import Dao.iteracion2.EstudianteEncargadoDAO;
import Dao.iteracion2.InscripcionDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion2.Encargado;
import modelo.iteracion2.EstudianteEncargado;
import modelo.iteracion2.Inscripcion;

/**
 *
 * @author fernan
 */
public class InscripcionService {
    private final InscripcionDAO inscripcionDAO;
    private final EncargadoDAO encargadoDAO;
    private final EstudianteEncargadoDAO estudianteEncargadoDAO;

    public InscripcionService() {
        this.inscripcionDAO = new InscripcionDAO();
        this.encargadoDAO = new EncargadoDAO();
        this.estudianteEncargadoDAO = new EstudianteEncargadoDAO();
    }

    public String registrarInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null) {
            return "Debe proporcionar la información de la inscripción.";
        }
        if (inscripcion.getIdEstudiante() <= 0) {
            return "Debe proporcionar un estudiante válido.";
        }
        if (inscripcion.getIdSeccion() <= 0) {
            return "Debe proporcionar una sección válida.";
        }
        if (inscripcion.getFechaInscripcion() == null || inscripcion.getFechaInscripcion().trim().isEmpty()) {
            return "La fecha de inscripción es obligatoria.";
        }
        try {java.sql.Date.valueOf(inscripcion.getFechaInscripcion());} catch (IllegalArgumentException e) {
            return "La fecha de inscripción no tiene un formato válido. Utilice yyyy-MM-dd.";
        }
        
        if (!inscripcionDAO.existeEstudiante(inscripcion.getIdEstudiante())) {
            return "El estudiante no existe.";
        }
        String estadoEstudiante = inscripcionDAO.obtenerEstadoEstudiante(
                inscripcion.getIdEstudiante()
        );
        if (!"ACTIVO".equals(estadoEstudiante)) {
            return "No se puede inscribir un estudiante que no esté activo.";
        }

        if (!inscripcionDAO.existeSeccion(inscripcion.getIdSeccion())) {
            return "La sección no existe.";
        }

        if (!inscripcionDAO.seccionActiva(inscripcion.getIdSeccion())) {
            return "La sección no está activa.";
        }

        int idAñoLectivo = inscripcionDAO.obtenerAñoLectivoSeccion(
                inscripcion.getIdSeccion()
        );

        if (idAñoLectivo <= 0) {
            return "No se pudo determinar el año lectivo de la sección.";
        }

        if (!inscripcionDAO.añoLectivoActivo(idAñoLectivo)) {
            return "El año lectivo de la sección no está activo.";
        }

        if (inscripcionDAO.existeInscripcionActivaEnAño(
                inscripcion.getIdEstudiante(),
                idAñoLectivo)) {

            return "El estudiante ya tiene una inscripción activa en este año lectivo.";
        }

        int capacidad = inscripcionDAO.obtenerCapacidadSeccion(
                inscripcion.getIdSeccion()
        );

        int inscritos = inscripcionDAO.obtenerCantidadInscritos(
                inscripcion.getIdSeccion()
        );

        if (inscritos >= capacidad) {
            return "La sección ya alcanzó su capacidad máxima.";
        }

        if (inscripcion.getEstado() == null|| inscripcion.getEstado().trim().isEmpty()) {
            inscripcion.setEstado("ACTIVA");
        }
        if (!estadoValido(inscripcion.getEstado())) {
            return "El estado de la inscripción no es válido.";
        }
        return registrarInscripcionConEncargados(inscripcion);
    }
    
    private String validarEncargados(ArrayList<EstudianteEncargado> encargados) {

        if (encargados == null || encargados.isEmpty()) {
            return "Debe proporcionar al menos un encargado.";
        }

        int responsablesPrincipales = 0;

        for (int i = 0; i < encargados.size(); i++) {

            EstudianteEncargado relacion = encargados.get(i);

            if (relacion == null) {
                return "La información de un encargado es inválida.";
            }

            if (relacion.getParentesco() == null
                    || relacion.getParentesco().trim().isEmpty()) {

                return "Debe indicar el parentesco de cada encargado.";
            }

            if (relacion.isResponsablePrincipal()) {
                responsablesPrincipales++;
            }

            if (relacion.getIdEncargado() < 0) {
                return "El identificador del encargado no es válido.";
            }

            // Encargado nuevo
            if (relacion.getIdEncargado() == 0) {

                Encargado encargado = relacion.getEncargado();

                if (encargado == null) {
                    return "Debe proporcionar los datos del nuevo encargado.";
                }

                if (encargado.getNombres() == null
                        || encargado.getNombres().trim().isEmpty()) {

                    return "Los nombres del encargado son obligatorios.";
                }

                if (encargado.getApellidos() == null
                        || encargado.getApellidos().trim().isEmpty()) {

                    return "Los apellidos del encargado son obligatorios.";
                }

            } else {

                // Verificar que no se repita un encargado existente
                for (int j = i + 1; j < encargados.size(); j++) {

                    if (relacion.getIdEncargado()
                            == encargados.get(j).getIdEncargado()) {

                        return "No puede registrar dos veces al mismo encargado.";
                    }
                }
            }
        }

        if (responsablesPrincipales == 0) {
            return "Debe seleccionar un responsable principal.";
        }

        if (responsablesPrincipales > 1) {
            return "Solamente puede existir un responsable principal.";
        }

        return null;
    }

    public Inscripcion buscarPorId(int idInscripcion) {
        if (idInscripcion <= 0) {
            return null;
        }
        return inscripcionDAO.buscarPorId(idInscripcion);
    }

    public ArrayList<Inscripcion> obtenerTodos() {
        return inscripcionDAO.obtenerTodos();
    }

    public String actualizar(Inscripcion inscripcion) {
        if (inscripcion == null) {
            return "Debe proporcionar la información de la inscripción.";
        }
        if (inscripcion.getIdInscripcion() <= 0) {
            return "El ID de la inscripción no es válido.";
        }
        Inscripcion inscripcionActual = inscripcionDAO.buscarPorId(inscripcion.getIdInscripcion() );
        if (inscripcionActual == null) {
            return "La inscripción no existe.";
        }
        if (inscripcion.getIdEstudiante() <= 0) {
            return "Debe proporcionar un estudiante válido.";
        }
        if (inscripcion.getIdSeccion() <= 0) {
            return "Debe proporcionar una sección válida.";
        }
        if (inscripcion.getFechaInscripcion() == null || inscripcion.getFechaInscripcion().trim().isEmpty()) {
            return "La fecha de inscripción es obligatoria.";
        }

        try {
            java.sql.Date.valueOf(inscripcion.getFechaInscripcion());
        } catch (IllegalArgumentException e) {
            return "La fecha de inscripción no tiene un formato válido. Utilice yyyy-MM-dd.";
        }

        if (!inscripcionDAO.existeEstudiante(inscripcion.getIdEstudiante())) {
            return "El estudiante no existe.";
        }

        String estadoEstudiante = inscripcionDAO.obtenerEstadoEstudiante(
                inscripcion.getIdEstudiante()
        );
        if (!"ACTIVO".equals(estadoEstudiante)) {
            return "No se puede inscribir un estudiante que no esté activo.";
        }

        if (!inscripcionDAO.existeSeccion(inscripcion.getIdSeccion())) {
            return "La sección no existe.";
        }

        if (!inscripcionDAO.seccionActiva(inscripcion.getIdSeccion())) {
            return "La sección no está activa.";
        }

        int idAñoLectivo = inscripcionDAO.obtenerAñoLectivoSeccion(
                inscripcion.getIdSeccion()
        );

        if (idAñoLectivo <= 0) {
            return "No se pudo determinar el año lectivo de la sección.";
        }

        if (!inscripcionDAO.añoLectivoActivo(idAñoLectivo)) {
            return "El año lectivo de la sección no está activo.";
        }

        if (inscripcionDAO.existeInscripcionActivaEnAño(
                inscripcion.getIdEstudiante(),
                idAñoLectivo,
                inscripcion.getIdInscripcion())) {

            return "El estudiante ya tiene otra inscripción activa en este año lectivo.";
        }

        if (inscripcionActual.getIdSeccion() != inscripcion.getIdSeccion()) {

            int capacidad = inscripcionDAO.obtenerCapacidadSeccion(
                    inscripcion.getIdSeccion()
            );

            int inscritos = inscripcionDAO.obtenerCantidadInscritos(
                    inscripcion.getIdSeccion()
            );

            if (inscritos >= capacidad) {
                return "La sección ya alcanzó su capacidad máxima.";
            }
        }

        if (inscripcionDAO.actualizar(inscripcion)) {
            return "Inscripción actualizada correctamente.";
        }

        return "No se pudo actualizar la inscripción.";
    }
    
    private String registrarInscripcionConEncargados(Inscripcion inscripcion) {
        String mensajeValidacion = validarEncargados(inscripcion.getEncargados());

        if (mensajeValidacion != null) {
            return mensajeValidacion;
        }
        Connection connection = null;
        try {
            connection = Conexion.getInstance().getConnection();
            connection.setAutoCommit(false);
            // 1. Registrar la inscripción
            boolean registrada = inscripcionDAO.registrarInscripcion(
                    connection, inscripcion);
            if (!registrada) {
                throw new SQLException("No se pudo registrar la inscripción.");
            }
            for (EstudianteEncargado relacion : inscripcion.getEncargados()) {
                relacion.setIdEstudiante(inscripcion.getIdEstudiante());
                int idEncargado;
                if (relacion.getIdEncargado() == 0) {
                    Encargado encargado = relacion.getEncargado();
                    encargado.setEstado(true);
                    idEncargado = encargadoDAO.registrar(
                            connection, encargado);
                    if (idEncargado <= 0) {
                        throw new SQLException(
                                "No se pudo registrar uno de los encargados.");
                    }
                } else {
                    Encargado encargadoExistente = encargadoDAO.buscarPorId(
                            connection, relacion.getIdEncargado());
                    if (encargadoExistente == null) {
                        throw new SQLException(
                                "El encargado con ID "
                                + relacion.getIdEncargado()
                                + " no existe.");
                    }
                    if (!encargadoExistente.isEstado()) {
                        throw new SQLException(
                                "El encargado con ID "
                                + relacion.getIdEncargado()
                                + " no está activo.");
                    }

                    idEncargado = encargadoExistente.getIdEncargado();
                }
                relacion.setIdEncargado(idEncargado);
                boolean relacionRegistrada
                        = estudianteEncargadoDAO.registrarRelacion(
                                connection, relacion);
                if (!relacionRegistrada) {
                    throw new SQLException(
                            "No se pudo registrar la relación con el encargado.");
                }
            }
            connection.commit();
            return "Inscripción registrada correctamente.";
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Error al realizar rollback: "
                        + ex.getMessage());
            }
            System.out.println("Error al registrar inscripción y encargados: "
                    + e.getMessage());
            return "No se pudo completar el registro de la inscripción: "
                    + e.getMessage();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión: "
                        + e.getMessage());
            }
        }
    }

    public String cambiarEstado(int idInscripcion, String estado) {
        if (idInscripcion <= 0) {
            return "El ID de la inscripción no es válido.";
        }
        if (estado == null || estado.trim().isEmpty()) {
            return "Debe proporcionar un estado.";
        }
        if (!estadoValido(estado)) {
            return "El estado de la inscripción no es válido.";
        }
        Inscripcion inscripcion = inscripcionDAO.buscarPorId(idInscripcion);

        if (inscripcion == null) {
            return "La inscripción no existe.";
        }
        if (inscripcion.getEstado().equals(estado)) {
            return "La inscripción ya tiene ese estado.";
        }
        if (inscripcionDAO.cambiarEstado(idInscripcion, estado)) {
            return "Estado de la inscripción actualizado correctamente.";
        }
        return "No se pudo actualizar el estado de la inscripción.";
    }

    private boolean estadoValido(String estado) {
        return estado.equals("ACTIVA")
                || estado.equals("RETIRADA")
                || estado.equals("FINALIZADA");
    }
}
