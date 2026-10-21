/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.etapa3;

import Dao.iteracion3.MultaDAO;
import java.util.ArrayList;
import modelo.iteracion3.Multa;

/**
 *
 * @author fernan
 */
public class MultaService {
    
    private final MultaDAO multaDAO;

    public MultaService() {
        this.multaDAO = new MultaDAO();
    }

    // Obtener todas las multas
    public ArrayList<Multa> obtenerTodos() {

        return multaDAO.obtenerTodos();
    }

    // Buscar una multa por ID
    public Multa buscarPorId(int idMulta) {

        if (idMulta <= 0) {
            return null;
        }

        return multaDAO.buscarPorId(idMulta);
    }

    // Actualizar el estado de una multa
    public String actualizarEstado(int idMulta, String estado) {

        if (idMulta <= 0) {
            return "El ID de la multa debe ser válido.";
        }

        if (estado == null || estado.trim().isEmpty()) {
            return "Debe proporcionar el estado de la multa.";
        }

        estado = estado.trim().toUpperCase();

        if (!estado.equals("PENDIENTE") && !estado.equals("PAGADA")) {
            return "El estado debe ser PENDIENTE o PAGADA.";
        }

        Multa multa = multaDAO.buscarPorId(idMulta);

        if (multa == null) {
            return "La multa indicada no existe.";
        }

        if (multa.getEstado().equals(estado)) {
            return "La multa ya tiene ese estado.";
        }

        boolean actualizado = multaDAO.actualizarEstado(idMulta, estado);

        if (actualizado) {
            return "Estado de la multa actualizado correctamente.";
        }

        return "No fue posible actualizar el estado de la multa.";
    }
}
