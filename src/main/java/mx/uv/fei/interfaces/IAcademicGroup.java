/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.util.List;
import mx.uv.fei.logic.AcademicGroup;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Professor;

/**
 *
 * @author alexs
 */
public interface IAcademicGroup {
    
    public int registerAcademicGroup(AcademicGroup academicBody) throws DAOException;
    public List<AcademicGroup> getAllAcademicGroup() throws DAOException;
    public int addProfessorInAcademicGroup(AcademicGroup academicGroup, Professor professor) throws DAOException;
    public boolean isAcademicGroupExisting(String keyAcademicGroup) throws DAOException;
    int deleteProfessorOfAcademicGroup(int personalNumber, String keyAcademicGroup) throws DAOException;
    int modifyMember(Professor professor, int personalNumber, String keyAcademicGroup) throws DAOException;
    public String getKeyAcademicGroup (int personalNumber) throws DAOException;
    public boolean isProfessorMember(int personalNumber, String keyAcademicGroup) throws DAOException;
    public String getKeyAcademicGroupByName(String name) throws DAOException;
}
