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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.ClassHolder;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;

/**
 * FXML Controller class
 * Esta clase sirve como contenedor para otros controladores y fxml, ya que
 * es para no tener todos los controladores en uno solo.
 * @author alexs
 */
public class HomeCodirectorController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label labelName;
    @FXML
    private TableView<Student> tableViewStudentPreliminaryProject;
    @FXML
    private TableColumn<Student, String> columnStudentEnrollment;
    @FXML
    private TableColumn<Student, String> columnStudentName;
    @FXML
    private TableColumn<Student, String> columnPreliminaryProjectName;
    @FXML
    private TableColumn<Student, String> columnEducativeExperienceName;
    @FXML
    private TextField textFieldName;

    private ObservableList<Student> observableListStudentPreliminaryProject;
    private ObservableList<Student> observableListFilterStudents;
    private Professor professor = new Professor();
    private final ClassHolder classHolder = ClassHolder.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        observableListStudentPreliminaryProject = FXCollections.observableArrayList();
        observableListFilterStudents = FXCollections.observableArrayList();
        columnStudentEnrollment.setCellValueFactory(new PropertyValueFactory<>("enrollment"));
        columnStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnPreliminaryProjectName.setCellValueFactory(new PropertyValueFactory<>("preliminaryProjectName"));
        columnEducativeExperienceName.setCellValueFactory(new PropertyValueFactory<>("educativeExperience"));
        observableListStudentPreliminaryProject = getTableStudentPreliminaryProject();
        tableViewStudentPreliminaryProject.setItems(observableListStudentPreliminaryProject);
        String email = classHolder.getUser().getEmail();
        try {
            initComponents(email);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void initComponents(String email) throws DAOException {
        ProfessorDAO professorDAO = new ProfessorDAO();
        professor = professorDAO.getInformation(email);
        labelName.setText(String.format("%s", professor.getName().split(" ")));
    }

    private ObservableList<Student> getTableStudentPreliminaryProject() {
        List<Student> listOfStudent = new ArrayList();
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            professor = professorDAO.getInformation(classHolder.getUser().getEmail());
            StudentDAO studentDAO = new StudentDAO();
            listOfStudent = studentDAO.getStudentsPreliminaryProjectCodirector(professor.getProfessorNumber());
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Student> observableListStudent = FXCollections.observableArrayList(listOfStudent);
        return observableListStudent;
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

    @FXML
    private void generateReportOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CodirectorReport.fxml"));
            Parent root = loader.load();
            CodirectorReportController controlador = loader.getController();
            controlador.initComponents(classHolder.getUser().getEmail());
            App.newView(root);
        } catch (IOException | DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void filterName(KeyEvent event) {
        String filterName = this.textFieldName.getText();
        this.observableListStudentPreliminaryProject = getTableStudentPreliminaryProject();
        if (filterName.isEmpty()) {
            this.tableViewStudentPreliminaryProject.setItems(observableListStudentPreliminaryProject);
        } else {
            this.observableListFilterStudents.clear();
            for (Student student : this.observableListStudentPreliminaryProject) {
                if (student.getPreliminaryProjectName().toLowerCase().contains(filterName.toLowerCase())) {
                    this.observableListFilterStudents.add(student);
                }
            }
            this.tableViewStudentPreliminaryProject.setItems(observableListFilterStudents);
        }
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
