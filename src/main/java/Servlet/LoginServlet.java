/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import modelo.Usuario;
import servicio.AuthService;

/**
 *
 * @author fernan
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet{
    private AuthService authService;
    
    @Override
    public void init() throws ServletException
    {
     super.init();
     authService = new AuthService();   
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String username = request.getParameter("username");
        String contraseña = request.getParameter("contraseña");
        
        Usuario usuario = authService.iniciarSesion(username, contraseña);
        
        if(usuario !=null){
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);
            
            response.getWriter().println("inicio de sesion correctamente");
            response.getWriter().println("usuario: " +usuario.getUsername());
            response.getWriter().println("contraseña: "+usuario.getContraseña());
        }
    }
}
