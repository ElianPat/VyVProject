/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author alexs
 */
public class User {

    private String name;
    private String password;
    private String email;
    private int enable;

    private final String EMAIL_REGEX = "^(?=.{1,256}$)[^\\s@]+@(?:uv\\.mx|estudiantes\\.uv\\.mx|gmail\\.com|hotmail\\.com|outlook\\.com|edu\\.mx)$";
    private final String NAME_REGEX = "^[\\p{L}]+(?:\\s[\\p{L}]+)*$";

    public User() {
        this.name = " ";
        this.password = " ";
        this.email = " ";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkName(name);
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        checkEmail(email);
        this.email = email;
    }

    public int getEnable() {
        return enable;
    }


    public void setEnable(int enable) {
        this.enable = enable;
    }

    private void checkEmail(String email) {
        if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico no puede estar vacío");
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("El email debe contener las siguientes características:\n"
                    + "1.- No debe contener espacios en blanco\n"
                    + "2.- Solo los siguientes dominios son permitidos: (@uv.mx, @estudiantes.uv.mx, @gmail.com, @hotmail.com, @outlook.com, @edu.mx)\n");
        }
    }

    private void checkName(String name) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
        }
        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("El nombre del usuario solo puede contener letras y un único espacio entre palabras");
        }
    }
}
