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
      public static String codificar(String contraseña) {

        return Base64.getEncoder().encodeToString(
                contraseña.getBytes(StandardCharsets.UTF_8)
        );
    }
}
