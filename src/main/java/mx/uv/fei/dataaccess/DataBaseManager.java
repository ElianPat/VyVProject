/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.dataaccess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import mx.uv.fei.gui.controller.App;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Status;

/**
 *
 * @author alexs
 */
public class DataBaseManager {

    private static Connection connection;
    private static final String DATABASE_NAME = "dataBase-name";
    private static final String DATABASE_USER = "dataBase-user";
    private static final String DATABASE_PASSWORD = "dataBase-password";

    public Connection getConnection() throws SQLException {
        connect();
        return connection;
    }

    private static void connect() throws SQLException {
        Properties properties = new DataBaseManager().getPropertiesFile();
        if (properties != null) {
            connection = DriverManager.getConnection(
                    properties.getProperty(DATABASE_NAME),
                    properties.getProperty(DATABASE_USER),
                    properties.getProperty(DATABASE_PASSWORD));
        } else {
            throw new SQLException("No fue posible encontrar las credenciales de la base de datos");
        }
    }

    public static boolean close() throws DAOException {
        boolean isClosed = false;
        try {
            if (connection != null) {
                connection.close();
            }
            isClosed = true;
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException(
                    "Lo sentimos, algo va mal con el sistema",
                    Status.FATAL);
        }
        return isClosed;
    }

    private Properties getPropertiesFile() {
        Properties properties = null;
        try {
            InputStream file = new FileInputStream("src/main/resources/DataBaseConnection.properties");
            if (file != null) {
                properties = new Properties();
                properties.load(file);
            }
            file.close();
        } catch (FileNotFoundException ex) {
            App.getLogger().error(ex.getMessage());
        } catch (IOException ex) {
            App.getLogger().error(ex.getMessage());
        }
        return properties;
    }
}
