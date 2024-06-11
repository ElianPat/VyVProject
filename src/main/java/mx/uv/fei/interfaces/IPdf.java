/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Pdf;

/**
 *
 * @author Palom
 */
public interface IPdf {
    
    public int addEvidence(Pdf pdf, String enrollment, int advanceNumber)throws DAOException;
}
