/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.ConfiguracionGradoDAO;
import java.util.ArrayList;
import java.util.List;
import modelo.ConfiguracionGrado;

/**
 *
 * @author fernan
 */
public class ConfiguracionGradoService {
    private final ConfiguracionGradoDAO configuracionGradoDAO;

    public ConfiguracionGradoService() {
        this.configuracionGradoDAO = new ConfiguracionGradoDAO();
    }

    public String registrarConfiguracion(ConfiguracionGrado configuracion) {
        String errorValidacion = validarYFormatear(configuracion);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        boolean resultado = configuracionGradoDAO.insertar(configuracion);
        return resultado ? "Configuración de grado registrada correctamente."
                         : "No se pudo registrar la configuración de grado.";
    }

    public ConfiguracionGrado buscarConfiguracion(int idConfiguracion) {
        if (idConfiguracion <= 0) {
            return null;
        }
        return configuracionGradoDAO.buscarPorId(idConfiguracion);
    }

    public List<ConfiguracionGrado> obtenerConfiguraciones() {
        return configuracionGradoDAO.obtenerTodos();
    }

    public String actualizarConfiguracion(ConfiguracionGrado configuracion) {
        if (configuracion == null) {
            return "La configuración no puede ser nula.";
        }

        if (configuracion.getIdConfiguracion() <= 0) {
            return "El ID de la configuración no es válido.";
        }

        ConfiguracionGrado existente = configuracionGradoDAO.buscarPorId(configuracion.getIdConfiguracion());
        if (existente == null) {
            return "La configuración de grado no existe.";
        }

        String errorValidacion = validarYFormatear(configuracion);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        boolean resultado = configuracionGradoDAO.actualizar(configuracion);
        return resultado ? "Configuración de grado actualizada correctamente."
                         : "No se pudo actualizar la configuración de grado.";
    }

    public String cambiarEstado(int idConfiguracion, boolean estado) {
        if (idConfiguracion <= 0) {
            return "El ID de la configuración no es válido.";
        }

        ConfiguracionGrado existente = configuracionGradoDAO.buscarPorId(idConfiguracion);
        if (existente == null) {
            return "La configuración de grado no existe.";
        }

        boolean resultado = configuracionGradoDAO.actualizarEstado(idConfiguracion, estado);

        if (resultado) {
            return estado ? "Configuración de grado activada correctamente."
                          : "Configuración de grado desactivada correctamente.";
        }

        return "No se pudo cambiar el estado de la configuración.";
    }

    // Método privado que unifica la validación y el formateo de datos
    private String validarYFormatear(ConfiguracionGrado configuracion) {
        if (configuracion == null) {
            return "La configuración no puede ser nula.";
        }

        if (configuracion.getNivel() == null || configuracion.getNivel().trim().isEmpty()) {
            return "El nivel es obligatorio.";
        }

        String nivel = configuracion.getNivel().trim().toUpperCase();
        if (!nivelValido(nivel)) {
            return "El nivel no es válido.";
        }

        if (configuracion.getNombreGrado() == null || configuracion.getNombreGrado().trim().isEmpty()) {
            return "El nombre del grado es obligatorio.";
        }

        if (configuracion.getOrden() <= 0) {
            return "El orden debe ser mayor que cero.";
        }

        // Formatear/limpiar datos en el objeto recibido
        configuracion.setNivel(nivel);
        configuracion.setNombreGrado(configuracion.getNombreGrado().trim());

        return null;
    }

    private boolean nivelValido(String nivel) {
        return "PREPRIMARIA".equals(nivel)
                || "PRIMARIA".equals(nivel)
                || "BASICO".equals(nivel)
                || "DIVERSIFICADO".equals(nivel);
    }
}
