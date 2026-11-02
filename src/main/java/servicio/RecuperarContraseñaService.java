/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.HistorialContraseñaDAO;
import Dao.UsuarioDAO;
import Util.ContraseñaUtil;
import modelo.recuperarContraseña.RecuperarContraseñaRequest;
import modelo.Usuario;

/**
 *
 * @author fernan
 */
public class RecuperarContraseñaService {
    private final UsuarioDAO usuarioDAO;
    private final HistorialContraseñaDAO historialDAO;

    public RecuperarContraseñaService() {
        this.usuarioDAO = new UsuarioDAO();
        this.historialDAO = new HistorialContraseñaDAO();
    }

    public String recuperarContrasena(RecuperarContraseñaRequest request) {

        if (request == null) {
            return "La solicitud no puede ser nula.";
        }

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return "El username es obligatorio.";
        }

        if (request.getPasswordAnterior() == null || request.getPasswordAnterior().trim().isEmpty()) {
            return "La contraseña anterior es obligatoria.";
        }

        if (request.getNuevoPassword() == null || request.getNuevoPassword().trim().isEmpty()) {
            return "La nueva contraseña es obligatoria.";
        }

        String username = request.getUsername().trim();
        String contraseñaAnterior = request.getPasswordAnterior().trim();
        String nuevaContraseña = request.getNuevoPassword().trim();

        String contraseñaAnteriorCodificada = ContraseñaUtil.codificar(contraseñaAnterior);
        String nuevaContraseñaCodificada = ContraseñaUtil.codificar(nuevaContraseña);

        Usuario usuario = usuarioDAO.buscarPorUsername(username);

        if (usuario == null) {
            return "El usuario no existe.";
        }

        if (!usuario.isEstado()) {
            return "El usuario está inactivo.";
        }

        if (!usuario.getContraseña().equals(contraseñaAnteriorCodificada)) {
            return "La contraseña anterior no es válida.";
        }

        if (usuario.getContraseña().equals(nuevaContraseñaCodificada)) {
            return "La nueva contraseña no puede ser igual a la contraseña actual.";
        }

        if (historialDAO.existeContraseñaAnterior(usuario.getIdUsuario(), nuevaContraseñaCodificada)) {
            return "La nueva contraseña ya fue utilizada anteriormente.";
        }

        boolean guardarHistorial = historialDAO.insertar(usuario.getIdUsuario(), usuario.getContraseña());

        if (!guardarHistorial) {
            return "No se pudo guardar la contraseña anterior.";
        }

        boolean actualizar = usuarioDAO.confirmarPrimerCambioClave(usuario.getIdUsuario(), nuevaContraseñaCodificada);

        if (!actualizar) {
            return "No se pudo actualizar la contraseña.";
        }

        return "Contraseña actualizada correctamente.";
    }
}
