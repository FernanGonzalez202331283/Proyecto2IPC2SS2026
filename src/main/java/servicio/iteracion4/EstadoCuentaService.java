/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.EstadoCuentaDAO;
import modelo.iteracion4.EstadoCuenta;

/**
 *
 * @author fernan
 */
public class EstadoCuentaService {
    
    private final EstadoCuentaDAO estadoCuentaDAO;

    public EstadoCuentaService() {
        this.estadoCuentaDAO = new EstadoCuentaDAO();
    }

    public EstadoCuenta consultarEstadoCuenta(int idEstudiante, int idAñoLectivo) {

        if (idEstudiante <= 0 || idAñoLectivo <= 0) {
            return null;
        }

        return estadoCuentaDAO.consultarEstadoCuenta(
                idEstudiante,
                idAñoLectivo
        );
    }
}
