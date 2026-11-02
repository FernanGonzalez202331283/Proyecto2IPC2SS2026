package servicio.etapa3;

import Dao.iteracion3.LibroDAO;
import Dao.iteracion3.PrestamoDAO;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import modelo.RespuestaPaginada;
import modelo.iteracion3.Libro;
import modelo.iteracion3.Prestamo;

/**
 *
 * @author fernan
 */
public class PrestamoService {
    private final PrestamoDAO prestamoDAO;
    private final LibroDAO libroDAO;

    public PrestamoService() {
        this.prestamoDAO = new PrestamoDAO();
        this.libroDAO = new LibroDAO();
    }

    public String registrar(Prestamo prestamo) {

        if (prestamo == null) {
            return "Debe proporcionar los datos del préstamo.";
        }

        if (prestamo.getIdLibro() <= 0) {
            return "Debe proporcionar un ID de libro válido.";
        }

        if (prestamo.getIdPersona() <= 0) {
            return "Debe proporcionar un ID de persona válido.";
        }

        Libro libro = libroDAO.buscarPorId(prestamo.getIdLibro());

        if (libro == null) {
            return "El libro indicado no existe.";
        }

        if (!libro.isEstado()) {
            return "El libro se encuentra inactivo.";
        }

        if (libro.getCantidadDisponible() <= 0) {
            return "No hay ejemplares disponibles de este libro.";
        }

        if (!prestamoDAO.personaActiva(prestamo.getIdPersona())) {
            return "La persona no existe, se encuentra inactiva, o no tiene el rol de ESTUDIANTE o MAESTRO.";
        }

        LocalDate fechaPrestamo;
        LocalDate fechaDevolucion;

        try {
            fechaPrestamo = LocalDate.parse(prestamo.getFechaPrestamo());
            fechaDevolucion = LocalDate.parse(prestamo.getFechaDevolucionEsperada());
        } catch (DateTimeParseException | NullPointerException e) {
            return "Las fechas deben tener el formato AAAA-MM-DD.";
        }

        if (!fechaDevolucion.isAfter(fechaPrestamo)) {
            return "La fecha de devolución debe ser posterior a la fecha del préstamo.";
        }

        prestamo.setEstado("PRESTADO");

        if (prestamoDAO.registrar(prestamo)) {
            return "Préstamo registrado correctamente.";
        }

        return "No se pudo registrar el préstamo.";
    }

    public RespuestaPaginada<Prestamo> obtenerTodosPaginado(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        ArrayList<Prestamo> lista = prestamoDAO.obtenerTodosPaginado(offset, limite);
        int totalRegistros = prestamoDAO.contarTotal();

        return new RespuestaPaginada<>(lista, pagina, limite, totalRegistros);
    }

    public Prestamo buscarPorId(int idPrestamo) {
        if (idPrestamo <= 0) {
            return null;
        }
        return prestamoDAO.buscarPorId(idPrestamo);
    }
    
    public String registrarDevolucion(int idPrestamo) {
        if (idPrestamo <= 0) {
            return "Debe proporcionar un ID de préstamo válido.";
        }

        Prestamo prestamo = prestamoDAO.buscarPorId(idPrestamo);

        if (prestamo == null) {
            return "El préstamo indicado no existe.";
        }

        String estadoActual = prestamo.getEstado();
        if (!"PRESTADO".equals(estadoActual) && !"VENCIDO".equals(estadoActual)) {
            return "Este préstamo ya fue devuelto o no se encuentra activo.";
        }

        if (prestamoDAO.registrarDevolucion(idPrestamo)) {
            return "Devolución registrada correctamente.";
        }

        return "No se pudo registrar la devolución.";
    }

    public ArrayList<Prestamo> obtenerPorPersona(int idPersona) {
        if (idPersona <= 0) {
            return new ArrayList<>();
        }
        return prestamoDAO.obtenerPorPersona(idPersona);
    }
}