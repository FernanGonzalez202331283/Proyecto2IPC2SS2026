/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.etapa3;

import Dao.iteracion3.LibroDAO;
import java.util.ArrayList;
import modelo.iteracion3.Libro;

/**
 *
 * @author fernan
 */
public class LibroService {
    private final LibroDAO libroDAO;
    
    public LibroService(){
        this.libroDAO = new LibroDAO();
    }
    
    public String registrarLibro(Libro libro){
        if(libro == null){
            return "los datos del libro son obligatorios";
        }
        
        if(libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()){
            return "EL titulo del libro es obligatorio";
        }
        
        if(libro.getTitulo().trim().length() > 200){
            return "El titulo del libro no debe superar los 200 caracteres";
        }
        
        if(libro.getAutor() == null || libro.getAutor().trim().isEmpty()){
            return "el aurtor es obligatorio";
        }
        
        if(libro.getAutor().trim().length() > 150){
            return "el autor no puede superar los 150 caracteres";
        }
        
        if(libro.getEditorial() != null && libro.getEditorial().trim().length()> 150){
            return "el editorial no puede superar los 150 caracteres";
        }
        
        if(libro.getCantidadTotal() <= 0){
            return "La cantidad debe ser mayor a 0";
        }
        
        if(libro.getCantidadDisponible() < 0){
            return "La cantidad disponible no puede ser negativa";
        }
       
        if(libro.getCantidadDisponible() > libro.getCantidadTotal()){
            return "la cantidad disponible no puede ser mayor que la cantidad total";
        }
        libro.setTitulo(libro.getTitulo().trim());
        libro.setAutor(libro.getAutor().trim());
        if (libro.getEditorial() != null) {
            libro.setEditorial(libro.getEditorial().trim());

            if (libro.getEditorial().isEmpty()) {
                libro.setEditorial(null);
            }
        }
        boolean resultado = libroDAO.registrar(libro);
        if (resultado) {
            return "Libro registrado correctamente.";
        }
        return "No se pudo registrar el libro.";
    }

    public ArrayList<Libro> obtenerTodos() {
        return libroDAO.obtenerTodos();
    }

    public Libro buscarPorId(int idLibro) {
        if (idLibro <= 0) {
            return null;
        }
        return libroDAO.buscarPorId(idLibro);
    }

    public String actualizar(Libro libro) {
        if (libro == null) {
            return "Los datos del libro son obligatorios.";
        }
        if (libro.getIdLibro() <= 0) {
            return "Debe proporcionar un ID de libro válido.";
        }
        Libro existente = libroDAO.buscarPorId(libro.getIdLibro());

        if (existente == null) {
            return "El libro que intenta actualizar no existe.";
        }

        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            return "El título del libro es obligatorio.";
        }

        if (libro.getTitulo().trim().length() > 200) {
            return "El título no puede superar los 200 caracteres.";
        }

        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            return "El autor del libro es obligatorio.";
        }

        if (libro.getAutor().trim().length() > 150) {
            return "El autor no puede superar los 150 caracteres.";
        }

        if (libro.getEditorial() != null
                && libro.getEditorial().trim().length() > 150) {
            return "La editorial no puede superar los 150 caracteres.";
        }

        if (libro.getCantidadTotal() <= 0) {
            return "La cantidad total debe ser mayor que cero.";
        }

        if (libro.getCantidadDisponible() < 0) {
            return "La cantidad disponible no puede ser negativa.";
        }

        if (libro.getCantidadDisponible() > libro.getCantidadTotal()) {
            return "La cantidad disponible no puede ser mayor que la cantidad total.";
        }

        int ejemplaresPrestados = existente.getCantidadTotal()- existente.getCantidadDisponible();
        if (libro.getCantidadTotal() < ejemplaresPrestados) {
            return "La cantidad total no puede ser menor que los ejemplares actualmente prestados.";
        }
        int nuevaCantidadDisponible = libro.getCantidadTotal()- ejemplaresPrestados;
        libro.setCantidadDisponible(nuevaCantidadDisponible);
        libro.setTitulo(libro.getTitulo().trim());
        libro.setAutor(libro.getAutor().trim());
        if (libro.getEditorial() != null) {
            libro.setEditorial(libro.getEditorial().trim());

            if (libro.getEditorial().isEmpty()) {
                libro.setEditorial(null);
            }
        }
        boolean resultado = libroDAO.actualizar(libro);

        if (resultado) {
            return "Libro actualizado correctamente.";
        }

        return "No se pudo actualizar el libro.";
    }

    public String cambiarEstado(int idLibro, boolean estado) {

        if (idLibro <= 0) {
            return "Debe proporcionar un ID de libro válido.";
        }

        Libro existente = libroDAO.buscarPorId(idLibro);

        if (existente == null) {
            return "El libro que intenta modificar no existe.";
        }

        boolean resultado = libroDAO.cambiarEstado(idLibro, estado);

        if (resultado) {
            return estado
                    ? "Libro activado correctamente."
                    : "Libro desactivado correctamente.";
        }

        return "No se pudo cambiar el estado del libro.";
    }
    
    public ArrayList<Libro> obtenerCatalogo() {
        return libroDAO.obtenerCatalogo();
    }
        
}
