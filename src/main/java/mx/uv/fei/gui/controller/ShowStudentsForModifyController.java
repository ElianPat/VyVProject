/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.MouseEvent;
import mx.uv.fei.logic.Course;
import mx.uv.fei.logic.CourseDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.User;
import mx.uv.fei.logic.UserDAO;

/**
 * FXML Controller class
 * muesta un listado de los estudiantes registrados para poder seleccionar
 * un estudiante y así poder modificar su información.
 * @author aresj
 */
public class ShowStudentsForModifyController implements Initializable {

    @FXML
    private TableView<Student> tableViewStudents;
    @FXML
    private TableColumn<Student, String> columnStudentName;
    @FXML
    private TableColumn<Student, String> columnStudentEnrollment;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldEnrollment;
    @FXML
    private ComboBox<Course> comboBoxNrc;
    @FXML
    private CheckBox checkBoxEnable;
    @FXML
    private Button buttonModifyStudent;

    private ObservableList<Student> observableListStudent;
    private String firstEmail;
    private String firstEnrollment;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnStudentEnrollment.setCellValueFactory(new PropertyValueFactory<>("enrollment"));
        observableListStudent = getTableStudents();
        tableViewStudents.setItems(observableListStudent);
        initComboBoxCourse();
        limitTextField(9);
    }

    private ObservableList<Student> getTableStudents() {
        StudentDAO studentDAO = new StudentDAO();
        List<Student> listOfStudent = new ArrayList();
        try {
            listOfStudent = studentDAO.getAllStudent();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Student> observableListStudentName = (ObservableList<Student>) 
                FXCollections.observableArrayList(listOfStudent);
        return observableListStudentName;
    }

    @FXML
    private void selectedRow(MouseEvent event) throws SQLException {
        Student student = new Student();
        StudentDAO studentDAO = new StudentDAO();
        student = this.tableViewStudents.getSelectionModel().getSelectedItem();
        if (student != null) {
            try {
                String enrollment = this.tableViewStudents.getSelectionModel().getSelectedItem().getEnrollment();
                student = studentDAO.getInfoToModifyStudent(enrollment);
                initComponents(student);
                buttonModifyStudent.setDisable(false);
            } catch (DAOException ex) {
                App.getLogger().error(ex.getMessage());
                DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
            }
        }
    }

    public void initComponents(Student student) {
        firstEmail = student.getEmail();
        Course course = new Course();
        this.textFieldName.setText(student.getName());
        this.textFieldEnrollment.setText(student.getEnrollment());
        firstEnrollment = student.getEnrollment();
        if (student.getEnable() > 0) {
            checkBoxEnable.setSelected(true);
        } else {
            checkBoxEnable.setSelected(false);
        }
        course.setNrc(student.getNrcCourse());
        this.comboBoxNrc.getSelectionModel().select(course);
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

    @FXML
    private void modifyStudentOnAction(ActionEvent event) {
        try {
            UserDAO userDAO = new UserDAO();
            Student student = new Student();
            student = getDataFromForm();
            if (firstEmail.equals(student.getEmail())) {
                invokeModify(getDataFromForm());
            } else {
                if (userDAO.isUserExisting(student.getEmail()) == false) {
                    invokeModify(getDataFromForm());
                } else {
                    DialogGenerator.getConfirmationDialog("Matricula registrado previamente en el sistema", 
                            "Duplicidad de datos");
                }
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private void invokeModify(Student student) throws DAOException {
        StudentDAO studentDAO = new StudentDAO();
        User user = new User();
        UserDAO userDAO = new UserDAO();
        Student selectedStudent = this.tableViewStudents.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            DialogGenerator.getDialog("Debes seleccionar una persona para modificar", Status.WARNING);
        } else {
            user.setName(student.getName());
            user.setEmail(student.getEmail());
            user.setEnable(student.getEnable());
            String email = firstEmail;
            if (userDAO.modifyUser(user, email) > 0 && studentDAO.modifyStudent(student, firstEnrollment) > 0) {
                DialogGenerator.getConfirmationDialog("Los datos del usuario han sido actualizados exitosamente", 
                        "Usuario modificado");
                this.tableViewStudents.setItems(getTableStudents());
                buttonModifyStudent.setDisable(true);
            } else {
                DialogGenerator.getDialog("Error al modificar el estudiante", Status.WARNING);
            }
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
        student.setEnrollment(enrollment);
        student.setEmail(student.makeEmail(student.getEnrollment()));
        student.setNrcCourse(comboBoxNrc.getSelectionModel().getSelectedItem().getNrc());
        if (checkBoxEnable.isSelected()) {
            student.setEnable(1);
        } else {
            student.setEnable(0);
        }
        return student;
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
