/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.util.List;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;

/**
 *
 * @author alexs
 */
public interface IPreliminaryProject {
    
    public int createPreliminaryProject(PreliminaryProject preliminaryProject) throws DAOException;
    public boolean isPreliminaryProjectExisting(String preliminaryProjectName) throws DAOException;
    public List<PreliminaryProject> getPreliminaryProjectsFromDirector(int directorNumber) throws DAOException;
    public int getIdPreliminaryProject(String preliminaryProjectName) throws DAOException;
    public String getPreliminaryProjectNameWithReceptionalWork(String receptionalWorkName) throws DAOException;
    public List<PreliminaryProject> getPreliminaryProjectsForAssignament(int directorNumber) throws DAOException;
    public int getReceptionalWorkWithPreliminaryProject(int IdPreliminaryProject) throws DAOException;
    public int modifyQuotaPreliminaryProject(int IdPreliminaryProject) throws DAOException;
    public List<PreliminaryProject> getNameProjectPerDirector(int directorNumber) throws DAOException;
    public PreliminaryProject getInformation (String title) throws DAOException;
    public List<PreliminaryProject> getNameProjectPerCodirector(int codirectorNumber) throws DAOException;
    public int updateStatePreliminaryProject(String state, String title)throws DAOException;
    public List<PreliminaryProject> getPreliminaryProjectsForAcademicGroup(String keyAcademicGroup) throws DAOException;
    public List<PreliminaryProject> getAllPreliminaryProject(int directorNumber) throws DAOException;
    public int updatePreliminaryProject(PreliminaryProject preliminaryProject, String preliminaryProjectName) throws DAOException; 
   
}
