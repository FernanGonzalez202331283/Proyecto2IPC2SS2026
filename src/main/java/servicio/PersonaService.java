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
    private PersonaDAO personaDAO;
    
    public PersonaService(){
        personaDAO = new PersonaDAO();
    }
    
    public String registrarPersona(Persona persona) {
        if (persona == null) {
            return "Los datos de la persona son obligatorios";
        }

        if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {
            return "los nombres son obligatorios";
        }

        if (persona.getApellidos() == null || persona.getApellidos().trim().isEmpty()) {
            return "Los apellidos son obligatorios";
        }

        if (persona.getDpi() != null && !persona.getDpi().trim().isEmpty()) {
            String dpi = persona.getDpi().trim();
            if (dpi.length() == 12) {
                return "El dpi debe de contener 13 digitos";
            }
            
            if (personaDAO.existeDpi(persona.getDpi())) {
                return "EL dpi ya esta registrado";
            }
        }
        
        boolean registrado = personaDAO.insertar(persona);

           if (registrado) {
               return "OK";
           }

           return "No se pudo registrar la persona";
    }
    
    public Persona buscarPersona(int idPersona){
        return personaDAO.buscarPorId(idPersona);
    }
    
    public List<Persona> obtenerPersona(){
        return personaDAO.obtenerTodos();
    }
    
    public boolean actualizarPersona(Persona persona){
        return personaDAO.actualizar(persona);
    }
    
    public boolean cambiarEstado(int idPersona, boolean estado){
        return personaDAO.cambiarEstado(idPersona, estado);
    }
}
