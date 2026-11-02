/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 *
 * @author fernan
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {

    private final JwtService jwtService = new JwtService();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        if (path.contains("login") || path.contains("recuperar-password") || requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"mensaje\": \"Acceso denegado: Token de autenticación no proporcionado.\"}")
                        .build()
            );
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            Claims claims = jwtService.validarToken(token);
            String username = claims.getSubject();
            Object rolObj = claims.get("rol");
            String rolToken = (rolObj != null) ? rolObj.toString().trim() : "";

            System.out.println("=== JWT FILTER DEBUG ===");
            System.out.println("Usuario autenticado: " + username);
            System.out.println("Rol extraído del token: '" + rolToken + "'");

            boolean isSecure = requestContext.getSecurityContext().isSecure();

            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> username;
                }

                @Override
                public boolean isUserInRole(String role) {
                    if (rolToken.isEmpty() || role == null) {
                        return false;
                    }
                    String cleanRol = rolToken.replace("ROLE_", "").trim();
                    String cleanRole = role.replace("ROLE_", "").trim();
                    boolean match = cleanRol.equalsIgnoreCase(cleanRole);
                    System.out.println("Evaluando rol: Token('" + cleanRol + "') vs Requerido('" + cleanRole + "') -> Permitido: " + match);
                    return match;
                }

                @Override
                public boolean isSecure() {
                    return isSecure;
                }

                @Override
                public String getAuthenticationScheme() {
                    return "Bearer";
                }
            });

        } catch (Exception e) {
            System.err.println("Error al validar token JWT en filtro: " + e.getMessage());
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"mensaje\": \"Acceso denegado: Token inválido o expirado.\"}")
                        .build()
            );
        }
    }
}