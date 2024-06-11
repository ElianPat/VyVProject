/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.uv.fei.interfaces;

import java.util.Map;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.User;
import mx.uv.fei.logic.UserRole;

/**
 *
 * @author alexs
 */
public interface IUser {

    public UserRole loginUserByType(String email) throws DAOException;
    public Map<UserRole, Boolean> validateUser(String email) throws DAOException;
    public User getUserData(String email) throws DAOException;
    public boolean isUserExisting(String email) throws DAOException;
    public String generatePassword() throws DAOException;
    public int addUser(User user) throws DAOException;
    public int modifyUser (User user, String email) throws DAOException;
}
