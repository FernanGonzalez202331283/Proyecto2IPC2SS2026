/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad.iteracion2;

import Dao.iteracion2.CursoAsignadoDAO;
import Dao.iteracion2.EmpleadoDAO;
import java.util.ArrayList;
import modelo.iteracion2.CursoAsignado;
import modelo.iteracion2.Empleado;

/**
 *
 * @author fernan
 */
public class CursoAsignadoService {
    
    private final CursoAsignadoDAO cursoAsignadoDAO;
    private final EmpleadoDAO empleadoDAO;

    public CursoAsignadoService() {
        this.cursoAsignadoDAO = new CursoAsignadoDAO();
        this.empleadoDAO = new EmpleadoDAO();
    }

    // Consultar todos los cursos asignados
    public ArrayList<CursoAsignado> obtenerTodos() {

        ArrayList<CursoAsignado> lista =
                cursoAsignadoDAO.obtenerTodos();

        if (lista == null || lista.isEmpty()) {
            return new ArrayList<>();
        }

        return lista;
    }

    // Consultar cursos asignados a un maestro específico
    public ArrayList<CursoAsignado> obtenerPorEmpleado(int idEmpleado) {

        if (idEmpleado <= 0) {
            return new ArrayList<>();
        }

        ArrayList<CursoAsignado> lista =
                cursoAsignadoDAO.obtenerPorEmpleado(idEmpleado);

        if (lista == null || lista.isEmpty()) {
            return new ArrayList<>();
        }

        return lista;
    }

    // Buscar empleado relacionado con una persona
    public Empleado obtenerEmpleadoPorPersona(int idPersona) {

        if (idPersona <= 0) {
            return null;
        }

        return empleadoDAO.buscarPorIdPersona(idPersona);
    }
}
