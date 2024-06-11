/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * Asigna anteproyectos registrados a estudiantes también previamente
 * registrados.
 * @author aresj
 */
public class AssignPreliminaryProjectController implements Initializable {

    @FXML
    private TableView<PreliminaryProject> tableViewPreliminaryProject;
    @FXML
    private TableColumn<PreliminaryProject, String> columnPreliminaryProjectName;
    @FXML
    private TableColumn<PreliminaryProject, String> columnPreliminaryProjectQuota;
    @FXML
    private Pane paneAssing;
    @FXML
    private TextField textFieldName;
    @FXML
    private TableView<Student> tableViewStudent;
    @FXML
    private TableColumn<Student, String> columnStudentName;
    @FXML
    private TableColumn<Student, String> columnStudentEnrollment;

    private ObservableList<Student> observableListStudents;
    private ObservableList<Student> observableListFilterStudents;
    private int directorNumber;
    private String directorEmail;
    private int idPreliminaryProject;

    ClassHolder classHolder = ClassHolder.getInstance();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        observableListStudents = FXCollections.observableArrayList();
        observableListFilterStudents = FXCollections.observableArrayList();
        columnPreliminaryProjectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        columnPreliminaryProjectQuota.setCellValueFactory(new PropertyValueFactory<>("quota"));
        tableViewPreliminaryProject.setItems(getTableProjectsForAssignment());
        paneAssing.setVisible(false);
    }

    private ObservableList<PreliminaryProject> getTableProjectsForAssignment() {
        PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList();
        ProfessorDAO professorDAO = new ProfessorDAO();
        try {
            directorEmail = classHolder.getUser().getEmail();
            directorNumber = professorDAO.getProfessorNumberByEmail(directorEmail);
            listOfPreliminaryProject = preliminaryProjectDAO.getPreliminaryProjectsForAssignament(directorNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<PreliminaryProject> observableListPreliminaryProject = FXCollections.observableArrayList(listOfPreliminaryProject);
        return observableListPreliminaryProject;
    }

    private ObservableList<Student> getTableStudentsForAssignament() {
        StudentDAO studentDAO = new StudentDAO();
        List<Student> listOfStudent = new ArrayList();
        try {
            listOfStudent = studentDAO.getStudentForAssign();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Student> observableListStudentsForAssignament = FXCollections.observableArrayList(listOfStudent);
        return observableListStudentsForAssignament;
    }

    @FXML
    private void selectedRow(MouseEvent event) throws DAOException {
        PreliminaryProject preliminaryProject = new PreliminaryProject();
        preliminaryProject = this.tableViewPreliminaryProject.getSelectionModel().getSelectedItem();
        if (preliminaryProject != null) {
            columnStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
            columnStudentEnrollment.setCellValueFactory(new PropertyValueFactory<>("enrollment"));
            tableViewStudent.setItems(getTableStudentsForAssignament());
            paneAssing.setVisible(true);
        } else {
            DialogGenerator.getDialog("Por favor selecciona un anteproyecto para asignarlo a un estudiante disponible", Status.WARNING);
        }
    }

    @FXML
    private void assignOnAction(ActionEvent event) {
        try {
            invokeAssing(getDataFromForm());
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private void invokeAssing(PreliminaryProject preliminaryProject) throws DAOException {
        Student student = new Student();
        StudentDAO studentDAO = new StudentDAO();
        PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
        student = this.tableViewStudent.getSelectionModel().getSelectedItem();
        if (student != null) {
            idPreliminaryProject = preliminaryProjectDAO.getIdPreliminaryProject(preliminaryProject.getProjectName());
            studentDAO.assignPreliminaryProjectStudent(idPreliminaryProject,
                    preliminaryProjectDAO.getReceptionalWorkWithPreliminaryProject(idPreliminaryProject),
                    student.getEnrollment());
            preliminaryProjectDAO.modifyQuotaPreliminaryProject(idPreliminaryProject);
            DialogGenerator.getConfirmationDialog("El anteproyecto fue asignado al estudiante",
                    "El anteproyecto fue asignado exitosamente");
            tableViewPreliminaryProject.setItems(getTableProjectsForAssignment());
            tableViewStudent.setItems(getTableStudentsForAssignament());
        } else {
            DialogGenerator.getDialog("Por favor selecciona un estudiante que se encuentre en espera de anteproyecto", Status.WARNING);
        }
    }

    private PreliminaryProject getDataFromForm() {
        PreliminaryProject preliminaryProject = this.tableViewPreliminaryProject.getSelectionModel().getSelectedItem();
        if (preliminaryProject.getQuota() == 0) {
            throw new IllegalArgumentException("El anteproyecto seleccionado no posee cupos, por favor selecciona otro o "
                    + "modificalo en la sección correspondiente");
        }
        return preliminaryProject;
    }

    @FXML
    private void filterName(KeyEvent event) {
        String filterName = this.textFieldName.getText();
        this.observableListStudents = getTableStudentsForAssignament();
        if (filterName.isEmpty()) {
            this.tableViewStudent.setItems(observableListStudents);
        } else {
            this.observableListFilterStudents.clear();
            for (Student student : this.observableListStudents) {
                if (student.getName().toLowerCase().contains(filterName.toLowerCase())) {
                    this.observableListFilterStudents.add(student);
                }
            }
            this.tableViewStudent.setItems(observableListFilterStudents);
        }
    }
}
