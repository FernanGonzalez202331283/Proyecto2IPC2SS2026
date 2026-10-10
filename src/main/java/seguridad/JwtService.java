/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 *
 * @author fernan
 */
public class JwtService {
    private static final String CLAVE_SECRETA =
            "AvU/7HXYU1Fr5+EpcEwxPv2IeNHtERVstcNIuLJ0eo4=";

    private static final long TIEMPO_EXPIRACION =
            1000 * 60 * 60; // 1 hor
    private final SecretKey key;

    public JwtService() {

        key = Keys.hmacShaKeyFor(
                java.util.Base64.getDecoder().decode(
                        CLAVE_SECRETA));
    }

    public String generarToken(
            int idUsuario,
            String username,
            String rol) {

        Date ahora = new Date();

        Date expiracion =
                new Date(
                        ahora.getTime()
                        + TIEMPO_EXPIRACION);

        return Jwts.builder()
                .subject(username)
                .claim("idUsuario", idUsuario)
                .claim("rol", rol)
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
}
