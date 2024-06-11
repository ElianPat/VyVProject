/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.util.Objects;

/**
 *
 * @author aresj
 */
public class Course {

    private String nrc;
    private String period;
    private int idEducativeExperience;
    private int professorNumber;
    private String subject;
    private String professor;

    public Course() {

    }

    public Course(String nrc) {
        this.nrc = nrc;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getIdEducativeExperience() {
        return idEducativeExperience;
    }

    public void setIdEducativeExperience(int idEducativeExperience) {
        this.idEducativeExperience = idEducativeExperience;
    }

    public int getProfessorNumber() {
        return professorNumber;
    }

    public void setProfessorNumber(int professorNumber) {
        this.professorNumber = professorNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    @Override
    public String toString() {
        return getNrc();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Course) {
            Course other = (Course) object;
            return Objects.equals(this.getNrc(), other.getNrc())
                    && Objects.equals(this.getSubject(), other.getSubject())
                    && Objects.equals(this.getPeriod(), other.getPeriod());
        }
        return false;
    }
}
