/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad.iteracion2;

import Dao.PersonaDAO;
import Dao.iteracion2.EmpleadoDAO;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Persona;
import modelo.Usuario;
import modelo.iteracion2.Empleado;

/**
 *
 * @author fernan
 */
public class EmpleadoService {
    
    private EmpleadoDAO empleadoDAO;
    private PersonaDAO personaDAO;

    public EmpleadoService() {
        empleadoDAO = new EmpleadoDAO();
        personaDAO = new PersonaDAO();
    }

    // Registrar empleado
    public String registrar(Empleado empleado) {

        if (empleado == null) {
            return "Los datos del empleado son obligatorios.";
        }

        if (empleado.getPersona() == null) {
            return "La información personal del empleado es obligatoria.";
        }

        Persona persona = empleado.getPersona();

        // Validar nombres
        if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {
            return "Los nombres son obligatorios.";
        }

        // Validar apellidos
        if (persona.getApellidos() == null || persona.getApellidos().trim().isEmpty()) {
            return "Los apellidos son obligatorios.";
        }

        // Validar DPI
        if (persona.getDpi() == null || persona.getDpi().trim().isEmpty()) {
            return "El DPI es obligatorio.";
        }

        // Validar teléfono
        if (persona.getTelefono() == null || persona.getTelefono().trim().isEmpty()) {
            return "El teléfono es obligatorio.";
        }

        // Validar puesto
        if (empleado.getPuesto() == null || empleado.getPuesto().trim().isEmpty()) {
            return "El puesto del empleado es obligatorio.";
        }

        // Validar fecha de contratación
        if (empleado.getFechaContratacion() == null
                || empleado.getFechaContratacion().trim().isEmpty()) {
            return "La fecha de contratación es obligatoria.";
        }

        try {
            LocalDate.parse(empleado.getFechaContratacion());
        } catch (Exception e) {
            return "La fecha de contratación no tiene un formato válido. Utilice YYYY-MM-DD.";
        }

        // Validar salario
        if (empleado.getSalario() <= 0
                || Double.isNaN(empleado.getSalario())
                || Double.isInfinite(empleado.getSalario())) {
            return "El salario debe ser mayor que cero.";
        }

        // Verificar DPI duplicado
        if (personaDAO.existeDpi(persona.getDpi())) {
            return "El DPI ingresado ya se encuentra registrado.";
        }

        // El empleado se registra inicialmente activo
        empleado.setEstado(true);

        if (empleadoDAO.registrar(empleado)) {
            return "Empleado registrado correctamente.";
        }

        return "No fue posible registrar el empleado.";
    }
    

    // Consultar empleado por ID
    public Empleado buscarPorId(int idEmpleado) {

        if (idEmpleado <= 0) {
            return null;
        }

        return empleadoDAO.buscarPorId(idEmpleado);
    }

    // Consultar todos los empleados
    public ArrayList<Empleado> obtenerTodos() {

        return empleadoDAO.obtenerTodos();
    }

    // Actualizar empleado
    public String actualizar(Empleado empleado) {

        if (empleado == null) {
            return "Los datos del empleado son obligatorios.";
        }

        if (empleado.getIdEmpleado() <= 0) {
            return "El ID del empleado no es válido.";
        }

        if (empleado.getPersona() == null) {
            return "La información personal del empleado es obligatoria.";
        }

        // Verificar que el empleado exista
        Empleado existente = empleadoDAO.buscarPorId(empleado.getIdEmpleado());

        if (existente == null) {
            return "El empleado que desea actualizar no existe.";
        }

        Persona persona = empleado.getPersona();

        // Validar nombres
        if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {
            return "Los nombres son obligatorios.";
        }

        // Validar apellidos
        if (persona.getApellidos() == null || persona.getApellidos().trim().isEmpty()) {
            return "Los apellidos son obligatorios.";
        }

        // Validar DPI
        if (persona.getDpi() == null || persona.getDpi().trim().isEmpty()) {
            return "El DPI es obligatorio.";
        }

        // Validar teléfono
        if (persona.getTelefono() == null || persona.getTelefono().trim().isEmpty()) {
            return "El teléfono es obligatorio.";
        }

        // Validar puesto
        if (empleado.getPuesto() == null || empleado.getPuesto().trim().isEmpty()) {
            return "El puesto del empleado es obligatorio.";
        }

        // Validar fecha de contratación
        if (empleado.getFechaContratacion() == null
                || empleado.getFechaContratacion().trim().isEmpty()) {
            return "La fecha de contratación es obligatoria.";
        }

        try {
            LocalDate.parse(empleado.getFechaContratacion());
        } catch (Exception e) {
            return "La fecha de contratación no tiene un formato válido. Utilice YYYY-MM-DD.";
        }

        // Validar salario
        if (empleado.getSalario() <= 0
                || Double.isNaN(empleado.getSalario())
                || Double.isInfinite(empleado.getSalario())) {
            return "El salario debe ser mayor que cero.";
        }

        // Verificar que el DPI no pertenezca a otra persona
        if (!persona.getDpi().equals(existente.getPersona().getDpi())
                && personaDAO.existeDpi(persona.getDpi())) {

            return "El DPI ingresado ya se encuentra registrado.";
        }

        // Mantener la relación con la persona original
        persona.setIdPersona(existente.getPersona().getIdPersona());

        if (empleadoDAO.actualizar(empleado)) {
            return "Empleado actualizado correctamente.";
        }

        return "No fue posible actualizar el empleado.";
    }

    // Cambiar estado del empleado
    public String cambiarEstado(int idEmpleado, boolean estado) {

        if (idEmpleado <= 0) {
            return "El ID del empleado no es válido.";
        }

        // Verificar que el empleado exista
        Empleado empleado = empleadoDAO.buscarPorId(idEmpleado);

        if (empleado == null) {
            return "El empleado no existe.";
        }

        // Verificar si ya tiene el estado solicitado
        if (empleado.isEstado() == estado) {

            if (estado) {
                return "El empleado ya se encuentra activo.";
            } else {
                return "El empleado ya se encuentra inactivo.";
            }
        }

        if (empleadoDAO.cambiarEstado(idEmpleado, estado)) {

            if (estado) {
                return "Empleado activado correctamente.";
            } else {
                return "Empleado desactivado correctamente.";
            }
        }

        return "No fue posible cambiar el estado del empleado.";
    }
}
