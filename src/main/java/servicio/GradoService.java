/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.GradoDAO;
import java.util.ArrayList;
import modelo.Grado;

/**
 *
 * @author fernan
 */
public class GradoService {
    private final GradoDAO gradoDAO;

    public GradoService() {
        gradoDAO = new GradoDAO();
    }

    public String registrarGrado(Grado grado) {

        if (grado == null) {
            return "El grado no puede ser nulo.";
        }

        if (grado.getNivel() == null
                || grado.getNivel().trim().isEmpty()) {

            return "El nivel es obligatorio.";
        }

        String nivel = grado.getNivel()
                .trim()
                .toUpperCase();

        if (!nivelValido(nivel)) {
            return "El nivel no es válido.";
        }

        if (grado.getNombre() == null
                || grado.getNombre().trim().isEmpty()) {

            return "El nombre del grado es obligatorio.";
        }

        grado.setNivel(nivel);
        grado.setNombre(grado.getNombre().trim());

        if (grado.getDescripcion() != null) {
            grado.setDescripcion(
                    grado.getDescripcion().trim());
        }

        boolean resultado =
                gradoDAO.insertar(grado);

        if (resultado) {
            return "Grado registrado correctamente.";
        }

        return "No se pudo registrar el grado.";
    }

    public Grado buscarGrado(int idGrado) {

        if (idGrado <= 0) {
            return null;
        }

        return gradoDAO.buscarPorId(idGrado);
    }

    public ArrayList<Grado> obtenerGrados() {

        return gradoDAO.obtenerTodos();
    }

    public String actualizarGrado(Grado grado) {

        if (grado == null) {
            return "El grado no puede ser nulo.";
        }

        if (grado.getIdGrado() <= 0) {
            return "El ID del grado no es válido.";
        }

        Grado existente =
                gradoDAO.buscarPorId(
                        grado.getIdGrado());

        if (existente == null) {
            return "El grado no existe.";
        }

        if (grado.getNivel() == null
                || grado.getNivel().trim().isEmpty()) {

            return "El nivel es obligatorio.";
        }

        String nivel = grado.getNivel()
                .trim()
                .toUpperCase();

        if (!nivelValido(nivel)) {
            return "El nivel no es válido.";
        }

        if (grado.getNombre() == null
                || grado.getNombre().trim().isEmpty()) {

            return "El nombre del grado es obligatorio.";
        }

        grado.setNivel(nivel);
        grado.setNombre(grado.getNombre().trim());

        if (grado.getDescripcion() != null) {
            grado.setDescripcion(
                    grado.getDescripcion().trim());
        }

        boolean resultado =
                gradoDAO.actualizar(grado);

        if (resultado) {
            return "Grado actualizado correctamente.";
        }

        return "No se pudo actualizar el grado.";
    }

    private boolean nivelValido(String nivel) {

        return nivel.equals("PREPRIMARIA")
                || nivel.equals("PRIMARIA")
                || nivel.equals("BASICO")
                || nivel.equals("DIVERSIFICADO");
    }
}
