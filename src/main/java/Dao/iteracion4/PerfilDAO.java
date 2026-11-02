/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion4;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.iteracion4.Perfil;

/**
 *
 * @author fernan
 */
public class PerfilDAO {

    public Perfil obtenerPerfil(int idUsuario) {

        String sql = """
            SELECT
                u.id_usuario,
                u.username,
                u.rol,

                p.id_persona,
                p.nombres,
                p.apellidos,
                p.dpi,
                p.telefono,
                p.correo,
                p.direccion,
                p.estado,

                e.codigo_estudiante,
                emp.puesto

            FROM usuario u

            INNER JOIN persona p
                ON u.id_persona = p.id_persona

            LEFT JOIN estudiante e
                ON p.id_persona = e.id_persona

            LEFT JOIN empleado emp
                ON p.id_persona = emp.id_persona

            WHERE u.id_usuario = ?
        """;

        try (Connection conexion = Conexion.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Perfil perfil = new Perfil();

                    perfil.setIdUsuario(rs.getInt("id_usuario"));
                    perfil.setNombreUsuario(rs.getString("username"));
                    perfil.setRol(rs.getString("rol"));

                    perfil.setIdPersona(rs.getInt("id_persona"));
                    perfil.setNombres(rs.getString("nombres"));
                    perfil.setApellidos(rs.getString("apellidos"));
                    perfil.setDpi(rs.getString("dpi"));
                    perfil.setTelefono(rs.getString("telefono"));
                    perfil.setCorreo(rs.getString("correo"));
                    perfil.setDireccion(rs.getString("direccion"));

                    perfil.setEstadoPersona(rs.getBoolean("estado"));

                    perfil.setCodigoEstudiante(rs.getString("codigo_estudiante"));
                    perfil.setPuesto(rs.getString("puesto"));

                    // Identificar el tipo de perfil según el Rol de usuario
                    String rol = perfil.getRol() != null ? perfil.getRol().toUpperCase() : "";

                    if ("ESTUDIANTE".equals(rol)) {
                        perfil.setTipoPerfil("ESTUDIANTE");
                    } else if ("MAESTRO".equals(rol) || "SECRETARIA".equals(rol) || "BIBLIOTECARIO".equals(rol) || "PERSONAL".equals(rol)) {
                        perfil.setTipoPerfil("EMPLEADO");
                    } else {
                        perfil.setTipoPerfil("ADMINISTRATIVO");
                    }

                    return perfil;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar perfil: " + e.getMessage());
        }

        return null;
    }
}
