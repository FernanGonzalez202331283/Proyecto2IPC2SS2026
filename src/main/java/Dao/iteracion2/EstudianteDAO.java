/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion2;

import Conexion.Conexion;
import Dao.UsuarioDAO;
import Util.ContraseñaUtil;
import modelo.iteracion2.Estudiante;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.Persona.Persona;
import modelo.Usuario;
import modelo.iteracion2.EstudianteEncargado;

/**
 *
 * @author fernan
 */
public class EstudianteDAO {
private final EstudianteEncargadoDAO estudianteEncargadoDAO = new EstudianteEncargadoDAO();
    private final EncargadoDAO encargadoDAO = new EncargadoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public boolean registrarEstudiante(Estudiante estudiante) {
        String sqlPersona = """
                            INSERT INTO persona
                            (nombres, apellidos, dpi, telefono, correo, direccion)
                            VALUES (?, ?, ?, ?, ?, ?)
                            """;

        String sqlEstudiante = """
                               INSERT INTO estudiante
                               (id_persona, codigo_estudiante, informacion_medica, estado)
                               VALUES (?, ?, ?, ?)
                               """;

        try (Connection connection = Conexion.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                int idPersona;
                try (PreparedStatement psPersona = connection.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                    psPersona.setString(1, estudiante.getPersona().getNombres());
                    psPersona.setString(2, estudiante.getPersona().getApellidos());
                    psPersona.setString(3, estudiante.getPersona().getDpi());
                    psPersona.setString(4, estudiante.getPersona().getTelefono());
                    psPersona.setString(5, estudiante.getPersona().getCorreo());
                    psPersona.setString(6, estudiante.getPersona().getDireccion());

                    psPersona.executeUpdate();

                    try (ResultSet rs = psPersona.getGeneratedKeys()) {
                        if (rs.next()) {
                            idPersona = rs.getInt(1);
                        } else {
                            throw new SQLException("No se pudo obtener el ID de la persona.");
                        }
                    }
                }

                int idEstudiante;
                try (PreparedStatement psEstudiante = connection.prepareStatement(sqlEstudiante, Statement.RETURN_GENERATED_KEYS)) {
                    psEstudiante.setInt(1, idPersona);
                    psEstudiante.setString(2, estudiante.getCodigoEstudiante());
                    psEstudiante.setString(3, estudiante.getInformacionMedica());
                    if (estudiante.getEstado() == null || estudiante.getEstado().trim().isEmpty()) {
                        psEstudiante.setString(4, "ACTIVO");
                    } else {
                        psEstudiante.setString(4, estudiante.getEstado());
                    }

                    psEstudiante.executeUpdate();

                    try (ResultSet rs = psEstudiante.getGeneratedKeys()) {
                        if (rs.next()) {
                            idEstudiante = rs.getInt(1);
                            estudiante.setIdEstudiante(idEstudiante);
                        } else {
                            throw new SQLException("No se pudo obtener el ID del estudiante.");
                        }
                    }
                }

                if (estudiante.getEncargados() != null && !estudiante.getEncargados().isEmpty()) {
                    for (EstudianteEncargado rel : estudiante.getEncargados()) {
                        int idEncargado = rel.getIdEncargado();

                        if (idEncargado <= 0 && rel.getEncargado() != null) {
                            rel.getEncargado().setEstado(true);
                            idEncargado = encargadoDAO.registrar(connection, rel.getEncargado());

                            if (idEncargado <= 0) {
                                throw new SQLException("No se pudo registrar la información del encargado.");
                            }
                            rel.setIdEncargado(idEncargado);
                        }

                        rel.setIdEstudiante(idEstudiante);
                        boolean okRelacion = estudianteEncargadoDAO.registrarRelacion(connection, rel);
                        if (!okRelacion) {
                            throw new SQLException("No se pudo registrar la relación estudiante-encargado.");
                        }
                    }
                }

                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setUsername(estudiante.getCodigoEstudiante());

                String passCodificada = ContraseñaUtil.codificar("123456");
                nuevoUsuario.setContraseña(passCodificada);

                nuevoUsuario.setIdPersona(idPersona);
                nuevoUsuario.setRol("ESTUDIANTE");
                nuevoUsuario.setEstado(true);
                nuevoUsuario.setDebeCambiarClave(true);

                boolean usuarioCreado = usuarioDAO.insertar(connection, nuevoUsuario);
                if (!usuarioCreado) {
                    throw new SQLException("No se pudo crear la cuenta de usuario para el estudiante.");
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Error en transacción al registrar estudiante: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión al registrar estudiante: " + e.getMessage());
            return false;
        }
    }

    public Estudiante buscarPorId(int idEstudiante) {
        String sql = """
                     SELECT 
                         e.id_estudiante, e.id_persona, e.codigo_estudiante, e.informacion_medica,
                         e.estado AS estado_estudiante, p.nombres, p.apellidos, p.dpi, p.telefono,
                         p.correo, p.direccion, p.estado AS estado_persona
                     FROM estudiante e
                     INNER JOIN persona p ON e.id_persona = p.id_persona
                     WHERE e.id_estudiante = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Estudiante estudiante = mapearEstudiante(rs);
                    estudiante.setEncargados(estudianteEncargadoDAO.obtenerPorEstudiante(idEstudiante));
                    return estudiante;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar estudiante: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Estudiante> obtenerTodos() {
        ArrayList<Estudiante> estudiantes = new ArrayList<>();
        String sql = """
                     SELECT 
                         e.id_estudiante, e.id_persona, e.codigo_estudiante, e.informacion_medica,
                         e.estado AS estado_estudiante, p.nombres, p.apellidos, p.dpi, p.telefono,
                         p.correo, p.direccion, p.estado AS estado_persona
                     FROM estudiante e
                     INNER JOIN persona p ON e.id_persona = p.id_persona
                     ORDER BY e.id_estudiante DESC
                     """;
        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estudiante estudiante = mapearEstudiante(rs);
                estudiante.setEncargados(estudianteEncargadoDAO.obtenerPorEstudiante(estudiante.getIdEstudiante()));
                estudiantes.add(estudiante);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener estudiantes: " + e.getMessage());
        }
        return estudiantes;
    }

    public List<Estudiante> obtenerPaginados(int limite, int offset) {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = """
                     SELECT 
                         e.id_estudiante, e.id_persona, e.codigo_estudiante, e.informacion_medica,
                         e.estado AS estado_estudiante, p.nombres, p.apellidos, p.dpi, p.telefono,
                         p.correo, p.direccion, p.estado AS estado_persona
                     FROM estudiante e
                     INNER JOIN persona p ON e.id_persona = p.id_persona
                     ORDER BY e.id_estudiante DESC
                     LIMIT ? OFFSET ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Estudiante estudiante = mapearEstudiante(rs);
                    estudiante.setEncargados(estudianteEncargadoDAO.obtenerPorEstudiante(estudiante.getIdEstudiante()));
                    estudiantes.add(estudiante);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener estudiantes paginados: " + e.getMessage());
        }
        return estudiantes;
    }

    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM estudiante";
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar estudiantes: " + e.getMessage());
        }
        return 0;
    }

    public boolean actualizar(Estudiante estudiante) {
        String sqlPersona = """
                            UPDATE persona
                            SET nombres = ?, apellidos = ?, dpi = ?, telefono = ?, correo = ?, direccion = ?
                            WHERE id_persona = ?
                            """;

        String sqlEstudiante = """
                               UPDATE estudiante
                               SET codigo_estudiante = ?, informacion_medica = ?, estado = ?
                               WHERE id_estudiante = ?
                               """;

        try (Connection connection = Conexion.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement psPersona = connection.prepareStatement(sqlPersona)) {
                    psPersona.setString(1, estudiante.getPersona().getNombres());
                    psPersona.setString(2, estudiante.getPersona().getApellidos());
                    psPersona.setString(3, estudiante.getPersona().getDpi());
                    psPersona.setString(4, estudiante.getPersona().getTelefono());
                    psPersona.setString(5, estudiante.getPersona().getCorreo());
                    psPersona.setString(6, estudiante.getPersona().getDireccion());
                    psPersona.setInt(7, estudiante.getIdPersona());

                    psPersona.executeUpdate();
                }

                try (PreparedStatement psEstudiante = connection.prepareStatement(sqlEstudiante)) {
                    psEstudiante.setString(1, estudiante.getCodigoEstudiante());
                    psEstudiante.setString(2, estudiante.getInformacionMedica());
                    psEstudiante.setString(3, estudiante.getEstado());
                    psEstudiante.setInt(4, estudiante.getIdEstudiante());

                    psEstudiante.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Error al actualizar estudiante: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión al actualizar estudiante: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int idEstudiante, String estado) {
        String sql = "UPDATE estudiante SET estado = ? WHERE id_estudiante = ?";
        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idEstudiante);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar el estado del estudiante: " + e.getMessage());
            return false;
        }
    }

    public boolean existeCodigo(String codigoEstudiante) {
        String sql = "SELECT COUNT(*) FROM estudiante WHERE codigo_estudiante = ?";
        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codigoEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar codigo: " + e.getMessage());
        }
        return false;
    }

    public boolean existeCodigo(String codigoEstudiante, int idEstudiante) {
        String sql = "SELECT COUNT(*) FROM estudiante WHERE codigo_estudiante = ? AND id_estudiante <> ?";
        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codigoEstudiante);
            ps.setInt(2, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar código: " + e.getMessage());
        }
        return false;
    }

    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        Persona persona = new Persona();
        persona.setIdPersona(rs.getInt("id_persona"));
        persona.setNombres(rs.getString("nombres"));
        persona.setApellidos(rs.getString("apellidos"));
        persona.setDpi(rs.getString("dpi"));
        persona.setTelefono(rs.getString("telefono"));
        persona.setCorreo(rs.getString("correo"));
        persona.setDireccion(rs.getString("direccion"));
        persona.setEstado(rs.getBoolean("estado_persona"));

        Estudiante estudiante = new Estudiante();
        estudiante.setIdEstudiante(rs.getInt("id_estudiante"));
        estudiante.setIdPersona(rs.getInt("id_persona"));
        estudiante.setCodigoEstudiante(rs.getString("codigo_estudiante"));
        estudiante.setInformacionMedica(rs.getString("informacion_medica"));
        estudiante.setEstado(rs.getString("estado_estudiante"));
        estudiante.setPersona(persona);

        return estudiante;
    }
}
