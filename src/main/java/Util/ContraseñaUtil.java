/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 *
 * @author fernan
 */
public class ContraseñaUtil {
   private ContraseñaUtil() {
        // Constructor privado para prevenir instanciación
    }

    public static String codificar(String contraseña) {
        if (contraseña == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(
                contraseña.trim().getBytes(StandardCharsets.UTF_8)
        ).trim();
    }

    public static String decodificar(String contraseñaBase64) {
        if (contraseñaBase64 == null) {
            return null;
        }
        byte[] bytesDecodificados = Base64.getDecoder().decode(contraseñaBase64.trim());
        return new String(bytesDecodificados, StandardCharsets.UTF_8).trim();
    }
}
