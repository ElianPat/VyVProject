/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.util.Objects;

/**
 *
 * @author alexs
 */
public class Professor extends User {

    private int professorNumber;
    private String role;

    public Professor() {
        this.professorNumber = 0;
    }

    public int getProfessorNumber() {
        return professorNumber;
    }

    public void setProfessorNumber(int professorNumber) {
        this.professorNumber = professorNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Professor) {
            Professor other = (Professor) object;
            return Objects.equals(this.getName(), other.getName())
                    && Objects.equals(this.getProfessorNumber(), other.getProfessorNumber())
                    && Objects.equals(this.getEmail(), other.getEmail())
                    && Objects.equals(this.getEnable(), other.getEnable())
                    && Objects.equals(this.getRole(), other.getRole());
        }
        return false;
    }
}
