/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import static mx.uv.fei.gui.controller.App.configureStage;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Director;
import mx.uv.fei.logic.DirectorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.User;
import mx.uv.fei.logic.UserDAO;
import mx.uv.fei.logic.ClassHolder;
import org.mindrot.jbcrypt.BCrypt;

/**
 * FXML Controller class
 * Esta clase sirve para poder iniciar sesión al usuario.
 * @author alexs
 */
public class LoginController implements Initializable {

    @FXML
    private TextField textFieldUserEmail;
    @FXML
    private PasswordField textFieldPassword;

    ClassHolder classHolder = ClassHolder.getInstance();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setStyleComponents();
    }

    public void setStyleComponents() {
        textFieldUserEmail.setStyle("-fx-control-inner-background: #FAB237;-fx-prompt-text-fill: black;");
        textFieldPassword.setStyle("-fx-control-inner-background: #FAB237;-fx-prompt-text-fill: black;");
    }

    @FXML
    void signInOnAction(ActionEvent event) {
        try {   
            User user = new User();
            UserDAO userDAO = new UserDAO();
            user = userDAO.getUserData(textFieldUserEmail.getText());
            classHolder.setUser(user);

            if (userDAO.isUserExisting(textFieldUserEmail.getText())) {
                String password = textFieldPassword.getText();
                String passwordHash = user.getPassword();
                if (BCrypt.checkpw(password, passwordHash)) {
                    invokeAuthenticateUser(textFieldUserEmail.getText());
                } else {
                    DialogGenerator.getDialog("Contraseña incorrecta", Status.WARNING);
                }
            } else {
                DialogGenerator.getDialog("Correo incorrecto", Status.WARNING);
            }
        } catch (DAOException | IllegalArgumentException | IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    void loginOnKeyUser(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            signInOnAction(new ActionEvent());
        }
    }

    @FXML
    void loginOnKeyPassword(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            signInOnAction(new ActionEvent());
        }
    }

    private void invokeAuthenticateUser(String email) throws DAOException, IOException {
        UserDAO userDAO = new UserDAO();
        Director director = new Director();
        DirectorDAO directorDAO = new DirectorDAO();
        switch (userDAO.loginUserByType(email)) {
            case STUDENT:
                App.openNewWindow("/fxml/HomeStudent.fxml");
                break;
            case PROFESSORWITHCOURSE:
            case PROFESSORWITHOUTCOURSE:
                director = directorDAO.getDirectorData(textFieldUserEmail.getText());
                classHolder.setDirector(director);
                loginForProfessor("/fxml/MainInterface.fxml");
                break;
            case ADMINISTRATOR:
                App.openNewWindow("/fxml/HomeAdministrator.fxml");
                break;
            default:
                DialogGenerator.getDialog("No se pudo abrir la interfaz del usuario", Status.FATAL);
                break;
        }
    }

    private void loginForProfessor(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();

        MainInterfaceController controller = loader.getController();
        controller.enableTabs(textFieldUserEmail.getText(), textFieldPassword.getText());

        double width = root.prefWidth(USE_COMPUTED_SIZE);
        double height = root.prefHeight(USE_COMPUTED_SIZE);

        Stage stage = (Stage) textFieldUserEmail.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        configureStage(stage, width, height);
        stage.show();

        scene.getWindow().setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
