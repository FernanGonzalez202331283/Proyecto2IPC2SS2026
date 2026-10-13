/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad.iteracion2;

import Dao.iteracion2.NominaAlumnoDAO;
import java.util.ArrayList;
import modelo.iteracion2.NominaAlumno;

/**
 *
 * @author fernan
 */
public class NominaAlumnoService {
    private final NominaAlumnoDAO nominaAlumnoDAO = new NominaAlumnoDAO();
    
    public ArrayList<NominaAlumno> obtenerNomina(int año, int idGrado, int idSeccion){
               if (año <= 0) {
            throw new IllegalArgumentException("Debe proporcionar un año lectivo válido");
        }

        if (idGrado < 0) {
            throw new IllegalArgumentException("El identificador del grado no es válido");
        }

        if (idSeccion < 0) {
            throw new IllegalArgumentException("El identificador de la sección no es válido");
        }

        if (idSeccion > 0 && idGrado <= 0) {
            throw new IllegalArgumentException("Debe proporcionar un grado para consultar una sección");
        }

        return nominaAlumnoDAO.obtenerNomina(año, idGrado, idSeccion);
    }
}
