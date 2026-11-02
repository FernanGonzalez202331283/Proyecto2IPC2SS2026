package servicio.etapa3;

import Dao.iteracion3.LibroDAO;
import java.util.ArrayList;
import modelo.RespuestaPaginada;
import modelo.iteracion3.Libro;

/**
 *
 * @author fernan
 */
public class LibroService {
    private final LibroDAO libroDAO;

    public LibroService() {
        this.libroDAO = new LibroDAO();
    }

    public String registrarLibro(Libro libro) {
        if (libro == null) {
            return "Los datos del libro son obligatorios";
        }
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            return "El título del libro es obligatorio";
        }
        if (libro.getTitulo().trim().length() > 200) {
            return "El título del libro no debe superar los 200 caracteres";
        }
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            return "El autor es obligatorio";
        }
        if (libro.getAutor().trim().length() > 150) {
            return "El autor no puede superar los 150 caracteres";
        }
        if (libro.getEditorial() != null && libro.getEditorial().trim().length() > 150) {
            return "La editorial no puede superar los 150 caracteres";
        }
        if (libro.getCantidadTotal() <= 0) {
            return "La cantidad debe ser mayor a 0";
        }
        if (libro.getCantidadDisponible() < 0) {
            return "La cantidad disponible no puede ser negativa";
        }
        if (libro.getCantidadDisponible() > libro.getCantidadTotal()) {
            return "La cantidad disponible no puede ser mayor que la cantidad total";
        }
        if (libroDAO.existeLibroPorTituloYAutor(libro.getTitulo().trim(), libro.getAutor().trim(), null)) {
            return "Ya existe un libro registrado con este título y autor.";
        }

        libro.setEstado(true);
        libro.setTitulo(libro.getTitulo().trim());
        libro.setAutor(libro.getAutor().trim());

        if (libro.getEditorial() != null) {
            libro.setEditorial(libro.getEditorial().trim());
            if (libro.getEditorial().isEmpty()) {
                libro.setEditorial(null);
            }
        }

        return libroDAO.registrar(libro) ? "Libro registrado correctamente." : "No se pudo registrar el libro.";
    }

    public RespuestaPaginada<Libro> obtenerTodosPaginado(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        ArrayList<Libro> lista = libroDAO.obtenerTodosPaginado(offset, limite);
        int totalRegistros = libroDAO.contarTotal();

        return new RespuestaPaginada<>(lista, pagina, limite, totalRegistros);
    }

    public Libro buscarPorId(int idLibro) {
        return (idLibro <= 0) ? null : libroDAO.buscarPorId(idLibro);
    }

    public String actualizar(Libro libro) {
        if (libro == null || libro.getIdLibro() <= 0) {
            return "Debe proporcionar datos e ID de libro válidos.";
        }

        Libro existente = libroDAO.buscarPorId(libro.getIdLibro());
        if (existente == null) {
            return "El libro que intenta actualizar no existe.";
        }
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            return "El título del libro es obligatorio.";
        }
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            return "El autor del libro es obligatorio.";
        }

        int ejemplaresPrestados = existente.getCantidadTotal() - existente.getCantidadDisponible();
        if (libro.getCantidadTotal() < ejemplaresPrestados) {
            return "La cantidad total no puede ser menor que los ejemplares actualmente prestados.";
        }

        if (libroDAO.existeLibroPorTituloYAutor(libro.getTitulo().trim(), libro.getAutor().trim(), libro.getIdLibro())) {
            return "Ya existe otro libro registrado con este título y autor.";
        }

        libro.setEstado(existente.isEstado());
        libro.setCantidadDisponible(libro.getCantidadTotal() - ejemplaresPrestados);
        libro.setTitulo(libro.getTitulo().trim());
        libro.setAutor(libro.getAutor().trim());

        if (libro.getEditorial() != null) {
            libro.setEditorial(libro.getEditorial().trim());
            if (libro.getEditorial().isEmpty()) {
                libro.setEditorial(null);
            }
        }

        return libroDAO.actualizar(libro) ? "Libro actualizado correctamente." : "No se pudo actualizar el libro.";
    }

    public String cambiarEstado(int idLibro, boolean estado) {
        if (idLibro <= 0) return "Debe proporcionar un ID de libro válido.";
        Libro existente = libroDAO.buscarPorId(idLibro);
        if (existente == null) return "El libro que intenta modificar no existe.";

        boolean resultado = libroDAO.cambiarEstado(idLibro, estado);
        if (resultado) {
            return estado ? "Libro activado correctamente." : "Libro desactivado correctamente.";
        }
        return "No se pudo cambiar el estado del libro.";
    }

    public ArrayList<Libro> obtenerCatalogo() {
        return libroDAO.obtenerCatalogo();
    }
}