/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.CarreraDAO;
import java.util.ArrayList;
import modelo.Carrera;

/**
 *
 * @author fernan
 */
public class CarreraService {
    private final CarreraDAO carreraDAO;

    public CarreraService() {
        carreraDAO = new CarreraDAO();
    }

    public String registrarCarrera(Carrera carrera) {

        if (carrera == null) {
            return "La carrera no puede ser nula.";
        }

        if (carrera.getNombre() == null
                || carrera.getNombre().trim().isEmpty()) {
            return "El nombre de la carrera es obligatorio.";
        }

        String nombre = carrera.getNombre().trim();
        ArrayList<Carrera> carreras = carreraDAO.obtenerTodos();

        for (Carrera existente : carreras) {

            if (existente.getNombre()
                    .equalsIgnoreCase(nombre)) {

                return "Ya existe una carrera con ese nombre.";
            }
        }

        carrera.setNombre(nombre);

        if (carrera.getDescripcion() != null) {
            carrera.setDescripcion(
                    carrera.getDescripcion().trim());
        }

        carrera.setEstado(true);

        boolean resultado = carreraDAO.insertar(carrera);

        if (resultado) {
            return "Carrera registrada correctamente.";
        }

        return "No se pudo registrar la carrera.";
    }

    public Carrera buscarCarrera(int idCarrera) {

        if (idCarrera <= 0) {
            return null;
        }

        return carreraDAO.buscarPorId(idCarrera);
    }

    public ArrayList<Carrera> obtenerCarreras() {

        return carreraDAO.obtenerTodos();
    }

    public String actualizarCarrera(Carrera carrera) {

        if (carrera == null) {
            return "La carrera no puede ser nula.";
        }

        if (carrera.getIdCarrera() <= 0) {
            return "El ID de la carrera no es válido.";
        }

        Carrera existente = carreraDAO.buscarPorId(carrera.getIdCarrera());

        if (existente == null) {
            return "La carrera no existe.";
        }

        if (carrera.getNombre() == null || carrera.getNombre().trim().isEmpty()) {
            return "El nombre de la carrera es obligatorio.";
        }

        String nombre =
                carrera.getNombre().trim();

        ArrayList<Carrera> carreras =
                carreraDAO.obtenerTodos();

        for (Carrera otraCarrera : carreras) {

            if (otraCarrera.getIdCarrera()
                    != carrera.getIdCarrera()
                    && otraCarrera.getNombre()
                            .equalsIgnoreCase(nombre)) {

                return "Ya existe otra carrera con ese nombre.";
            }
        }

        carrera.setNombre(nombre);

        if (carrera.getDescripcion() != null) {
            carrera.setDescripcion(carrera.getDescripcion().trim());
        }

        carrera.setEstado(existente.isEstado());

        boolean resultado = carreraDAO.actualizar(carrera);

        if (resultado) {
            return "Carrera actualizada correctamente.";
        }

        return "No se pudo actualizar la carrera.";
    }

    public String cambiarEstado(
            int idCarrera,
            boolean estado) {

        if (idCarrera <= 0) {
            return "El ID de la carrera no es válido.";
        }

        Carrera existente = carreraDAO.buscarPorId(idCarrera);

        if (existente == null) {
            return "La carrera no existe.";
        }

        boolean resultado = carreraDAO.actualizarEstado( idCarrera, estado);
        if (resultado) {
            return "Estado de la carrera actualizado correctamente.";
        }

        return "No se pudo actualizar el estado de la carrera.";
    }
}
