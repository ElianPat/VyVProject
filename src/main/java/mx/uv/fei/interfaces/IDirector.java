/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.util.List;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Director;

/**
 *
 * @author alexs
 */
public interface IDirector {

    public List<Director> getAllDirector() throws DAOException;
    public Director getDirectorData(String email) throws DAOException;
}
