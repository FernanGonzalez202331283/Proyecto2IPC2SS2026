/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.Persona.Persona;
import modelo.Usuario;
import modelo.UsuarioOperativo;
import modelo.iteracion2.Empleado;

/**
 *
 * @author fernan
 */
public class UsuarioOperativoDAO {

    public String registrarEmpleadoYUsuario(UsuarioOperativo dto) {
        if (dto == null || dto.getPersona() == null || dto.getEmpleado() == null) {
            return "Los datos de la persona y del empleado son obligatorios.";
        }

        Connection connection = null;

        try {
            connection = Conexion.getInstance().getConnection();
            connection.setAutoCommit(false);

            Persona persona = dto.getPersona();
            Empleado empleado = dto.getEmpleado();

            String sqlPersona = """
                                INSERT INTO persona
                                (nombres, apellidos, dpi, telefono, correo, direccion, estado)
                                VALUES (?, ?, ?, ?, ?, ?, ?)
                                """;

            int idPersonaGenerado = 0;

            try (PreparedStatement psPersona = connection.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                psPersona.setString(1, persona.getNombres());
                psPersona.setString(2, persona.getApellidos());
                psPersona.setString(3, persona.getDpi());
                psPersona.setString(4, persona.getTelefono());
                psPersona.setString(5, persona.getCorreo());
                psPersona.setString(6, persona.getDireccion());
                psPersona.setBoolean(7, true);

                int filasP = psPersona.executeUpdate();

                if (filasP > 0) {
                    try (ResultSet rsKey = psPersona.getGeneratedKeys()) {
                        if (rsKey.next()) {
                            idPersonaGenerado = rsKey.getInt(1);
                            persona.setIdPersona(idPersonaGenerado);
                        }
                    }
                }
            }

            if (idPersonaGenerado <= 0) {
                connection.rollback();
                return "No se pudo registrar la información personal.";
            }

            String sqlEmpleado = """
                                 INSERT INTO empleado
                                 (id_persona, puesto, fecha_contratacion, salario, estado)
                                 VALUES (?, ?, ?, ?, ?)
                                 """;

            try (PreparedStatement psEmpleado = connection.prepareStatement(sqlEmpleado, Statement.RETURN_GENERATED_KEYS)) {
                psEmpleado.setInt(1, idPersonaGenerado);
                psEmpleado.setString(2, empleado.getPuesto());
                psEmpleado.setString(3, empleado.getFechaContratacion());
                psEmpleado.setDouble(4, empleado.getSalario());
                psEmpleado.setBoolean(5, true);

                int filasE = psEmpleado.executeUpdate();
                if (filasE <= 0) {
                    connection.rollback();
                    return "No se pudo registrar la ficha del empleado.";
                }

                try (ResultSet rsKeyE = psEmpleado.getGeneratedKeys()) {
                    if (rsKeyE.next()) {
                        empleado.setIdEmpleado(rsKeyE.getInt(1));
                    }
                }
            }

            if (dto.isCrearCuentaUsuario() && dto.getUsuario() != null) {
                Usuario usuario = dto.getUsuario();

                String passwordCodificada = Util.ContraseñaUtil.codificar(usuario.getContraseña());

                String sqlUsuario = """
                    INSERT INTO usuario
                    (username, contraseña, id_persona, rol, estado, debe_cambiar_clave)
                    VALUES (?, ?, ?, ?, ?, true)
                    """;

                String sqlHistorial = """
                                      INSERT INTO historial_contraseña (id_usuario, contraseña)
                                      VALUES (?, ?)
                                      """;

                int idUsuarioGenerado = 0;

                try (PreparedStatement psUsuario = connection.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    psUsuario.setString(1, usuario.getUsername());
                    psUsuario.setString(2, passwordCodificada);
                    psUsuario.setInt(3, idPersonaGenerado);
                    psUsuario.setString(4, usuario.getRol());
                    psUsuario.setBoolean(5, true);

                    int filasU = psUsuario.executeUpdate();

                    if (filasU > 0) {
                        try (ResultSet rsKeyU = psUsuario.getGeneratedKeys()) {
                            if (rsKeyU.next()) {
                                idUsuarioGenerado = rsKeyU.getInt(1);
                                usuario.setIdUsuario(idUsuarioGenerado);
                            }
                        }

                        if (idUsuarioGenerado > 0) {
                            try (PreparedStatement psHistorial = connection.prepareStatement(sqlHistorial)) {
                                psHistorial.setInt(1, idUsuarioGenerado);
                                psHistorial.setString(2, passwordCodificada);
                                psHistorial.executeUpdate();
                            }
                        }
                    } else {
                        connection.rollback();
                        return "No se pudo crear la cuenta de usuario.";
                    }
                }
            }

            connection.commit();
            return "Empleado registrado correctamente.";

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error al ejecutar rollback: " + ex.getMessage());
            }

            System.err.println("Error en transacción de UsuarioOperativo: " + e.getMessage());

            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("username")) {
                    return "El nombre de usuario ingresado ya existe en el sistema.";
                }
                if (e.getMessage().contains("dpi")) {
                    return "El DPI ingresado ya se encuentra registrado.";
                }
            }

            return "Error al registrar el empleado en la base de datos.";

        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    public List<UsuarioOperativo> obtenerOperativosPaginados(int limite, int offset) {
        String sql = """
                     SELECT u.id_usuario, u.username, u.rol, u.estado, u.fecha_creacion, u.debe_cambiar_clave,
                            p.id_persona, p.nombres, p.apellidos, p.dpi, p.telefono, p.correo, p.direccion
                     FROM usuario u
                     INNER JOIN persona p ON u.id_persona = p.id_persona
                     WHERE u.rol IN ('SECRETARIA', 'MAESTRO', 'BIBLIOTECARIO', 'PERSONAL')
                     ORDER BY u.id_usuario DESC
                     LIMIT ? OFFSET ?
                     """;

        List<UsuarioOperativo> lista = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsuarioOperativo dto = new UsuarioOperativo();

                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setUsername(rs.getString("username"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getBoolean("estado"));
                    u.setFechaCreacion(rs.getString("fecha_creacion"));
                    u.setDebeCambiarClave(rs.getBoolean("debe_cambiar_clave"));

                    Persona p = new Persona();
                    p.setIdPersona(rs.getInt("id_persona"));
                    p.setNombres(rs.getString("nombres"));
                    p.setApellidos(rs.getString("apellidos"));
                    p.setDpi(rs.getString("dpi"));
                    p.setTelefono(rs.getString("telefono"));
                    p.setCorreo(rs.getString("correo"));
                    p.setDireccion(rs.getString("direccion"));

                    dto.setUsuario(u);
                    dto.setPersona(p);

                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios operativos paginados: " + e.getMessage());
        }

        return lista;
    }

    public int contarOperativos() {
        String sql = """
                     SELECT COUNT(*) 
                     FROM usuario 
                     WHERE rol IN ('SECRETARIA', 'MAESTRO', 'BIBLIOTECARIO', 'PERSONAL')
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar usuarios operativos: " + e.getMessage());
        }
        return 0;
    }
}
