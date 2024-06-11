/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.security.SecureRandom;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import mx.uv.fei.dataaccess.DataBaseManager;
import mx.uv.fei.gui.controller.App;
import mx.uv.fei.interfaces.IUser;

/**
 *
 * @author alexs
 */
public class UserDAO implements IUser {

    private UserRole userRole;

    private final int USER_TYPE_STUDENT = 1;
    private final int USER_TYPE_ADMIN = 2;
    private final int USER_TYPE_PROFESSOR_WITH_COURSE = 3;
    private final int USER_TYPE_PROFESSOR_WITHOUT_COURSE = 0;

    /**
     * regresa el tipo de usuario que sea el correo que se le pase.
     * 
     * @param email correo electronico de un usuario.
     * @return un objeto del tipo de usuario validos en el sistema.
     * @throws DAOException 
     */
    
    @Override
    public UserRole loginUserByType(String email) throws DAOException {
        try {
            Map<UserRole, Boolean> userRoles = validateUser(email);
            if (userRoles.containsKey(UserRole.STUDENT) && userRoles.get(UserRole.STUDENT)) {
                setUserRole(UserRole.STUDENT);
            } else if (userRoles.containsKey(UserRole.PROFESSORWITHCOURSE) && userRoles.get(UserRole.PROFESSORWITHCOURSE)) {
                setUserRole(UserRole.PROFESSORWITHCOURSE);
            } else if (userRoles.containsKey(UserRole.PROFESSORWITHOUTCOURSE) && userRoles.get(UserRole.PROFESSORWITHOUTCOURSE)) {
                setUserRole(UserRole.PROFESSORWITHOUTCOURSE);
            } else if (userRoles.containsKey(UserRole.ADMINISTRATOR) && userRoles.get(UserRole.ADMINISTRATOR)) {
                setUserRole(UserRole.ADMINISTRATOR);
            } else {
                throw new IllegalArgumentException("Correo o contraseña inválidos");
            }
        } catch (IllegalArgumentException ex) {
            throw new DAOException(ex.getMessage(), Status.WARNING);
        }
        return userRole;
    }
    
    /**
     * valida el correo que se le pase mediante el proceso almacenado y regresa el tipo 
     * de usuario y si cuenta con mas roles si es que se trata de un profesor.
     * 
     * @param email correo electronico del usuario.
     * @return un mapa con los roles y el tipo de usuario, 0 si el usuario no cuenta
     * con un tipo.
     * @throws DAOException 
     */

    @Override
    public Map<UserRole, Boolean> validateUser(String email) throws DAOException {
        Map<UserRole, Boolean> userRoles = new HashMap<>();
        try {
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            CallableStatement statement = connection.prepareCall("{CALL ValidateUser(?, ?, ?)}");
            statement.setString(1, email);
            statement.registerOutParameter(2, Types.INTEGER);
            statement.registerOutParameter(3, Types.VARCHAR);
            statement.execute();
            int userType = statement.getInt(2);
            String additionalRoles = statement.getString(3);
            switch (userType) {
                case USER_TYPE_STUDENT:
                    userRoles.put(UserRole.STUDENT, true);
                    break;
                case USER_TYPE_ADMIN:
                    userRoles.put(UserRole.ADMINISTRATOR, true);
                    break;
                case USER_TYPE_PROFESSOR_WITH_COURSE:
                    userRoles.put(UserRole.PROFESSORWITHCOURSE, true);
                    break;
                case USER_TYPE_PROFESSOR_WITHOUT_COURSE:
                    userRoles.put(UserRole.PROFESSORWITHOUTCOURSE, true);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de usuario no válido");
            }
            if (additionalRoles != null) {
                if (additionalRoles.contains("DIRECTOR")) {
                    userRoles.put(UserRole.DIRECTOR, true);
                }
                if (additionalRoles.contains("CODIRECTOR")) {
                    userRoles.put(UserRole.CODIRECTOR, true);
                }
                if (additionalRoles.contains("ACADEMICGROUP")) {
                    userRoles.put(UserRole.ACADEMICGROUP, true);
                }
            }
        } catch (SQLException ex) {
            throw new DAOException(""
                    + "Error de conexión", Status.WARNING);
        }
        return userRoles;
    }
    
    /**
     * obtiene la información, correo, nombre y contraseña del email colocado.
     * 
     * @param email correo del usuario.
     * @return un objeto de tipo usuario con su información, null si el correo
     * corresponde a un usuario no valido.
     * @throws DAOException 
     */

    @Override
    public User getUserData(String email) throws DAOException {
        User user = new User();
        try {
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            String query = "SELECT correo, Nombre, Contraseña FROM proyecto.usuario WHERE correo = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                user.setEmail(result.getString("correo"));
                user.setName(result.getString("Nombre"));
                user.setPassword(result.getString("Contraseña"));
            }
        } catch (SQLException ex) {
            throw new DAOException("No fue posible recuperar la información del usuario", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return user;
    }
    
    /**
     * valida que el correo pasado no corresponda a un usuario ya existente en la
     * base de datos, esto con la finalidad de evitar duplicidad de usuarios.
     * 
     * @param email correo electronico del usuario.
     * @return true si el correo pasado ya esta registrado en la base de datos, 
     * false si esto no es asi.
     * @throws DAOException 
     */

    @Override
    public boolean isUserExisting(String email) throws DAOException {
        boolean validateEmail = false;
        try {
            String query = "select * from usuario where correo like (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                validateEmail = true;
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible validar el usuario en el sistema", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return validateEmail;
    }
    
    /**
     * genera una contraseña aleatoria pero segura para el usuario que se registre.
     * 
     * @return una contraseña
     * @throws DAOException 
     */

    @Override
    public String generatePassword() throws DAOException {
        String charsRange = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&¡|°";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(charsRange.length());
            sb.append(charsRange.charAt(randomIndex));
        }
        return sb.toString();
    }

    /**
     * se añade un nuevo tipo de usuario a la base de datos.
     * 
     * @param user un objeto de tipo usuario.
     * @return 1 si el registro del usuario fue exitoso, -1 si esto no fue asi.
     * @throws DAOException 
     */
    
    @Override
    public int addUser(User user) throws DAOException {
        int result = -1;
        try {
            String query = "insert into usuario (Correo, Nombre, Contraseña) values (?,?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getName());
            statement.setString(3, user.getPassword());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar el usuario", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * modifica la información de un usuario pasandole su email.
     * 
     * @param user objeto de tipo usuario.
     * @param email correo electronico del usuario.
     * @return 1 si la actualización del usuario fue exitoso, -1 si esto no fue así.
     * @throws DAOException 
     */

    @Override
    public int modifyUser(User user, String email) throws DAOException {
        int result;
        try {
            String query = "UPDATE proyecto.usuario set Nombre=(?), correo=(?), habilitado=(?) WHERE correo=(?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setInt(3, user.getEnable());
            statement.setString(4, email);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible actualizar los datos del usuario", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }

    /**
     *
     * @param userRole
     */
    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    /**
     *
     * @return
     */
    public UserRole getUserRole() {
        return userRole;
    }
}
