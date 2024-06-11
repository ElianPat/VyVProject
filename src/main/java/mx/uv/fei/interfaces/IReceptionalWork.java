/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.util.List;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.ReceptionalWork;

/**
 *
 * @author aresj
 */
public interface IReceptionalWork {
    public int registerReceptionalWork(ReceptionalWork receptionalWork, int idPreliminaryProject, int directorNumber, int synodalNumber) throws DAOException;
    public List<ReceptionalWork> showReceptionalWorkFromDirector(int directorNumber) throws DAOException;
    public String getDirector(String receptionalWorkName) throws DAOException;
    public String getSynodal(String receptionalWorkName) throws DAOException;
    public int updateReceptionalWork(ReceptionalWork receptionalWork, int idPreliminaryProject, int directorNumber, int synodalNumber, int idReceptionalWork) throws DAOException;
    public int getIdReceptionalWork(String receptionalWorkName) throws DAOException;
    public ReceptionalWork getDataReceptionalWork(int IdReceptionalWork) throws DAOException;
    public boolean isReceptionalWorkIsExisting(String receptionalWorkName, int directorNumber) throws DAOException;
}
