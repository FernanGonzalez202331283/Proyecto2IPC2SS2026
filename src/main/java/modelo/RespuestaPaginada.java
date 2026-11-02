package modelo;


import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author fernan
 */
public class RespuestaPaginada<T> {
    private List<T> contenido;
    private int paginaActual;
    private int totalPaginas;
    private int totalRegistros;

    public RespuestaPaginada(List<T> contenido, int paginaActual, int limite, int totalRegistros) {
        this.contenido = contenido;
        this.paginaActual = paginaActual;
        this.totalRegistros = totalRegistros;
        this.totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
    }

    public List<T> getContenido() { return contenido; }
    public int getPaginaActual() { return paginaActual; }
    public int getTotalPaginas() { return totalPaginas; }
    public int getTotalRegistros() { return totalRegistros; }
}