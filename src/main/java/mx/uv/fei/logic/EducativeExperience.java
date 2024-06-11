/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

/**
 *
 * @author alexs
 */
public class EducativeExperience {

    private int idEducativeExperience;
    private String educativeExperienceName;

    public int getIdEducativeExperience() {
        return idEducativeExperience;
    }

    public void setIdEducativeExperience(int idEducativeExperience) {
        this.idEducativeExperience = idEducativeExperience;
    }

    public String getEducativeExperienceName() {
        return educativeExperienceName;
    }

    public void setEducativeExperienceName(String educativeExperienceName) {
        this.educativeExperienceName = educativeExperienceName;
    }

    @Override
    public String toString() {
        return getEducativeExperienceName() + "";
    }
}
