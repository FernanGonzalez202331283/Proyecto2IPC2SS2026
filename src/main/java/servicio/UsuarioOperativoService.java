/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.PersonaDAO;
import Dao.UsuarioDAO;
import Dao.UsuarioOperativoDAO;
import java.time.LocalDate;
import java.util.List;
import modelo.Persona.Persona;
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.UsuarioOperativo;
import modelo.iteracion2.Empleado;

/**
 *
 * @author fernan
 */
public class UsuarioOperativoService {
    private final UsuarioOperativoDAO usuarioOperativoDAO;
    private final PersonaDAO personaDAO;
    private final UsuarioDAO usuarioDAO;

    public UsuarioOperativoService() {
        this.usuarioOperativoDAO = new UsuarioOperativoDAO();
        this.personaDAO = new PersonaDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public RespuestaPaginada<UsuarioOperativo> obtenerOperativosPaginados(int pagina, int limite) {
        if (pagina < 1) {
            pagina = 1;
        }
        if (limite < 1) {
            limite = 10;
        }

        int offset = (pagina - 1) * limite;
        List<UsuarioOperativo> lista = usuarioOperativoDAO.obtenerOperativosPaginados(limite, offset);
        int totalRegistros = usuarioOperativoDAO.contarOperativos();

        int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
        if (totalPaginas == 0) {
            totalPaginas = 1;
        }

        return new RespuestaPaginada<>(lista, pagina, totalPaginas, totalRegistros);
    }

    public String registrar(UsuarioOperativo dto) {
        if (dto == null) {
            return "Los datos del formulario son obligatorios.";
        }

        Persona persona = dto.getPersona();
        Empleado empleado = dto.getEmpleado();

        if (persona == null) {
            return "La información personal es obligatoria.";
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

        if (personaDAO.existeDpi(persona.getDpi())) {
            return "El DPI ingresado ya se encuentra registrado.";
        }

        if (empleado == null) {
            return "La información laboral es obligatoria.";
        }

        if (empleado.getPuesto() == null || empleado.getPuesto().trim().isEmpty()) {
            return "El puesto es obligatorio.";
        }

        if (empleado.getFechaContratacion() == null || empleado.getFechaContratacion().trim().isEmpty()) {
            return "La fecha de contratación es obligatoria.";
        }

        try {
            LocalDate.parse(empleado.getFechaContratacion());
        } catch (Exception e) {
            return "La fecha de contratación no tiene un formato válido (YYYY-MM-DD).";
        }

        if (empleado.getSalario() <= 0) {
            return "El salario debe ser mayor a cero.";
        }

        if (dto.isCrearCuentaUsuario()) {
            Usuario usuario = dto.getUsuario();

            if (usuario == null) {
                return "Debe ingresar las credenciales para la cuenta de usuario.";
            }

            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                return "El nombre de usuario es obligatorio.";
            }

            if (usuarioDAO.buscarPorUsername(usuario.getUsername()) != null) {
                return "El nombre de usuario ya se encuentra registrado.";
            }

            if (usuario.getContraseña() == null || usuario.getContraseña().trim().isEmpty()) {
                return "La contraseña es obligatoria.";
            }

            if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
                return "El rol del usuario es obligatorio.";
            }
        }

        return usuarioOperativoDAO.registrarEmpleadoYUsuario(dto);
    }
}
