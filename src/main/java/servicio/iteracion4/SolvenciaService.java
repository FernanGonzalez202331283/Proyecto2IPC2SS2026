/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.SolvenciaDAO;
import modelo.iteracion4.Solvencia;

/**
 *
 * @author fernan
 */
public class SolvenciaService {
    
    private final SolvenciaDAO solvenciaDAO;

    public SolvenciaService() {
        this.solvenciaDAO = new SolvenciaDAO();
    }

    public Solvencia generarSolvencia(int idEstudiante, int idAñoLectivo) {

        // Validar identificadores.
        if (idEstudiante <= 0 || idAñoLectivo <= 0) {
            return null;
        }

        // Consultar solvencia.
        return solvenciaDAO.generarSolvencia(idEstudiante, idAñoLectivo);
    }
}
