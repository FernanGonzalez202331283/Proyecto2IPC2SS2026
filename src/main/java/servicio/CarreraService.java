/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.CarreraDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import modelo.carrera.Carrera;
import modelo.RespuestaPaginada;
import modelo.carrera.CarreraRequest;
import modelo.carrera.CarreraResponse;

/**
 *
 * @author fernan
 */
public class CarreraService {
    private final CarreraDAO carreraDAO;

    public CarreraService() {
        carreraDAO = new CarreraDAO();
    }

    private CarreraResponse mapearAResponse(Carrera c) {
        return new CarreraResponse(c.getIdCarrera(), c.getNombre(), c.getDescripcion(), c.isEstado());
    }

    public RespuestaPaginada<CarreraResponse> obtenerCarrerasPaginadas(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        List<Carrera> lista = carreraDAO.obtenerPaginados(limite, offset);
        
        List<CarreraResponse> listaResponse = lista.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
                
        int totalRegistros = carreraDAO.contarTotalCarreras();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
        if (totalPaginas == 0) totalPaginas = 1;

        return new RespuestaPaginada<>(listaResponse, pagina, limite, totalRegistros);
    }

    public String registrarCarrera(CarreraRequest peticion) {
        if (peticion == null) {
            return "La petición no puede ser nula.";
        }
        if (peticion.getNombre() == null || peticion.getNombre().trim().isEmpty()) {
            return "El nombre de la carrera es obligatorio.";
        }

        String nombre = peticion.getNombre().trim();
        ArrayList<Carrera> carreras = carreraDAO.obtenerTodos();

        for (Carrera existente : carreras) {
            if (existente.getNombre().equalsIgnoreCase(nombre)) {
                return "Ya existe una carrera con ese nombre.";
            }
        }

        Carrera nuevaCarrera = new Carrera();
        nuevaCarrera.setNombre(nombre);
        if (peticion.getDescripcion() != null) {
            nuevaCarrera.setDescripcion(peticion.getDescripcion().trim());
        }
        nuevaCarrera.setEstado(true);

        boolean resultado = carreraDAO.insertar(nuevaCarrera);
        return resultado ? "Carrera registrada correctamente." : "No se pudo registrar la carrera.";
    }

    public CarreraResponse buscarCarrera(int idCarrera) {
        if (idCarrera <= 0) return null;
        
        Carrera carrera = carreraDAO.buscarPorId(idCarrera);
        if (carrera != null) {
            return mapearAResponse(carrera);
        }
        return null;
    }

    public List<CarreraResponse> obtenerCarrerasActivas() {
        return carreraDAO.obtenerActivas().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    public String actualizarCarrera(int id, CarreraRequest peticion) {
        if (peticion == null) return "La petición no puede ser nula.";
        if (id <= 0) return "El ID de la carrera no es válido.";

        Carrera existente = carreraDAO.buscarPorId(id);
        if (existente == null) return "La carrera no existe.";

        if (peticion.getNombre() == null || peticion.getNombre().trim().isEmpty()) {
            return "El nombre de la carrera es obligatorio.";
        }

        String nombre = peticion.getNombre().trim();
        ArrayList<Carrera> carreras = carreraDAO.obtenerTodos();

        for (Carrera otraCarrera : carreras) {
            if (otraCarrera.getIdCarrera() != id && otraCarrera.getNombre().equalsIgnoreCase(nombre)) {
                return "Ya existe otra carrera con ese nombre.";
            }
        }

        existente.setNombre(nombre);
        if (peticion.getDescripcion() != null) {
            existente.setDescripcion(peticion.getDescripcion().trim());
        }

        boolean resultado = carreraDAO.actualizar(existente);
        return resultado ? "Carrera actualizada correctamente." : "No se pudo actualizar la carrera.";
    }

    public String cambiarEstado(int idCarrera, boolean estado) {
        if (idCarrera <= 0) return "El ID de la carrera no es válido.";
        
        Carrera existente = carreraDAO.buscarPorId(idCarrera);
        if (existente == null) return "La carrera no existe.";

        boolean resultado = carreraDAO.actualizarEstado(idCarrera, estado);
        return resultado ? "Estado de la carrera actualizado correctamente." : "No se pudo actualizar el estado de la carrera.";
    }
}
