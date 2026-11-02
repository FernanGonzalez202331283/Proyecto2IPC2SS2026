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

        if (pago == null) {
            return DATOS_INVALIDOS;
        }

        if (idUsuario <= 0) {
            return DATOS_INVALIDOS;
        }

        if (rol == null || (!"SECRETARIA".equals(rol) && !"ADMIN".equals(rol) && !"SUPER_ADMIN".equals(rol))) {
            return SIN_PERMISO;
        }

        if (pago.getIdObligacion() <= 0) {
            return DATOS_INVALIDOS;
        }

        pago.setIdUsuario(idUsuario);

        if (pago.getFechaPago() == null) {
            pago.setFechaPago(new Timestamp(System.currentTimeMillis()));
        }

        return pagoDAO.registrar(pago);
    }
}
