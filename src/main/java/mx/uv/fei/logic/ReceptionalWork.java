/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aresj
 */
public class ReceptionalWork {

    private String receptionalWorkName;
    private Date startDate;
    private Date endDate;

    private final String RECEPCIONALWORKNAME_REGEX = "^[\\p{L}]+(?:\\s[\\p{L}]+)*$";

    public String getReceptionalWorkName() {
        return receptionalWorkName;
    }

    public void setReceptionalWorkName(String receptionalWorkName) {
        checkNameRecepcionalWork(receptionalWorkName);
        this.receptionalWorkName = receptionalWorkName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return getReceptionalWorkName() + " " + getStartDate();
    }

    private void checkNameRecepcionalWork(String receptionalWorkName) {
        if (receptionalWorkName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del trabajo recepcional no puede estar vacío");
        }
        Pattern pattern = Pattern.compile(RECEPCIONALWORKNAME_REGEX);
        Matcher matcher = pattern.matcher(receptionalWorkName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("El nombre del trabajo recepcional solo puede contener letras y un único espacio entre palabras");
        }
    }
}
