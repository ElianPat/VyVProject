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
public class Student extends User {

    private String enrollment;
    private String educativeExperience;
    private int idPreliminaryProject;
    private int idEducativeExperience;
    private int idReceptionalWork;
    private int advanceNumber;
    private String preliminaryProjectName;
    private String nrcCourse;

    private final String ENROLLMENT_REGEX = "^s\\d{8}$";

    public String  getNrcCourse() {
        return nrcCourse;
    }

    public void setNrcCourse(String nrcCourse) {
        this.nrcCourse = nrcCourse;
    }

    public int getAdvanceNumber() {
        return advanceNumber;
    }

    public void setAdvanceNumber(int advanceNumber) {
        this.advanceNumber = advanceNumber;
    }

    public String getPreliminaryProjectName() {
        return preliminaryProjectName;
    }

    public void setPreliminaryProjectName(String preliminaryProjectName) {
        this.preliminaryProjectName = preliminaryProjectName;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        isEnrollmentValid(enrollment);
        this.enrollment = enrollment;
    }

    public String getEducativeExperience() {
        return educativeExperience;
    }

    public void setEducativeExperience(String educativeExperience) {
        this.educativeExperience = educativeExperience;
    }

    public int getIdPreliminaryProject() {
        return idPreliminaryProject;
    }

    public void setIdPreliminaryProject(int idPreliminaryProject) {
        this.idPreliminaryProject = idPreliminaryProject;
    }

    public int getIdEducativeExperience() {
        return idEducativeExperience;
    }

    public void setIdEducativeExperience(int idEducativeExperience) {
        this.idEducativeExperience = idEducativeExperience;
    }

    public int getIdReceptionalWork() {
        return idReceptionalWork;
    }

    public void setIdReceptionalWork(int idReceptionalWork) {
        this.idReceptionalWork = idReceptionalWork;
    }

    public String makeEmail(String enrollment) {
        String email = "z" + enrollment + "@estudiantes.uv.mx";
        return email;
    }

    public void isEnrollmentValid(String enrollment) {
        Pattern pattern = Pattern.compile(ENROLLMENT_REGEX);
        Matcher matcher = pattern.matcher(enrollment);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("La matircula ingresada debe tener las siguientes caracteristicas:\n"
                    + "1.- Debe iniciar con una letra S que sea minuscula.\n"
                    + "2.- Debe tener 8 numeros despues de la letra S.");
        }
    }

    @Override
    public String toString() {
        return getEnrollment() + " ";
    }
}
