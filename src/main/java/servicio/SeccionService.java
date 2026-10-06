/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.AñoLectivoDAO;
import Dao.GradoDAO;
import Dao.SeccionDAO;
import java.util.ArrayList;
import modelo.AñoLectivo;
import modelo.Grado;
import modelo.Seccion;

/**
 *
 * @author fernan
 */
public class SeccionService {
    private final SeccionDAO seccionDAO;
    private final GradoDAO gradoDAO;
    private final AñoLectivoDAO añoLectivoDAO;

    public SeccionService() {
        seccionDAO = new SeccionDAO();
        gradoDAO = new GradoDAO();
        añoLectivoDAO = new AñoLectivoDAO();
    }

    public String registrarSeccion(Seccion seccion) {

        if (seccion == null) {
            return "La sección no puede ser nula.";
        }

        if (seccion.getIdGrado() <= 0) {
            return "El ID del grado no es válido.";
        }

        if (seccion.getIdAñoLectivo() <= 0) {
            return "El ID del año lectivo no es válido.";
        }

        if (seccion.getNombre() == null
                || seccion.getNombre().trim().isEmpty()) {
            return "El nombre de la sección es obligatorio.";
        }

        if (seccion.getCapacidad() <= 0) {
            return "La capacidad debe ser mayor que cero.";
        }

        Grado grado = gradoDAO.buscarPorId(
                seccion.getIdGrado());

        if (grado == null) {
            return "El grado no existe.";
        }

        AñoLectivo añoLectivo = añoLectivoDAO.buscarPorId(
                seccion.getIdAñoLectivo());

        if (añoLectivo == null) {
            return "El año lectivo no existe.";
        }

        seccion.setNombre(seccion.getNombre().trim());
        seccion.setEstado(true);

        boolean resultado = seccionDAO.insertar(seccion);

        if (resultado) {
            return "Sección registrada correctamente.";
        }

        return "No se pudo registrar la sección.";
    }

    public Seccion buscarSeccion(int idSeccion) {

        if (idSeccion <= 0) {
            return null;
        }

        return seccionDAO.buscarPorId(idSeccion);
    }

    public ArrayList<Seccion> obtenerSecciones() {

        return seccionDAO.obtenerTodos();
    }

    public String actualizarSeccion(Seccion seccion) {

        if (seccion == null) {
            return "La sección no puede ser nula.";
        }

        if (seccion.getIdSeccion() <= 0) {
            return "El ID de la sección no es válido.";
        }

        Seccion existente =
                seccionDAO.buscarPorId(
                        seccion.getIdSeccion());

        if (existente == null) {
            return "La sección no existe.";
        }

        if (seccion.getIdGrado() <= 0) {
            return "El ID del grado no es válido.";
        }

        if (seccion.getIdAñoLectivo() <= 0) {
            return "El ID del año lectivo no es válido.";
        }

        if (seccion.getNombre() == null
                || seccion.getNombre().trim().isEmpty()) {
            return "El nombre de la sección es obligatorio.";
        }

        if (seccion.getCapacidad() <= 0) {
            return "La capacidad debe ser mayor que cero.";
        }

        Grado grado = gradoDAO.buscarPorId(
                seccion.getIdGrado());

        if (grado == null) {
            return "El grado no existe.";
        }

        AñoLectivo añoLectivo = añoLectivoDAO.buscarPorId(
                seccion.getIdAñoLectivo());

        if (añoLectivo == null) {
            return "El año lectivo no existe.";
        }

        seccion.setNombre(seccion.getNombre().trim());
        seccion.setEstado(existente.isEstado());

        boolean resultado =
                seccionDAO.actualizar(seccion);

        if (resultado) {
            return "Sección actualizada correctamente.";
        }

        return "No se pudo actualizar la sección.";
    }

    public String cambiarEstado(
            int idSeccion,
            boolean estado) {

        if (idSeccion <= 0) {
            return "El ID de la sección no es válido.";
        }

        Seccion existente =
                seccionDAO.buscarPorId(idSeccion);

        if (existente == null) {
            return "La sección no existe.";
        }

        boolean resultado =
                seccionDAO.actualizarEstado(
                        idSeccion,
                        estado);

        if (resultado) {
            return "Estado de la sección actualizado correctamente.";
        }

        return "No se pudo actualizar el estado de la sección.";
    }
}
