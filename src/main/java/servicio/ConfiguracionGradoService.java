/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.ConfiguracionGradoDAO;
import java.util.List;
import java.util.stream.Collectors;
import modelo.ConfiguracionGrados.CofiguracionGradoResponse;
import modelo.ConfiguracionGrados.ConfiguracionGrado;
import modelo.ConfiguracionGrados.ConfiguracionGradoRequest;
import modelo.RespuestaPaginada;

/**
 *
 * @author fernan
 */
public class ConfiguracionGradoService {
    private final ConfiguracionGradoDAO configuracionGradoDAO;

    public ConfiguracionGradoService() {
        this.configuracionGradoDAO = new ConfiguracionGradoDAO();
    }

    private CofiguracionGradoResponse mapearAResponse(ConfiguracionGrado c) {
        return new CofiguracionGradoResponse(
                c.getIdConfiguracion(),
                c.getNivel(),
                c.getNombreGrado(),
                c.getOrden(),
                c.isEstado()
        );
    }

    public String registrarConfiguracion(ConfiguracionGradoRequest peticion) {
        String errorValidacion = validarPeticion(peticion);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        ConfiguracionGrado nuevaConfig = new ConfiguracionGrado();
        nuevaConfig.setNivel(peticion.getNivel().trim().toUpperCase());
        nuevaConfig.setNombreGrado(peticion.getNombreGrado().trim());
        nuevaConfig.setOrden(peticion.getOrden());
        nuevaConfig.setEstado(true); // Por defecto activo al crear

        boolean resultado = configuracionGradoDAO.insertar(nuevaConfig);
        return resultado ? "Configuración de grado registrada correctamente."
                         : "No se pudo registrar la configuración de grado.";
    }

    public CofiguracionGradoResponse buscarConfiguracion(int idConfiguracion) {
        if (idConfiguracion <= 0) return null;
        
        ConfiguracionGrado configuracion = configuracionGradoDAO.buscarPorId(idConfiguracion);
        if (configuracion != null) {
            return mapearAResponse(configuracion);
        }
        return null;
    }

    public List<CofiguracionGradoResponse> obtenerConfiguraciones() {
        return configuracionGradoDAO.obtenerTodos().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public RespuestaPaginada<CofiguracionGradoResponse> obtenerConfiguracionesPaginadas(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        List<ConfiguracionGrado> lista = configuracionGradoDAO.obtenerPaginados(limite, offset);
        
        List<CofiguracionGradoResponse> listaResponse = lista.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
                
        int totalRegistros = configuracionGradoDAO.contarTotal();

        return new RespuestaPaginada<>(listaResponse, pagina, limite, totalRegistros);
    }

    public String actualizarConfiguracion(int id, ConfiguracionGradoRequest peticion) {
        if (peticion == null) return "La petición no puede ser nula.";
        if (id <= 0) return "El ID de la configuración no es válido.";

        ConfiguracionGrado existente = configuracionGradoDAO.buscarPorId(id);
        if (existente == null) return "La configuración de grado no existe.";

        String errorValidacion = validarPeticion(peticion);
        if (errorValidacion != null) return errorValidacion;

        existente.setNivel(peticion.getNivel().trim().toUpperCase());
        existente.setNombreGrado(peticion.getNombreGrado().trim());
        existente.setOrden(peticion.getOrden());

        boolean resultado = configuracionGradoDAO.actualizar(existente);
        return resultado ? "Configuración de grado actualizada correctamente."
                         : "No se pudo actualizar la configuración de grado.";
    }

    public String cambiarEstado(int idConfiguracion, boolean estado) {
        if (idConfiguracion <= 0) return "El ID de la configuración no es válido.";

        ConfiguracionGrado existente = configuracionGradoDAO.buscarPorId(idConfiguracion);
        if (existente == null) return "La configuración de grado no existe.";

        boolean resultado = configuracionGradoDAO.actualizarEstado(idConfiguracion, estado);

        if (resultado) {
            return estado ? "Configuración de grado activada correctamente."
                          : "Configuración de grado desactivada correctamente.";
        }
        return "No se pudo cambiar el estado de la configuración.";
    }

    private String validarPeticion(ConfiguracionGradoRequest peticion) {
        if (peticion == null) return "La petición no puede ser nula.";

        if (peticion.getNivel() == null || peticion.getNivel().trim().isEmpty()) {
            return "El nivel es obligatorio.";
        }

        String nivel = peticion.getNivel().trim().toUpperCase();
        if (!nivelValido(nivel)) return "El nivel no es válido.";

        if (peticion.getNombreGrado() == null || peticion.getNombreGrado().trim().isEmpty()) {
            return "El nombre del grado es obligatorio.";
        }

        if (peticion.getOrden() <= 0) return "El orden debe ser mayor que cero.";

        return null;
    }

    private boolean nivelValido(String nivel) {
        return "PREPRIMARIA".equals(nivel)
                || "PRIMARIA".equals(nivel)
                || "BASICO".equals(nivel)
                || "DIVERSIFICADO".equals(nivel);
    }
}
