/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import mx.uv.fei.logic.Course;
import mx.uv.fei.logic.CourseDAO;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Email;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.User;
import mx.uv.fei.logic.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

/**
 * FXML Controller class
 * esta clase permite registrar nuevos estudiantes.
 * @author aresj
 */
public class RegisterStudentsController implements Initializable {

    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldEnrollment;
    @FXML
    private ComboBox<Course> comboBoxNrc;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboBoxCourse();
        limitTextField(9);
    }

    @FXML
    private void registerStudentOnAction(ActionEvent event) {    
        try {
            invokeRegistration(getDataFromForm());
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }
    
    private void invokeRegistration(Student student) throws DAOException{
        User user = new User();
        UserDAO userDAO = new UserDAO();
        StudentDAO studentDAO = new StudentDAO();
        if (userDAO.isUserExisting(student.getEmail()) == false) {
            String password = userDAO.generatePassword();
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
            user.setName(student.getName());
            user.setEmail(student.getEmail());
            user.setPassword(passwordHash);
            if (userDAO.addUser(user) > 0 && studentDAO.registerStudent(student) > 0) {         
                sendPasswordToUser(user, password);
                DialogGenerator.getDialog("Registro realizado exitosamente", Status.SUCCESS);
            }else{
                DialogGenerator.getDialog("No se pudo registrar al estudiante", Status.WARNING);
            }
        }else{
            DialogGenerator.getConfirmationDialog("Matricula registrado previamente en el sistema", 
                    "Duplicidad de datos");
        }
    }
    
    private Student getDataFromForm() {
        Student student = new Student();
        String name = textFieldName.getText();
        String enrollment = textFieldEnrollment.getText();
        if (name.length() > 255) {
            throw new IllegalArgumentException("El nombre no puede exceder los 255 caracteres. Se ingresaron " 
                    + name.length() + " caracteres.");
        }
        if (enrollment.length() > 255) {
            throw new IllegalArgumentException("La matrícula no puede exceder los 255 caracteres. Se ingresaron " 
                    + enrollment.length() + " caracteres.");
        }
        student.setName(name);
        student.setEmail(student.makeEmail(enrollment));
        student.setEnrollment(enrollment);
        Course selectedCourse = comboBoxNrc.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            student.setNrcCourse(selectedCourse.toString());
        } else {
            throw new IllegalArgumentException("Debe seleccionar un curso.");
        }
        return student;
    }

    public void initComboBoxCourse() {
        CourseDAO course = new CourseDAO();
        List<Course> listOfNrc = new ArrayList();
        try {
            listOfNrc = course.getAllNrc();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Course> observableListCourse = FXCollections.observableArrayList(listOfNrc);
        comboBoxNrc.setItems(observableListCourse);
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
        UnaryOperator<Change> textLimitFilter = change -> {
            if (change.isContentChange()) {
                int newLength = change.getControlNewText().length();
                if (newLength > limit) {
                    String trimmedText = change.getControlNewText().substring(0, limit);
                    change.setText(trimmedText);
                    int oldLength = change.getControlText().length();
                    change.setRange(0, oldLength);
                }
            }
            return change;
        };
        textFieldEnrollment.setTextFormatter(new TextFormatter(textLimitFilter));
    }
}
