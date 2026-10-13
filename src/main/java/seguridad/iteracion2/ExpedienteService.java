/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad.iteracion2;

import Dao.iteracion2.ExpedienteDAO;
import modelo.iteracion2.Expediente;

/**
 *
 * @author fernan
 */
public class ExpedienteService {
    
    private ExpedienteDAO expedienteDAO;

    public ExpedienteService() {
        expedienteDAO = new ExpedienteDAO();
    }

    // Consultar expediente de un estudiante
    public Expediente consultarExpediente(int idEstudiante) {

        // Validar ID
        if (idEstudiante <= 0) {
            throw new IllegalArgumentException(
                "El ID del estudiante debe ser mayor que cero"
            );
        }

        // Consultar expediente
        Expediente expediente = expedienteDAO.buscarPorEstudiante(idEstudiante);

        // Verificar si existe
        if (expediente == null) {
            throw new IllegalArgumentException(
                "No se encontró el expediente del estudiante"
            );
        }

        return expediente;
    }
}
