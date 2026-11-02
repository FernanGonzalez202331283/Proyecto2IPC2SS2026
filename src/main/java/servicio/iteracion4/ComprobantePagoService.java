/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.ComprobantePagoDAO;

/**
 *
 * @author fernan
 */
public class ComprobantePagoService {
    
    private final ComprobantePagoDAO comprobanteDAO = new ComprobantePagoDAO();

    public static final int DATOS_INVALIDOS = -5;

    public int registrar(ComprobantePago comprobante) {
        if (comprobante == null) {
            return DATOS_INVALIDOS;
        }

        if (comprobante.getIdPago() <= 0) {
            return DATOS_INVALIDOS;
        }

        return comprobanteDAO.registrar(comprobante);
    }

    public java.util.ArrayList<ComprobantePago> obtenerTodos() {

        return comprobanteDAO.obtenerTodos();
    }

    public ComprobantePago buscarPorId(int idComprobante) {

        if (idComprobante <= 0) {
            return null;
        }

        return comprobanteDAO.buscarPorId(idComprobante);
    }

    public ComprobantePago buscarPorPago(int idPago) {

        if (idPago <= 0) {
            return null;
        }

        return comprobanteDAO.buscarPorPago(idPago);
    }
}
