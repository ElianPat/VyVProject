/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.sql.Blob;
import java.util.List;
import mx.uv.fei.logic.Advance;
import mx.uv.fei.logic.DAOException;

/**
 *
 * @author alexs
 */
public interface IAdvance {
    
    public int updateEvidence (int advanceNumber, byte[] evidence) throws DAOException;
    public int updateAdvance (String newDescription, int advanceNumber) throws DAOException;
    public int validationAdvanceDirector(int advanceNumber, String validation) throws DAOException;
    public int validationAdvanceCodirector(int advanceNumber, String validation) throws DAOException;
    public int getAdvanceNumber(String title) throws DAOException;
    public int scheduleAdvance(Advance advance) throws DAOException;
    public int studentAdvance(String enrollment, int advanceNumber) throws DAOException;
    public List<Blob> showAdvanceDocuments(int advanceNumber) throws DAOException;
    public List<Advance> showNameAdvance(String enrollment)throws DAOException;
    public int getIdAdvance(String title)throws DAOException;
    public Advance getDataAdvance(int advanceNumber) throws DAOException;
    public List<Advance> showAdvanceFromStudent(String enrollment) throws DAOException;
    public boolean isAdvanceDuplication(String advanceTitle, String enrollment) throws DAOException;
    public boolean isDeliverAdvance(String advanceTitle, String enrollment) throws DAOException; 
}
