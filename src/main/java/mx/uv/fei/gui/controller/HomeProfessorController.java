/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.fei.logic.Course;
import mx.uv.fei.logic.CourseDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * Sirve para contener otros controladores y así evitar tener varios
 * componentes en un solo controlador dado los tabs.
 * @author alexs
 */
public class HomeProfessorController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label labelName;
    @FXML
    private TableView<Student> tableViewStudent;
    @FXML
    private ComboBox<Course> comboBoxNrc;
    @FXML
    private TableColumn<Student, String> columnStudentName;
    @FXML
    private TableColumn<Student, String> columnStudentEnrollment;
    @FXML
    private TableColumn<Student, String> columnStudentNrc;

    private ObservableList<Student> observableListStudent;
    private int personalNumber;
    private Professor professor = new Professor();
    private final ClassHolder classHolder = ClassHolder.getInstance();
    private List<Student> listOfStudentFilter = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String email = classHolder.getUser().getEmail();
        try {
            initComponents(email);
            columnStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
            columnStudentEnrollment.setCellValueFactory(new PropertyValueFactory<>("enrollment"));
            columnStudentNrc.setCellValueFactory(new PropertyValueFactory<>("nrcCourse"));
            observableListStudent = getTableStudents();
            tableViewStudent.setItems(observableListStudent);
            initComboBoxCourse();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private ObservableList<Student> getTableStudents() {
        StudentDAO studentDAO = new StudentDAO();
        List<Student> listOfStudent = new ArrayList();
        ProfessorDAO professorDAO = new ProfessorDAO();
        try {
            personalNumber = professorDAO.getProfessorNumberByEmail(classHolder.getUser().getEmail());
            listOfStudent = studentDAO.getStudentsFromCourse(personalNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Student> getAllStudents = FXCollections.observableArrayList(listOfStudent);
        return getAllStudents;
    }

    public void initComponents(String email) throws DAOException {
        ProfessorDAO professorDAO = new ProfessorDAO();
        professor = professorDAO.getInformation(email);
        labelName.setText(String.format("%s", professor.getName().split(" ")));
    }

    @FXML
    private void generateReportOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProfessorReport.fxml"));
            Parent root = loader.load();
            ProfessorReportController controlador = loader.getController();
            controlador.initComponents(classHolder.getUser().getEmail());
            App.newView(root);
        } catch (IOException | DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void selectedRow(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Student student = new Student();
            student = tableViewStudent.getSelectionModel().getSelectedItem();
            if (student != null) {
                classHolder.setStudent(student);
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShowAvanceStudents.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException ex) {
                    App.getLogger().warn(ex.getMessage());
                    DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
                }
            } else {
                DialogGenerator.getDialog("Por favor selecciona a un estudiante para ver sus avances", Status.WARNING);
            }
        }
    }

    @FXML
    private void nrcOnAction(ActionEvent event) {
        Course selectedCourse = comboBoxNrc.getValue();
        if (selectedCourse != null) {
            listOfStudentFilter = observableListStudent.stream().filter(student -> student.getNrcCourse().equals(
                    selectedCourse.getNrc())).collect(Collectors.toList());
            tableViewStudent.setItems(FXCollections.observableArrayList(listOfStudentFilter));
        } else {
            tableViewStudent.setItems(observableListStudent);
        }
    }

    public void initComboBoxCourse() {
        CourseDAO course = new CourseDAO();
        List<Course> listOfNrc = new ArrayList();
        try {
            listOfNrc = course.getCoursesFromProfessor(personalNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Course> getNRC = FXCollections.observableArrayList(listOfNrc);
        comboBoxNrc.setItems(getNRC);
    }

    @FXML
    void exitOnAction(MouseEvent event) {
        try {
            App.openNewWindow("/fxml/Login.fxml");
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }
}
