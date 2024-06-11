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
 * @author alexs
 */
public class Advance {

    private int advanceNumber;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private String endTime;
    private String advanceState;

    private final String TXT_REGEX = "[\\p{L}\\p{N}\\p{P}\\s]{3,50}";
    private final String TXTAREA_REGEX = "[\\p{L}\\p{N}\\p{P}\\s]{3,300}";

    public int getAdvanceNumber() {
        return advanceNumber;
    }

    public void setAdvanceNumber(int advanceNumber) {
        this.advanceNumber = advanceNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        checkTextTxt(title);
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        checkTextArea(description);
        this.description = description;
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAdvanceState() {
        return advanceState;
    }

    public void setAdvanceState(String advanceState) {
        this.advanceState = advanceState;
    }

    private void checkTextArea(String name) {
        Pattern pattern = Pattern.compile(TXTAREA_REGEX);
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("El texto ingresado debe cumplir los siguientes criterios:\n"
                    + "1. Debe contener de 3 a 300 caracteres como máximo.\n"
                    + "2. No puede contener más de 2 espacios en blanco consecutivos.\n");
        }
    }

    private void checkTextTxt(String name) {
        Pattern pattern = Pattern.compile(TXT_REGEX);
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("El texto ingresado debe tener las siguientes características:\n"
                    + "1.- Debe contener de 3 a 50 caractéres como máximo\n"
                    + "2.- No puede contener más de 2 espacios en blanco juntos\n");
        }
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
