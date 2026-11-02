/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.PerfilDAO;
import modelo.iteracion4.Perfil;

/**
 *
 * @author fernan
 */
public class PerfilService {
    
    private final PerfilDAO perfilDAO;

    public PerfilService() {
        this.perfilDAO = new PerfilDAO();
    }

    public Perfil obtenerPerfil(int idUsuario) {

        if (idUsuario <= 0) {
            return null;
        }

        return perfilDAO.obtenerPerfil(idUsuario);
    }
}
