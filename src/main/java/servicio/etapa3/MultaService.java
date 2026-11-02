/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.etapa3;

import Dao.iteracion3.MultaDAO;
import java.util.ArrayList;
import modelo.iteracion3.Multa;
import modelo.RespuestaPaginada;
/**
 *
 * @author fernan
 */
public class MultaService {
private final MultaDAO multaDAO;

    public MultaService() {
        this.multaDAO = new MultaDAO();
    }

    public RespuestaPaginada<Multa> obtenerTodosPaginado(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        ArrayList<Multa> lista = multaDAO.obtenerTodosPaginado(offset, limite);
        int totalRegistros = multaDAO.contarTotal();

        return new RespuestaPaginada<>(lista, pagina, limite, totalRegistros);
    }

    public ArrayList<Multa> obtenerTodos() {
        return multaDAO.obtenerTodos();
    }

    public Multa buscarPorId(int idMulta) {
        if (idMulta <= 0) {
            return null;
        }
        return multaDAO.buscarPorId(idMulta);
    }

    public String actualizarEstado(int idMulta, String estado) {
        if (idMulta <= 0) {
            return "El ID de la multa debe ser válido.";
        }

        if (estado == null || estado.trim().isEmpty()) {
            return "Debe proporcionar el estado de la multa.";
        }

        estado = estado.trim().toUpperCase();

        if (!"PENDIENTE".equals(estado) && !"PAGADA".equals(estado)) {
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
