/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Conexion.Conexion;
import Dao.iteracion4.ObligacionPagoDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import modelo.iteracion4.ObligacionPago;

/**
 *
 * @author fernan
 */
public class ObligacionPagoService {
    
    private final ObligacionPagoDAO obligacionDAO = new ObligacionPagoDAO();

    public static final int DATOS_INVALIDOS = -2;
    public static final int ESTUDIANTE_NO_ENCONTRADO = -3;
    public static final int AÑO_NO_ENCONTRADO = -4;
    public static final int OBLIGACION_DUPLICADA = -5;
    public static final int ERROR = -1;

    // Registrar obligación
    public int registrar(ObligacionPago obligacion) {

        if (obligacion == null) {
            return DATOS_INVALIDOS;
        }

        if (obligacion.getIdEstudiante() <= 0
                || obligacion.getIdAñoLectivo() <= 0
                || obligacion.getConcepto() == null
                || obligacion.getMonto() == null
                || obligacion.getMonto() <= 0
                || obligacion.getFechaVencimiento() == null) {

            return DATOS_INVALIDOS;
        }

        String concepto = obligacion.getConcepto();

        if (!concepto.equals("INSCRIPCION")
                && !concepto.equals("MES_DE_COLEGIATURA")
                && !concepto.equals("ACTIVIDAD")) {

            return DATOS_INVALIDOS;
        }

        // Verificar que exista el estudiante y esté activo
        if (!estudianteActivo(obligacion.getIdEstudiante())) {
            return ESTUDIANTE_NO_ENCONTRADO;
        }

        if (!añoLectivoActivo(obligacion.getIdAñoLectivo())) {
            return AÑO_NO_ENCONTRADO;
        }

        // Evitar obligaciones duplicadas
        if (existeObligacion(
                obligacion.getIdEstudiante(),
                obligacion.getIdAñoLectivo(),
                obligacion.getConcepto())) {

            return OBLIGACION_DUPLICADA;
        }

        if (obligacion.getDescripcion() != null
                && obligacion.getDescripcion().length() > 255) {

            return DATOS_INVALIDOS;
        }

        return obligacionDAO.registrar(obligacion);
    }

    private boolean estudianteActivo(int idEstudiante) {

        String sql = """
                SELECT id_estudiante
                FROM estudiante
                WHERE id_estudiante = ?
                AND estado = 'ACTIVO'
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            System.out.println("Error al verificar estudiante: " + e.getMessage());
            return false;
        }
    }

    private boolean añoLectivoActivo(int idAñoLectivo) {

        String sql = """
                SELECT id_año_lectivo
                FROM año_lectivo
                WHERE id_año_lectivo = ?
                AND estado = 1
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idAñoLectivo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            System.out.println("Error al verificar año lectivo: " + e.getMessage());
            return false;
        }
    }

    private boolean existeObligacion(int idEstudiante, int idAñoLectivo,
            String concepto) {

        String sql = """
                SELECT id_obligacion
                FROM obligacion_pago
                WHERE id_estudiante = ?
                AND id_año_lectivo = ?
                AND concepto = ?
                AND estado != 'ANULADA'
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idAñoLectivo);
            ps.setString(3, concepto);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            System.out.println("Error al verificar obligación: " + e.getMessage());
            return true;
        }
                
    }
}
