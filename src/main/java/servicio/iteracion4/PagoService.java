/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.PagoDAO;
import java.sql.Timestamp;
import modelo.iteracion4.Pago;

/**
 *
 * @author fernan
 */
public class PagoService {
    
    private final PagoDAO pagoDAO = new PagoDAO();

    public static final int DATOS_INVALIDOS = -4;
    public static final int SIN_PERMISO = -5;

    public int registrar(Pago pago, int idUsuario, String rol) {

        // Validar que exista un pago
        if (pago == null) {
            return DATOS_INVALIDOS;
        }

        // Validar el usuario
        if (idUsuario <= 0) {
            return DATOS_INVALIDOS;
        }

        // Solamente SECRETARIA puede registrar pagos
        if (rol == null || !rol.equals("SECRETARIA")) {
            return SIN_PERMISO;
        }

        // Validar la obligación
        if (pago.getIdObligacion() <= 0) {
            return DATOS_INVALIDOS;
        }

        // El usuario se obtiene de la sesión
        pago.setIdUsuario(idUsuario);

        // Si no se proporciona fecha, se utiliza la actual
        if (pago.getFechaPago() == null) {
            pago.setFechaPago(new Timestamp(System.currentTimeMillis()));
        }

        // Registrar el pago
        return pagoDAO.registrar(pago);
    }
}
