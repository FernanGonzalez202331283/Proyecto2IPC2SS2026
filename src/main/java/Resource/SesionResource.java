/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto2ipc2ss2026_temp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.LoginResponse;
import modelo.Usuario;

/**
 *
 * @author fernan
 */
@Path("/sesion")
public class SesionResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerSesion(@Context HttpServletRequest httpRequest) {

        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }

        LoginResponse respuesta = new LoginResponse();

        respuesta.setIdUsuario(usuario.getIdUsuario());
        respuesta.setUsername(usuario.getUsername());
        respuesta.setIdPersona(usuario.getIdPersona());
        respuesta.setRol(usuario.getRol());
        respuesta.setEstado(usuario.isEstado());

        return Response.ok(respuesta).build();
    }
}
