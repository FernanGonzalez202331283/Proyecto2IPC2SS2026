/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad.iteracion2;

import Dao.PersonaDAO;
import Dao.iteracion2.EstudianteDAO;
import java.util.ArrayList;
import modelo.Persona;
import modelo.iteracion2.Estudiante;

/**
 *
 * @author fernan
 */
public class EstudianteService {
    private final EstudianteDAO estudianteDAO;
    private final PersonaDAO personaDAO;
    public EstudianteService() {
        this.estudianteDAO = new EstudianteDAO();
        this.personaDAO = new PersonaDAO();
    }

    public String registrarEstudiante(Estudiante estudiante) {

        if (estudiante == null) {
            return "Debe proporcionar la información del estudiante.";
        }

        Persona persona = estudiante.getPersona();

        if (persona == null) {
            return "Debe proporcionar los datos personales del estudiante.";
        }

        if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {
            return "Los nombres son obligatorios.";
        }
        if (persona.getApellidos() == null || persona.getApellidos().trim().isEmpty()) {
            return "Los apellidos son obligatorios.";
        }
        if (persona.getDpi() == null || persona.getDpi().trim().isEmpty()) {
            return "El DPI es obligatorio.";
        }
        if (estudiante.getCodigoEstudiante() == null
                || estudiante.getCodigoEstudiante().trim().isEmpty()) {
            return "El código del estudiante es obligatorio.";
        }
        if (personaDAO.existeDpi(persona.getDpi())) {
            return "El DPI ingresado ya se encuentra registrado.";
        }
        if (estudianteDAO.existeCodigo(estudiante.getCodigoEstudiante())) {
            return "El código de estudiante ya se encuentra registrado.";
        }

        if (estudiante.getEstado() == null || estudiante.getEstado().trim().isEmpty()) {
            estudiante.setEstado("ACTIVO");
        }

        if (!estadoValido(estudiante.getEstado())) {
            return "El estado del estudiante no es válido.";
        }

        if (estudianteDAO.registrarEstudiante(estudiante)) {
            return "Estudiante registrado correctamente.";
        }

        return "No se pudo registrar el estudiante.";
    }

    public Estudiante buscarPorId(int idEstudiante) {

        if (idEstudiante <= 0) {
            return null;
        }

        return estudianteDAO.buscarPorId(idEstudiante);
    }

    public ArrayList<Estudiante> obtenerTodos() {

        return estudianteDAO.obtenerTodos();
    }

    public String actualizar(Estudiante estudiante) {

        if (estudiante == null) {
            return "Debe proporcionar la información del estudiante.";
        }

        if (estudiante.getIdEstudiante() <= 0) {
            return "El ID del estudiante no es válido.";
        }

        Estudiante estudianteActual = estudianteDAO.buscarPorId(estudiante.getIdEstudiante());

        if (estudianteActual == null) {
            return "El estudiante no existe.";
        }

        Persona persona = estudiante.getPersona();

        if (persona == null) {
            return "Debe proporcionar los datos personales del estudiante.";
        }

        if (persona.getIdPersona() != estudianteActual.getIdPersona()) {
            return "Los datos personales no corresponden al estudiante indicado.";
        }

        if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {
            return "Los nombres son obligatorios.";
        }

        if (persona.getApellidos() == null || persona.getApellidos().trim().isEmpty()) {
            return "Los apellidos son obligatorios.";
        }

        if (persona.getDpi() == null || persona.getDpi().trim().isEmpty()) {
            return "El DPI es obligatorio.";
        }

        if (estudiante.getCodigoEstudiante() == null
                || estudiante.getCodigoEstudiante().trim().isEmpty()) {
            return "El código del estudiante es obligatorio.";
        }

        if (personaDAO.existeDpi(persona.getDpi())
                && !persona.getDpi().equals(estudianteActual.getPersona().getDpi())) {
            return "El DPI ingresado ya se encuentra registrado.";
        }

        if (estudianteDAO.existeCodigo(
                estudiante.getCodigoEstudiante(),
                estudiante.getIdEstudiante())) {

            return "El código de estudiante ya se encuentra registrado.";
        }

        if (estudiante.getEstado() != null && !estadoValido(estudiante.getEstado())) {
            return "El estado del estudiante no es válido.";
        }

        if (estudianteDAO.actualizar(estudiante)) {
            return "Estudiante actualizado correctamente.";
        }

        return "No se pudo actualizar el estudiante.";
    }

    public String cambiarEstado(int idEstudiante, String estado) {

        if (idEstudiante <= 0) {
            return "El ID del estudiante no es válido.";
        }

        if (estado == null || estado.trim().isEmpty()) {
            return "Debe proporcionar un estado.";
        }

        if (!estadoValido(estado)) {
            return "El estado del estudiante no es válido.";
        }

        Estudiante estudiante = estudianteDAO.buscarPorId(idEstudiante);

        if (estudiante == null) {
            return "El estudiante no existe.";
        }

        if (estudianteDAO.cambiarEstado(idEstudiante, estado)) {
            return "Estado del estudiante actualizado correctamente.";
        }

        return "No se pudo actualizar el estado del estudiante.";
    }

    private boolean estadoValido(String estado) {

        return estado.equals("ACTIVO")
                || estado.equals("INACTIVO")
                || estado.equals("GRADUADO");
    }
}
