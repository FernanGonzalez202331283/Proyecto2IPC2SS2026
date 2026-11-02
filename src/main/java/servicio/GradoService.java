/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.CarreraDAO;
import Dao.GradoDAO;
import java.util.ArrayList;
import java.util.List;
import modelo.carrera.Carrera;
import modelo.grado.Grado;
import modelo.RespuestaPaginada;
import modelo.grado.GradoResponse;

/**
 *
 * @author fernan
 */
public class GradoService {
private final GradoDAO gradoDAO;
    private final CarreraDAO carreraDAO;

    public GradoService() {
        this.gradoDAO = new GradoDAO();
        this.carreraDAO = new CarreraDAO();
    }

    public String registrarGrado(Grado grado) {
        String errorValidacion = validarYPrepararGrado(grado, false);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        boolean resultado = gradoDAO.insertar(grado);
        return resultado ? "Grado registrado correctamente." : "No se pudo registrar el grado.";
    }

    public String actualizarGrado(Grado grado) {
        String errorValidacion = validarYPrepararGrado(grado, true);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        boolean resultado = gradoDAO.actualizar(grado);
        return resultado ? "Grado actualizado correctamente." : "No se pudo actualizar el grado.";
    }

    public GradoResponse buscarGrado(int idGrado) {
        if (idGrado <= 0) {
            return null;
        }
        Grado grado = gradoDAO.buscarPorId(idGrado);
        return (grado != null) ? new GradoResponse(grado) : null;
    }

    public ArrayList<GradoResponse> obtenerGrados() {
        ArrayList<Grado> listaOriginal = gradoDAO.obtenerTodos();
        ArrayList<GradoResponse> listaResponse = new ArrayList<>();
        
        for (Grado g : listaOriginal) {
            listaResponse.add(new GradoResponse(g));
        }
        return listaResponse;
    }

    public RespuestaPaginada<GradoResponse> obtenerGradosPaginados(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        List<Grado> lista = gradoDAO.obtenerPaginados(limite, offset);
        int totalRegistros = gradoDAO.contarTotal();

        int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
        if (totalPaginas == 0) totalPaginas = 1;

        List<GradoResponse> listaResponse = new ArrayList<>();
        for (Grado grado : lista) {
            listaResponse.add(new GradoResponse(grado));
        }

        return new RespuestaPaginada<>(listaResponse, pagina, totalPaginas, totalRegistros);
    }

    private String validarYPrepararGrado(Grado grado, boolean esEdicion) {
        if (grado == null) return "El grado no puede ser nulo.";

        if (esEdicion) {
            if (grado.getIdGrado() <= 0) return "El ID del grado no es válido.";
            Grado existente = gradoDAO.buscarPorId(grado.getIdGrado());
            if (existente == null) return "El grado no existe.";
        }

        if (grado.getNivel() == null || grado.getNivel().trim().isEmpty()) {
            return "El nivel es obligatorio.";
        }

        String nivel = grado.getNivel().trim().toUpperCase();
        if (!nivelValido(nivel)) return "El nivel no es válido.";

        if (grado.getNombre() == null || grado.getNombre().trim().isEmpty()) {
            return "El nombre del grado es obligatorio.";
        }

        if ("DIVERSIFICADO".equals(nivel)) {
            if (grado.getIdCarrera() == null || grado.getIdCarrera() <= 0) {
                return "Debe seleccionar una carrera para los grados de nivel DIVERSIFICADO.";
            }
            Carrera carrera = carreraDAO.buscarPorId(grado.getIdCarrera());
            if (carrera == null || !carrera.isEstado()) {
                return "La carrera seleccionada no existe o está inactiva.";
            }
        } else {
            grado.setIdCarrera(null);
        }

        grado.setNivel(nivel);
        grado.setNombre(grado.getNombre().trim());

        if (grado.getDescripcion() != null) {
            grado.setDescripcion(grado.getDescripcion().trim());
        }
        return null;
    }

    private boolean nivelValido(String nivel) {
        return "PREPRIMARIA".equals(nivel) || "PRIMARIA".equals(nivel) || "BASICO".equals(nivel) || "DIVERSIFICADO".equals(nivel);
    }
}
