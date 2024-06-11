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
public class AcademicGroup {

    private String key;
    private String nameAcademicGroup;

    private final String NAMEACADEMICGROUP_REGEX = "^[\\p{L}]+(?:\\s[\\p{L}]+)*$";
    private final String KEYACADEMICGROUP_REGEX = "^UV-CA-[0-9]{3}$";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        checkKeyAcademicGroup(key);
        this.key = key;
    }

    public String getNameAcademicGroup() {
        return nameAcademicGroup;
    }

    public void setNameAcademicGroup(String nameAcademicGroup) {
        checkNameAcademicGroup(nameAcademicGroup);
        this.nameAcademicGroup = nameAcademicGroup;
    }

    @Override
    public String toString() {
        return " " + getNameAcademicGroup();
    }

    private void checkNameAcademicGroup(String nameAcademicGroup) {
        if (nameAcademicGroup.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cuerpo academico no puede estar vacío");
        }
        Pattern pattern = Pattern.compile(NAMEACADEMICGROUP_REGEX);
        Matcher matcher = pattern.matcher(nameAcademicGroup);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("El nombre del cuerpo academico solo puede contener letras y un único espacio entre palabras");
        }
    }

    private void checkKeyAcademicGroup(String keyAcademicGroup) {
        if (keyAcademicGroup.trim().isEmpty()) {
            throw new IllegalArgumentException("La clave del cuerpo académico no puede estar vacía");
        }
        Pattern pattern = Pattern.compile(KEYACADEMICGROUP_REGEX);
        Matcher matcher = pattern.matcher(keyAcademicGroup);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("La clave del cuerpo académico debe seguir las siguientes características:\n"
                    + "1. Debe comenzar con los caracteres 'UV-' seguidos de 'CA-'.\n"
                    + "2. Después de 'CA-' debe contener 3 dígitos enteros.\n"
                    + "3. No pueden existir espacios antes, durante o al final de la clave del cuerpo academico"
                    + "Ejemplo válido: 'UV-CA-123'");
        }
    }
}
