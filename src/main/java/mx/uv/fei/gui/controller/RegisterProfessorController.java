/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Email;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.User;
import mx.uv.fei.logic.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

/**
 * FXML Controller class
 * este controlador se encarga de crear profesores.
 * @author Palom
 */
public class RegisterProfessorController implements Initializable {

    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPersonalNumber;
    @FXML
    private CheckBox checkBoxDirector;
    @FXML
    private CheckBox checkBoxCodirector;
    @FXML
    private CheckBox checkBoxSynodal;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limitTextField(5);
    }

    @FXML
    private void registerProfessorOnAction(ActionEvent event) {
        try {
            if (textFieldPersonalNumber.getText().isEmpty()) {
                DialogGenerator.getDialog("No puede dejar campos vacios", Status.WARNING);
            } else {
                invokeRegistration(getDataFromForm());
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private void invokeRegistration(Professor professor) throws DAOException {
        User user = new User();
        UserDAO userDAO = new UserDAO();
        ProfessorDAO professorDAO = new ProfessorDAO();
        if (userDAO.isUserExisting(professor.getEmail()) == false) {
            if (professorDAO.isPersonalNumberExisting(professor.getProfessorNumber()) == false) {
                String password = userDAO.generatePassword();
                String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
                user.setName(professor.getName());
                user.setEmail(professor.getEmail());
                user.setPassword(passwordHash);
                if (userDAO.addUser(user) > 0 && professorDAO.registerProfessor(professor) > 0) {
                    roleRegistration(professor);
                    sendPasswordToUser(user, password);
                    DialogGenerator.getDialog("Registro realizado exitosamente", Status.SUCCESS);
                } else {
                    DialogGenerator.getDialog("No se pudo registrar al profesor", Status.WARNING);
                }
            } else {
                DialogGenerator.getConfirmationDialog("Numero de personal registrado previamente en el sistema",
                        "Duplicidad de datos");
            }
        } else {
            DialogGenerator.getConfirmationDialog("Correo registrado previamente en el sistema",
                    "Duplicidad de datos");
        }
    }

    private void roleRegistration(Professor professor) throws DAOException {
        ProfessorDAO professorDAO = new ProfessorDAO();
        if (checkBoxDirector.isSelected()) {
            professorDAO.registerDirector(professor);
        }
        if (checkBoxCodirector.isSelected()) {
            professorDAO.registerCodirector(professor);
        }
        if (checkBoxSynodal.isSelected()) {
            professorDAO.registerSynodal(professor);
        }
    }

    private Professor getDataFromForm() {
        Professor professor = new Professor();
        String name = textFieldName.getText();
        String email = textFieldEmail.getText();
        String personalNumber = textFieldPersonalNumber.getText();
        if (name.length() > 255) {
            throw new IllegalArgumentException("El nombre no puede exceder los 255 caracteres. Se ingresaron "
                    + name.length() + " caracteres.");
        }
        if (email.length() > 255) {
            throw new IllegalArgumentException("La matrícula no puede exceder los 255 caracteres. Se ingresaron "
                    + email.length() + " caracteres.");
        }
        professor.setName(name);
        professor.setEmail(email);
        professor.setProfessorNumber(Integer.parseInt(personalNumber));
        return professor;
    }

    private void sendPasswordToUser(User user, String password) {
        Email email = new Email();
        String address = user.getEmail();
        String subject = "Usuario y contraseña de SGPGyER";
        String userLoggin = user.getEmail();
        String passwordUser = password;
        String content = email.formatMessageToUser(userLoggin, passwordUser);
        email.sendEmail(address, subject, content);
    }

    public void limitTextField(int limit) {
        UnaryOperator<TextFormatter.Change> textFilter = change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("^(?!0+$)[0-9]{0," + limit + "}$")) {
                DialogGenerator.getDialog("El número de personal solo puede contener números y tener una longitud de "
                        + "1 a 5 dígitos", Status.WARNING);
                return null;
            }
            int newLength = newText.length();
            if (newLength > limit) {
                int exceedLength = newLength - limit;
                int replaceEndIndex = change.getRangeEnd() - exceedLength;
                String trimmedText = newText.substring(0, limit);
                change.setText(trimmedText);
                change.setRange(change.getRangeStart(), replaceEndIndex);
                change.setCaretPosition(replaceEndIndex);
            }
            return change;
        };
        textFieldPersonalNumber.setTextFormatter(new TextFormatter<>(textFilter));
    }
}
