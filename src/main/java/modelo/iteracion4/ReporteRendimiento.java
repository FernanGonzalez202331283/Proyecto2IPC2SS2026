/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion4;

/**
 *
 * @author fernan
 */
public class ReporteRendimiento {
    private int idEstudiante;
    private String codigoEstudiante;
    private String nombres;
    private String apellidos;
    
    private int idAnioLectivo;
    private int anioLectivo;
    
    private String grado; 
    private String seccion; 
    
    private int idCurso;
    private String nombreCurso;
    
    private double promedio; 
    
    public ReporteRendimiento() {}

    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    public String getCodigoEstudiante() { return codigoEstudiante; }
    public void setCodigoEstudiante(String codigoEstudiante) { this.codigoEstudiante = codigoEstudiante; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public int getIdAnioLectivo() { return idAnioLectivo; }
    public void setIdAnioLectivo(int idAnioLectivo) { this.idAnioLectivo = idAnioLectivo; }

    public int getAnioLectivo() { return anioLectivo; }
    public void setAnioLectivo(int anioLectivo) { this.anioLectivo = anioLectivo; }

    // Compatibilidad para evitar NoSuchMethodError con código legado
    public int getIdAñoLectivo() { return idAnioLectivo; }
    public void setIdAñoLectivo(int idAñoLectivo) { this.idAnioLectivo = idAñoLectivo; }
    public int getAñoLectivo() { return anioLectivo; }
    public void setAñoLectivo(int añoLectivo) { this.anioLectivo = añoLectivo; }

    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }

    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }

    public double getPromedio() { return promedio; }
    public void setPromedio(double promedio) { this.promedio = promedio; }
}