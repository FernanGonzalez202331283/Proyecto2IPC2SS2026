/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad.iteracion2;

import Dao.iteracion2.NominaAlumnoDAO;
import java.util.ArrayList;
import modelo.RespuestaPaginada;
import modelo.iteracion2.NominaAlumno;

/**
 *
 * @author fernan
 */
public class NominaAlumnoService {
   private final NominaAlumnoDAO nominaAlumnoDAO = new NominaAlumnoDAO();
    
    public RespuestaPaginada<NominaAlumno> obtenerNominaPaginada(int año, int idGrado, int idSeccion, int pagina, int limite) {
        if (año <= 0) {
            throw new IllegalArgumentException("Debe proporcionar un año lectivo válido");
        }
        if (idGrado < 0) throw new IllegalArgumentException("El identificador del grado no es válido");
        if (idSeccion < 0) throw new IllegalArgumentException("El identificador de la sección no es válido");
        if (idSeccion > 0 && idGrado <= 0) {
            throw new IllegalArgumentException("Debe proporcionar un grado para consultar una sección");
        }
        
        if (pagina <= 0) pagina = 1;
        if (limite <= 0) limite = 10;

        int offset = (pagina - 1) * limite;

        ArrayList<NominaAlumno> datos = nominaAlumnoDAO.obtenerNominaPaginada(año, idGrado, idSeccion, limite, offset);
        
        int totalRegistros = nominaAlumnoDAO.contarNomina(año, idGrado, idSeccion);

        return new RespuestaPaginada<>(datos, pagina, limite, totalRegistros);
    }
}
