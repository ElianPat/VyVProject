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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * esta clase permite mostrar los anteproyectos asignados a un estudiante
 * para asi poder revisar sus avances hechos.
 * @author alexs
 */
public class ProgressPreliminaryProjectDirectorController implements Initializable {

    @FXML
    private TableView<Student> tableViewStudentPreliminaryProject;
    @FXML
    private ComboBox<PreliminaryProject> comboBoxPreliminaryProject;
    @FXML
    private TableColumn<Student, String> columnStudentEnrollment;
    @FXML
    private TableColumn<Student, String> columnStudentName;
    @FXML
    private TableColumn<Student, String> columnPreliminaryProjectName;

    private ObservableList<Student> observableListStudentPreliminaryProject;
    private Professor professor;
    private final ClassHolder classHolder = ClassHolder.getInstance();
    private List<Student> listOfPreliminaryProjectFilter = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnStudentEnrollment.setCellValueFactory(new PropertyValueFactory<>("enrollment"));
        columnStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnPreliminaryProjectName.setCellValueFactory(new PropertyValueFactory<>("preliminaryProjectName"));
        observableListStudentPreliminaryProject = getTableStudentPreliminaryProject();
        tableViewStudentPreliminaryProject.setItems(observableListStudentPreliminaryProject);
        try {
            String email = classHolder.getUser().getEmail();
            ProfessorDAO professorDAO = new ProfessorDAO();
            professor = professorDAO.getInformation(email);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        initComboBoxPreliminaryProject(professor.getProfessorNumber());
    }

    private ObservableList<Student> getTableStudentPreliminaryProject() {
        List<Student> listOfStudent = new ArrayList();
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            professor = professorDAO.getInformation(classHolder.getUser().getEmail());
            StudentDAO studentDAO = new StudentDAO();
            listOfStudent = studentDAO.getStudentsPreliminaryProject(professor.getProfessorNumber());
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Student> observableListStudent = FXCollections.observableArrayList(listOfStudent);
        return observableListStudent;
    }

    @FXML
    void setPreliminaryProjectOnAction(ActionEvent event) {
        PreliminaryProject selectedPreliminaryProject = comboBoxPreliminaryProject.getValue();
        if (selectedPreliminaryProject != null) {
            listOfPreliminaryProjectFilter = observableListStudentPreliminaryProject
                    .stream()
                    .filter(student -> student.getPreliminaryProjectName().equals(
                            selectedPreliminaryProject.getProjectName()))
                    .collect(Collectors.toList());
            tableViewStudentPreliminaryProject.setItems(FXCollections.observableArrayList(listOfPreliminaryProjectFilter));
        } else {
            tableViewStudentPreliminaryProject.setItems(observableListStudentPreliminaryProject);
        }
    }

    @FXML
    void selectedRow(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Student student = new Student();
            student = tableViewStudentPreliminaryProject.getSelectionModel().getSelectedItem();
            if (student != null) {
                classHolder.setStudent(student);
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShowAdvancePreliminaryProject.fxml"));
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
            }
        }
    }

    public void initComboBoxPreliminaryProject(int directorNumber) {
        PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList<>();
        try {
            listOfPreliminaryProject = preliminaryProjectDAO.getNameProjectPerDirector(directorNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog("No fue posible mostrar la lista de proyectos del director", Status.WARNING);
        }
        ObservableList<PreliminaryProject> observableListPreliminaryProject = 
                FXCollections.observableArrayList(listOfPreliminaryProject);
        comboBoxPreliminaryProject.setItems(observableListPreliminaryProject);
    }
}
