package seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import modelo.Usuario;

/**
 *
 * @author fernan
 */
public class JwtService {

    private static final String CLAVE_SECRETA
            = "AvU/7HXYU1Fr5+EpcEwxPv2IeNHtERVstcNIuLJ0eo4=";

    private static final long TIEMPO_EXPIRACION
            = 4000 * 60 * 60; 
    private final SecretKey key;

    public JwtService() {
        key = Keys.hmacShaKeyFor(
                java.util.Base64.getDecoder().decode(CLAVE_SECRETA));
    }

    public String generarToken(int idUsuario, String username, String rol, int idPersona) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + TIEMPO_EXPIRACION);

        return Jwts.builder()
                .subject(username)
                .claim("idUsuario", idUsuario)
                .claim("rol", rol)
                .claim("idPersona", idPersona)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(key)
                .compact();
    }

    public Claims validarToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String obtenerUsername(String token) {
        Claims claims = validarToken(token);
        return claims.getSubject();
    }

    public String obtenerRol(String token) {
        Claims claims = validarToken(token);
        return claims.get("rol", String.class);
    }

    public int obtenerIdUsuario(String token) {
        Claims claims = validarToken(token);
        return claims.get("idUsuario", Integer.class);
    }

    public Usuario obtenerUsuarioDesdeToken(String token) {
        try {
            Claims claims = validarToken(token);

            Usuario usuario = new Usuario();
            usuario.setIdUsuario(claims.get("idUsuario", Integer.class));
            usuario.setUsername(claims.getSubject());
            usuario.setRol(claims.get("rol", String.class));

            Integer idPersona = claims.get("idPersona", Integer.class);
            if (idPersona != null) {
                usuario.setIdPersona(idPersona);
            }

            usuario.setEstado(true);

            return usuario;
        } catch (Exception e) {
            System.err.println("Error al validar token JWT: " + e.getMessage());
            return null;
        }
    }
}
