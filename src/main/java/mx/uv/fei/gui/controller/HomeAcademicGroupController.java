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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import mx.uv.fei.logic.AcademicGroupDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * Es la clase que contiene los controladores para el cuerpo académico, 
 * sirve para dividir los tabs que hay y no tener todos los controladores en 
 * una sola clase.
 * @author alexs
 */
public class HomeAcademicGroupController implements Initializable {

    @FXML
    private Label labelName;
    @FXML
    private TableView<PreliminaryProject> tableViewPreliminaryProject;
    @FXML
    private TableColumn<PreliminaryProject, String> columnPreliminaryProjectName;
    @FXML
    private TableColumn<PreliminaryProject, String> columnPreliminaryProjectState;

    private Professor professor = new Professor();
    private ObservableList<PreliminaryProject> observableListPreliminaryProject;
    private final ClassHolder classHolder = ClassHolder.getInstance();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnPreliminaryProjectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        columnPreliminaryProjectState.setCellValueFactory(new PropertyValueFactory<>("state"));
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

        AcademicGroupDAO academicGroupDAO = new AcademicGroupDAO();
        String keyAcademicGroup = academicGroupDAO.getKeyAcademicGroup(professor.getProfessorNumber());
        observableListPreliminaryProject = getTableViewPreliminaryProject(keyAcademicGroup);
        tableViewPreliminaryProject.setItems(observableListPreliminaryProject);
    }

    public ObservableList<PreliminaryProject> getTableViewPreliminaryProject(String keyAcademicGroup) {
        PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList();
        try {
            listOfPreliminaryProject = preliminaryProjectDAO.getPreliminaryProjectsForAcademicGroup(keyAcademicGroup);
        } catch (DAOException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        observableListPreliminaryProject = FXCollections.observableArrayList(listOfPreliminaryProject);
        return observableListPreliminaryProject;
    }

    @FXML
    private void selectedRow(MouseEvent event) {
        try {
            PreliminaryProject selectedPreliminaryProject = this.tableViewPreliminaryProject.getSelectionModel().getSelectedItem();
            if (selectedPreliminaryProject != null) {
                String preliminaryProjectName = this.tableViewPreliminaryProject.getSelectionModel().getSelectedItem().getProjectName();
                classHolder.setPreliminaryProject(selectedPreliminaryProject);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EvaluatePreliminaryProject.fxml"));
                Parent root = loader.load();
                EvaluatePreliminaryProjectController controlador = loader.getController();
                controlador.initComponents(preliminaryProjectName);
                App.newView(root);
                String email = classHolder.getUser().getEmail();
                initComponents(email);
            } else {
                DialogGenerator.getDialog("Selecciona una fila con información para poder ver sus detalles.", Status.WARNING);
            }
        } catch (IOException | DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
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

    @FXML
    private void updateTableOnMouseClicked(MouseEvent event) {
        try {
            String email = classHolder.getUser().getEmail();
            ProfessorDAO professorDAO = new ProfessorDAO();
            Professor professor = new Professor();
            professor = professorDAO.getInformation(email);
            
            AcademicGroupDAO academicGroupDAO = new AcademicGroupDAO();
            String keyAcademicGroup = academicGroupDAO.getKeyAcademicGroup(professor.getProfessorNumber());
            observableListPreliminaryProject = getTableViewPreliminaryProject(keyAcademicGroup);
            tableViewPreliminaryProject.setItems(observableListPreliminaryProject);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);  
        }
    }
}
