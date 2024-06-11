
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.util.List;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Professor;

/**
 *
 * @author alexs
 */
public interface IProfessor {

    public List<Professor> getProfessorNameOfAcademicGroup(String keyAcademicGroup) throws DAOException;
    public int getProfessorNumber(String name) throws DAOException;
    public List<Professor> getAllProfessorOfAcademicGroup(String keyAcademicGroup) throws DAOException;
    public Professor modifyMembersAcademicGroup(String name, String keyAcademicGroup)throws DAOException;
    public List<Professor> getSynodal() throws DAOException;
    public List<Professor> getDirector() throws DAOException;
    public int getProfessorNumberByEmail(String email) throws DAOException;
    public Professor getInformation(String email) throws DAOException;
    public String getEmailDirector (String title) throws DAOException;
    public int registerProfessor(Professor professor) throws DAOException;
    public int registerDirector(Professor professor) throws DAOException;
    public int registerCodirector(Professor professor) throws DAOException;
    public int registerSynodal(Professor professor) throws DAOException;
    public List<Professor> getProfessorToModify() throws DAOException;
    public Professor getInfoToModifyProfessor(int personalNumber) throws DAOException;
    public int updateProfessor(Professor professor, String email) throws DAOException;
    public List<Professor> getAllProfessor() throws DAOException;
    public boolean isPersonalNumberExisting(int personalNumber) throws DAOException;
}
