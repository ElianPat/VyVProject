/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.util.List;
import mx.uv.fei.logic.Codirector;
import mx.uv.fei.logic.DAOException;

/**
 *
 * @author alexs
 */
public interface ICodirector {

    public List<Codirector> getAllCodirector() throws DAOException;
    public int getCodirectorNumber(String name) throws DAOException;
}
