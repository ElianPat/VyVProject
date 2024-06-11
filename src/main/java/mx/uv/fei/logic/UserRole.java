/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package mx.uv.fei.logic;

/**
 *
 * @author alexs
 */
public enum UserRole {

    STUDENT("100"),
    PROFESSORWITHCOURSE("200"),
    PROFESSORWITHOUTCOURSE("300"),
    DIRECTOR("400"),
    CODIRECTOR("500"),
    ACADEMICGROUP("600"),
    ADMINISTRATOR("700");
    
    private String code;

    private UserRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    
    public void setCode(String code){
        this.code = code;
    }
}
