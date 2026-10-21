/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.HistorialPagoDAO;
import java.util.ArrayList;
import modelo.iteracion4.HistorialPago;

/**
 *
 * @author fernan
 */
public class HistorialPagoService {
    
    private final HistorialPagoDAO historialPagoDAO;

    public HistorialPagoService() {
        this.historialPagoDAO = new HistorialPagoDAO();
    }

    public ArrayList<HistorialPago> obtenerHistorial(int idEstudiante) {

        // Validar el identificador del estudiante
        if (idEstudiante <= 0) {
            return null;
        }

        return historialPagoDAO.obtenerHistorial(idEstudiante);
    }
}
