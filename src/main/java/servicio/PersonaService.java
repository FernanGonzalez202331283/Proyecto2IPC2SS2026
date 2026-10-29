/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.PersonaDAO;
import java.util.List;
import modelo.Persona;

/**
 *
 * @author fernan
 */
public class PersonaService {
  private final PersonaDAO personaDAO;

    public PersonaService() {
        this.personaDAO = new PersonaDAO();
    }

    public String registrarPersona(Persona persona) {
        if (persona == null) {
            return "Los datos de la persona son obligatorios";
        }

        if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {
            return "Los nombres son obligatorios";
        }

        if (persona.getApellidos() == null || persona.getApellidos().trim().isEmpty()) {
            return "Los apellidos son obligatorios";
        }

        if (persona.getDpi() != null && !persona.getDpi().trim().isEmpty()) {
            String dpi = persona.getDpi().trim();
            if (dpi.length() != 13) {
                return "El DPI debe contener exactamente 13 dígitos";
            }

            if (personaDAO.existeDpi(dpi)) {
                return "El DPI ya está registrado";
            }
        }

        boolean registrado = personaDAO.insertar(persona);
        return registrado ? "OK" : "No se pudo registrar la persona";
    }

    public int registrarPersonaObteniendoId(Persona persona) {
        if (persona == null) {
            return -1;
        }
        return personaDAO.insertarObteniendoId(persona);
    }

    public Persona buscarPersona(int idPersona) {
        return personaDAO.buscarPorId(idPersona);
    }

    public List<Persona> obtenerPersona() {
        return personaDAO.obtenerTodos();
    }

    public boolean actualizarPersona(Persona persona) {
        return personaDAO.actualizar(persona);
    }

    public boolean cambiarEstado(int idPersona, boolean estado) {
        return personaDAO.cambiarEstado(idPersona, estado);
    }
}
