/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.ConfiguracionGradoDAO;
import java.util.ArrayList;
import modelo.ConfiguracionGrado;

/**
 *
 * @author fernan
 */
public class ConfiguracionGradoService {
    private final ConfiguracionGradoDAO configuracionGradoDAO;

    public ConfiguracionGradoService() {
        configuracionGradoDAO = new ConfiguracionGradoDAO();
    }

    public String registrarConfiguracion(ConfiguracionGrado configuracion) {

        if (configuracion == null) {
            return "La configuración no puede ser nula.";
        }

        if (configuracion.getNivel() == null
                || configuracion.getNivel().trim().isEmpty()) {

            return "El nivel es obligatorio.";
        }

        String nivel = configuracion.getNivel().trim().toUpperCase();

        if (!nivelValido(nivel)) {
            return "El nivel no es válido.";
        }

        if (configuracion.getNombreGrado() == null
                || configuracion.getNombreGrado().trim().isEmpty()) {

            return "El nombre del grado es obligatorio.";
        }

        if (configuracion.getOrden() <= 0) {
            return "El orden debe ser mayor que cero.";
        }

        configuracion.setNivel(nivel);
        configuracion.setNombreGrado(
                configuracion.getNombreGrado().trim());

        boolean resultado =
                configuracionGradoDAO.insertar(configuracion);

        if (resultado) {
            return "Configuración de grado registrada correctamente.";
        }

        return "No se pudo registrar la configuración de grado.";
    }

    public ConfiguracionGrado buscarConfiguracion(int idConfiguracion) {

        if (idConfiguracion <= 0) {
            return null;
        }

        return configuracionGradoDAO.buscarPorId(idConfiguracion);
    }

    public ArrayList<ConfiguracionGrado> obtenerConfiguraciones() {

        return configuracionGradoDAO.obtenerTodos();
    }

    public String actualizarConfiguracion(
            ConfiguracionGrado configuracion) {

        if (configuracion == null) {
            return "La configuración no puede ser nula.";
        }

        if (configuracion.getIdConfiguracion() <= 0) {
            return "El ID de la configuración no es válido.";
        }

        ConfiguracionGrado existente =
                configuracionGradoDAO.buscarPorId(
                        configuracion.getIdConfiguracion());

        if (existente == null) {
            return "La configuración de grado no existe.";
        }

        if (configuracion.getNivel() == null
                || configuracion.getNivel().trim().isEmpty()) {

            return "El nivel es obligatorio.";
        }

        String nivel = configuracion.getNivel().trim().toUpperCase();

        if (!nivelValido(nivel)) {
            return "El nivel no es válido.";
        }

        if (configuracion.getNombreGrado() == null
                || configuracion.getNombreGrado().trim().isEmpty()) {

            return "El nombre del grado es obligatorio.";
        }

        if (configuracion.getOrden() <= 0) {
            return "El orden debe ser mayor que cero.";
        }

        configuracion.setNivel(nivel);
        configuracion.setNombreGrado(
                configuracion.getNombreGrado().trim());

        boolean resultado =
                configuracionGradoDAO.actualizar(configuracion);

        if (resultado) {
            return "Configuración de grado actualizada correctamente.";
        }

        return "No se pudo actualizar la configuración de grado.";
    }

    public String cambiarEstado(
            int idConfiguracion,
            boolean estado) {

        if (idConfiguracion <= 0) {
            return "El ID de la configuración no es válido.";
        }

        ConfiguracionGrado existente =
                configuracionGradoDAO.buscarPorId(idConfiguracion);

        if (existente == null) {
            return "La configuración de grado no existe.";
        }

        boolean resultado =
                configuracionGradoDAO.actualizarEstado(
                        idConfiguracion,
                        estado);

        if (resultado) {

            if (estado) {
                return "Configuración de grado activada correctamente.";
            } else {
                return "Configuración de grado desactivada correctamente.";
            }
        }

        return "No se pudo cambiar el estado de la configuración.";
    }

    private boolean nivelValido(String nivel) {

        return nivel.equals("PREPRIMARIA")
                || nivel.equals("PRIMARIA")
                || nivel.equals("BASICO")
                || nivel.equals("DIVERSIFICADO");
    }
}
