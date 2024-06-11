/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.util.List;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.EducativeExperience;

/**
 *
 * @author alexs
 */
public interface IEducativeExperience {
    
    public int createEducativeExperience(EducativeExperience educativeExperience) throws DAOException;
    public int getIdEducativeExperience (String subject) throws DAOException;
    public List<EducativeExperience> getAllEducativeExperience() throws DAOException;
}
