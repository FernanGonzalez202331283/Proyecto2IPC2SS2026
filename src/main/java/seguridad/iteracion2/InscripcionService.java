/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad.iteracion2;

import Dao.iteracion2.InscripcionDAO;
import Dao.iteracion4.ObligacionPagoDAO;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.RespuestaPaginada;
import modelo.iteracion2.Inscripcion;
import modelo.iteracion4.ObligacionPago;

/**
 *
 * @author fernan
 */
public class InscripcionService {
private final InscripcionDAO inscripcionDAO;

    public InscripcionService() {
        this.inscripcionDAO = new InscripcionDAO();
    }

    public RespuestaPaginada<Inscripcion> obtenerInscripcionesPaginadas(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        List<Inscripcion> lista = inscripcionDAO.obtenerPaginados(limite, offset);
        int totalRegistros = inscripcionDAO.contarTotal();

        int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
        if (totalPaginas == 0) totalPaginas = 1;

        return new RespuestaPaginada<>(lista, pagina, totalPaginas, totalRegistros);
    }

    public String registrarInscripcion(Inscripcion inscripcion) {

        if (inscripcion == null) {
            return "Debe proporcionar la información de la inscripción.";
        }

        if (inscripcion.getIdEstudiante() <= 0) {
            return "Debe seleccionar un estudiante válido.";
        }

        if (inscripcion.getIdSeccion() <= 0) {
            return "Debe seleccionar una sección válida.";
        }

        if (!inscripcionDAO.existeEstudiante(inscripcion.getIdEstudiante())) {
            return "El estudiante seleccionado no existe.";
        }

        String estadoEstudiante = inscripcionDAO.obtenerEstadoEstudiante(inscripcion.getIdEstudiante());
        if (!"ACTIVO".equals(estadoEstudiante)) {
            return "El estudiante debe estar en estado ACTIVO para poder inscribirse.";
        }

        if (!inscripcionDAO.existeSeccion(inscripcion.getIdSeccion())) {
            return "La sección seleccionada no existe.";
        }

        if (!inscripcionDAO.seccionActiva(inscripcion.getIdSeccion())) {
            return "La sección seleccionada se encuentra inactiva.";
        }

        int idAñoLectivo = inscripcionDAO.obtenerAñoLectivoSeccion(inscripcion.getIdSeccion());
        if (idAñoLectivo <= 0 || !inscripcionDAO.añoLectivoActivo(idAñoLectivo)) {
            return "El ciclo escolar correspondiente a la sección está inactivo o no existe.";
        }

        int cantidadInscritos = inscripcionDAO.obtenerCantidadInscritos(inscripcion.getIdSeccion());
        int capacidadMaxima = inscripcionDAO.obtenerCapacidadSeccion(inscripcion.getIdSeccion());

        if (capacidadMaxima > 0 && cantidadInscritos >= capacidadMaxima) {
            return "La sección ha alcanzado su capacidad máxima de estudiantes (" + capacidadMaxima + ").";
        }

        if (inscripcionDAO.existeInscripcionActivaEnAño(inscripcion.getIdEstudiante(), idAñoLectivo)) {
            return "El estudiante ya posee una inscripción activa para este ciclo escolar.";
        }

        if (inscripcion.getFechaInscripcion() == null || inscripcion.getFechaInscripcion().trim().isEmpty()) {
            inscripcion.setFechaInscripcion(LocalDate.now().toString());
        }

        if (inscripcion.getEstado() == null || inscripcion.getEstado().trim().isEmpty()) {
            inscripcion.setEstado("ACTIVA");
        }

        if (inscripcionDAO.registrarInscripcion(inscripcion)) {
            generarObligacionesAutomaticas(inscripcion.getIdEstudiante(), idAñoLectivo);
            return "Inscripción registrada correctamente.";
        }

        return "No se pudo registrar la inscripción.";
    }

    public Inscripcion buscarPorId(int idInscripcion) {
        if (idInscripcion <= 0) {
            return null;
        }
        return inscripcionDAO.buscarPorId(idInscripcion);
    }

    public ArrayList<Inscripcion> obtenerTodos() {
        return inscripcionDAO.obtenerTodos();
    }

    public String actualizar(Inscripcion inscripcion) {

        if (inscripcion == null) {
            return "Debe proporcionar los datos de la inscripción.";
        }

        if (inscripcion.getIdInscripcion() <= 0) {
            return "El ID de la inscripción no es válido.";
        }

        Inscripcion existente = inscripcionDAO.buscarPorId(inscripcion.getIdInscripcion());
        if (existente == null) {
            return "La inscripción no existe.";
        }

        if (inscripcion.getIdEstudiante() <= 0 || inscripcion.getIdSeccion() <= 0) {
            return "El estudiante y la sección son obligatorios.";
        }

        int idAñoLectivo = inscripcionDAO.obtenerAñoLectivoSeccion(inscripcion.getIdSeccion());
        if (inscripcionDAO.existeInscripcionActivaEnAño(inscripcion.getIdEstudiante(), idAñoLectivo, inscripcion.getIdInscripcion())) {
            return "El estudiante ya tiene otra inscripción activa para este ciclo escolar.";
        }

        if (inscripcion.getFechaInscripcion() == null || inscripcion.getFechaInscripcion().trim().isEmpty()) {
            inscripcion.setFechaInscripcion(existente.getFechaInscripcion());
        }

        if (inscripcion.getEstado() == null || inscripcion.getEstado().trim().isEmpty()) {
            inscripcion.setEstado(existente.getEstado());
        }

        if (inscripcionDAO.actualizar(inscripcion)) {
            return "Inscripción actualizada correctamente.";
        }

        return "No se pudo actualizar la inscripción.";
    }

    public String cambiarEstado(int idInscripcion, String estado) {

        if (idInscripcion <= 0) {
            return "El ID de la inscripción no es válido.";
        }

        if (estado == null || estado.trim().isEmpty()) {
            return "Debe proporcionar un estado válido.";
        }

        if (!"ACTIVA".equals(estado) && !"RETIRADA".equals(estado) && !"FINALIZADA".equals(estado)) {
            return "El estado debe ser ACTIVA, RETIRADA o FINALIZADA.";
        }

        Inscripcion existente = inscripcionDAO.buscarPorId(idInscripcion);
        if (existente == null) {
            return "La inscripción no existe.";
        }

        if (inscripcionDAO.cambiarEstado(idInscripcion, estado)) {
            return "Estado de la inscripción actualizado correctamente.";
        }

        return "No se pudo actualizar el estado de la inscripción.";
    }

    private void generarObligacionesAutomaticas(int idEstudiante, int idAñoLectivo) {
        ObligacionPagoDAO obligacionDAO = new ObligacionPagoDAO();
        int anioActual = LocalDate.now().getYear();
        double cuotaFija = 350.00;

        ObligacionPago obligacionInscripcion = new ObligacionPago();
        obligacionInscripcion.setIdEstudiante(idEstudiante);
        obligacionInscripcion.setIdAñoLectivo(idAñoLectivo);
        obligacionInscripcion.setConcepto("INSCRIPCION");
        obligacionInscripcion.setDescripcion("Inscripción ciclo escolar " + anioActual);
        obligacionInscripcion.setMonto(cuotaFija);
        obligacionInscripcion.setFechaVencimiento(Date.valueOf(LocalDate.now().plusDays(15)));

        obligacionDAO.registrar(obligacionInscripcion);

        String[] meses = {"", "", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre"};

        for (int mes = 2; mes <= 11; mes++) {
            ObligacionPago colegiatura = new ObligacionPago();
            colegiatura.setIdEstudiante(idEstudiante);
            colegiatura.setIdAñoLectivo(idAñoLectivo);
            colegiatura.setConcepto("MES_DE_COLEGIATURA");
            colegiatura.setDescripcion("Mes de " + meses[mes] + " " + anioActual);
            colegiatura.setMonto(cuotaFija);

            LocalDate fechaVencimiento = LocalDate.of(anioActual, mes, 5);
            colegiatura.setFechaVencimiento(Date.valueOf(fechaVencimiento));

            obligacionDAO.registrar(colegiatura);
        }
    }
}