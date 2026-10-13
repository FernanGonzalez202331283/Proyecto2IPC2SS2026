/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion2;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion2.Expediente;
import modelo.iteracion2.Inscripcion;

/**
 *
 * @author fernan
 */
public class ExpedienteDAO {
    
    private EstudianteEncargadoDAO estudianteEncargadoDAO;

    public ExpedienteDAO() {
        estudianteEncargadoDAO = new EstudianteEncargadoDAO();
    }

    // Buscar expediente completo de un estudiante
    public Expediente buscarPorEstudiante(int idEstudiante) {

        Expediente expediente = null;

        String sql = """
            SELECT
                e.id_estudiante,
                e.codigo_estudiante,
                e.informacion_medica,
                e.estado,
                p.nombres,
                p.apellidos,
                p.dpi,
                p.telefono,
                p.correo,
                p.direccion
            FROM estudiante e
            INNER JOIN persona p
                ON e.id_persona = p.id_persona
            WHERE e.id_estudiante = ?
            """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    expediente = new Expediente();

                    expediente.setIdEstudiante(
                        rs.getInt("id_estudiante")
                    );

                    expediente.setCodigoEstudiante(
                        rs.getString("codigo_estudiante")
                    );

                    expediente.setInformacionMedica(
                        rs.getString("informacion_medica")
                    );

                    expediente.setEstado(
                        rs.getString("estado")
                    );

                    expediente.setNombres(
                        rs.getString("nombres")
                    );

                    expediente.setApellidos(
                        rs.getString("apellidos")
                    );

                    expediente.setDpi(
                        rs.getString("dpi")
                    );

                    expediente.setTelefono(
                        rs.getString("telefono")
                    );

                    expediente.setCorreo(
                        rs.getString("correo")
                    );

                    expediente.setDireccion(
                        rs.getString("direccion")
                    );

                } else {
                    return null;
                }
            }

            // Obtener los encargados del estudiante
            expediente.setEncargados(
                estudianteEncargadoDAO.obtenerPorEstudiante(idEstudiante)
            );

            // Obtener historial académico
            expediente.setHistorialAcademico(
                obtenerHistorialAcademico(idEstudiante)
            );

        } catch (SQLException e) {
            System.out.println("Error al consultar expediente: " + e.getMessage());
            return null;
        }

        return expediente;
    }

    // Consultar historial académico del estudiante
    private ArrayList<Inscripcion> obtenerHistorialAcademico(int idEstudiante) {

        ArrayList<Inscripcion> historial = new ArrayList<>();

        String sql = """
            SELECT
                id_inscripcion,
                id_estudiante,
                id_seccion,
                fecha_inscripcion,
                estado
            FROM inscripcion
            WHERE id_estudiante = ?
            ORDER BY fecha_inscripcion DESC
            """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Inscripcion inscripcion = new Inscripcion();

                    inscripcion.setIdInscripcion(
                        rs.getInt("id_inscripcion")
                    );

                    inscripcion.setIdEstudiante(
                        rs.getInt("id_estudiante")
                    );

                    inscripcion.setIdSeccion(
                        rs.getInt("id_seccion")
                    );

                    inscripcion.setFechaInscripcion(
                        rs.getDate("fecha_inscripcion").toString()
                    );

                    inscripcion.setEstado(
                        rs.getString("estado")
                    );

                    historial.add(inscripcion);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar historial académico: " + e.getMessage());
        }

        return historial;
    }
}
