/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.PersonaDAO;
import java.util.List;
import java.util.stream.Collectors;
import modelo.Persona.Persona;
import modelo.Persona.PersonaActualizacionRequest;
import modelo.Persona.PersonaRegistroRequest;
import modelo.Persona.PersonaResponse;
import modelo.PersonaSeleccionable;

/**
 *
 * @author fernan
 */
public class PersonaService {
private final PersonaDAO personaDAO;

    public PersonaService() {
        this.personaDAO = new PersonaDAO();
    }

    public String registrarPersona(PersonaRegistroRequest request) {
        if (request == null) return "Los datos de la persona son obligatorios";
        if (request.getNombres() == null || request.getNombres().trim().isEmpty()) return "Los nombres son obligatorios";
        if (request.getApellidos() == null || request.getApellidos().trim().isEmpty()) return "Los apellidos son obligatorios";

        if (request.getDpi() != null && !request.getDpi().trim().isEmpty()) {
            String dpi = request.getDpi().trim();
            if (dpi.length() != 13) return "El DPI debe contener exactamente 13 dígitos";
            if (personaDAO.existeDpi(dpi)) return "El DPI ya está registrado";
        }

        Persona persona = new Persona();
        persona.setNombres(request.getNombres());
        persona.setApellidos(request.getApellidos());
        persona.setDpi(request.getDpi());
        persona.setTelefono(request.getTelefono());
        persona.setCorreo(request.getCorreo());
        persona.setDireccion(request.getDireccion());
        persona.setEstado(true); 

        boolean registrado = personaDAO.insertar(persona);
        return registrado ? "OK" : "No se pudo registrar la persona";
    }

    public PersonaResponse buscarPersona(int idPersona) {
        Persona persona = personaDAO.buscarPorId(idPersona);
        return (persona != null) ? new PersonaResponse(persona) : null;
    }

    public List<PersonaResponse> obtenerPersonas() {
        return personaDAO.obtenerTodos()
                .stream()
                .map(PersonaResponse::new)
                .collect(Collectors.toList());
    }

    public boolean actualizarPersona(int idPersona, PersonaActualizacionRequest request) {
        Persona personaExistente = personaDAO.buscarPorId(idPersona);
        if (personaExistente == null) return false;

        personaExistente.setNombres(request.getNombres());
        personaExistente.setApellidos(request.getApellidos());
        personaExistente.setTelefono(request.getTelefono());
        personaExistente.setCorreo(request.getCorreo());
        personaExistente.setDireccion(request.getDireccion());
        personaExistente.setEstado(request.isEstado());

        return personaDAO.actualizar(personaExistente);
    }

    public boolean cambiarEstado(int idPersona, boolean estado) {
        return personaDAO.cambiarEstado(idPersona, estado);
    }

    public List<PersonaSeleccionable> obtenerPersonasActivasConRol() {
        return personaDAO.obtenerPersonasActivasConRol();
    }
}
