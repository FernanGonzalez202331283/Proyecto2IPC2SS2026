package servicio.iteracion4;

import Dao.iteracion4.ObligacionPagoDAO;
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

        if (!obligacionDAO.estudianteActivo(obligacion.getIdEstudiante())) {
            return ESTUDIANTE_NO_ENCONTRADO;
        }

        if (!obligacionDAO.añoLectivoActivo(obligacion.getIdAñoLectivo())) {
            return AÑO_NO_ENCONTRADO;
        }

        if (obligacionDAO.existeObligacion(
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
    
    public String registrarObligacionPorSeccion(int idSeccion, ObligacionPago datosObligacion) {
        
        if (datosObligacion == null || datosObligacion.getMonto() <= 0 || datosObligacion.getFechaVencimiento() == null) {
            return "Datos de la actividad inválidos.";
        }
        
        if (!"ACTIVIDAD".equals(datosObligacion.getConcepto())) {
            return "El concepto debe ser 'ACTIVIDAD'.";
        }

        java.util.ArrayList<Integer> idEstudiantes = obligacionDAO.obtenerEstudiantesPorSeccion(idSeccion);
        
        if (idEstudiantes.isEmpty()) {
            return "No hay estudiantes activos en esta sección para asignar el cobro.";
        }
        
        int asignados = 0;
        int duplicados = 0;
        
        for (Integer idEstudiante : idEstudiantes) {
            
            if (obligacionDAO.existeObligacion(idEstudiante, datosObligacion.getIdAñoLectivo(), datosObligacion.getConcepto())) {
                duplicados++;
                continue; 
            }

            ObligacionPago nuevaDeuda = new ObligacionPago();
            nuevaDeuda.setIdEstudiante(idEstudiante);
            nuevaDeuda.setIdAñoLectivo(datosObligacion.getIdAñoLectivo());
            nuevaDeuda.setConcepto(datosObligacion.getConcepto());
            nuevaDeuda.setDescripcion(datosObligacion.getDescripcion());
            nuevaDeuda.setMonto(datosObligacion.getMonto());
            nuevaDeuda.setFechaVencimiento(datosObligacion.getFechaVencimiento());
            
            if (obligacionDAO.registrar(nuevaDeuda) > 0) {
                asignados++;
            }
        }
        
        return "Actividad asignada a " + asignados + " estudiantes. (" + duplicados + " ya la tenían asignada).";
    }
}