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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * esta clase muestra una tabla con los anteproyectos registrados para poder seleccionar
 * uno y así poder modificar su información.
 * @author Palom
 */
public class TableModifyPreliminaryProjectController implements Initializable {

    @FXML
    private Button buttonModify;
    @FXML
    private TableView<PreliminaryProject> tableViewPreliminaryProject;
    @FXML
    private TableColumn<PreliminaryProject, String> columnPreliminaryProjectTitle;
    @FXML
    private TableColumn<PreliminaryProject, String> columnPreliminaryProjectState;

    private ClassHolder classHolder = ClassHolder.getInstance();
    private ClassHolder preliminaryProjectHolder = ClassHolder.getInstance();
    private ObservableList<PreliminaryProject> observableListAdvance;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            columnPreliminaryProjectTitle.setCellValueFactory(new PropertyValueFactory<>("projectName"));
            columnPreliminaryProjectState.setCellValueFactory(new PropertyValueFactory<>("state"));
            observableListAdvance = getTableAdvance(professorDAO.getProfessorNumberByEmail(
                    classHolder.getUser().getEmail()));
            tableViewPreliminaryProject.setItems(observableListAdvance);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void modifyOnAction(ActionEvent event) throws IOException {
        PreliminaryProject preliminaryProject = new PreliminaryProject();
        preliminaryProject = this.tableViewPreliminaryProject.getSelectionModel().getSelectedItem();
        preliminaryProjectHolder.setPreliminaryProject(preliminaryProject);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyPreliminaryProject.fxml"));
            Parent root = loader.load();
            ModifyPreliminaryProjectController modifyPreliminaryProjectController = loader.getController();
            modifyPreliminaryProjectController.setHomeDirector(this);
            App.newView(root);
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    void selectedRow(MouseEvent event) {
        PreliminaryProject preliminaryProject = new PreliminaryProject();
        preliminaryProject = tableViewPreliminaryProject.getSelectionModel().getSelectedItem();
        if (preliminaryProject != null) {
            if (preliminaryProject.getState().equals("Validado")) {
                buttonModify.setDisable(true);
            }else{
                buttonModify.setDisable(false);
            }
        } else {
            DialogGenerator.getDialog("Debe que seleccionar un anteproyecto para poder modificarlo", Status.WARNING);
        }
    }

    public void updateTable() {
        try {
            observableListAdvance.clear();
            ProfessorDAO professorDAO = new ProfessorDAO();
            observableListAdvance.addAll(getTableAdvance(professorDAO.getProfessorNumberByEmail(
                    classHolder.getUser().getEmail())));
            tableViewPreliminaryProject.refresh();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public ObservableList<PreliminaryProject> getTableAdvance(int directorNumber) {
        PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList();
        try {
            listOfPreliminaryProject = preliminaryProjectDAO.getAllPreliminaryProject(directorNumber);
            ObservableList<PreliminaryProject> observableListPreliminaryProject = (ObservableList<PreliminaryProject>) 
                    FXCollections.observableArrayList(listOfPreliminaryProject);
            return observableListPreliminaryProject;
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<PreliminaryProject> observableListPreliminaryProject = (ObservableList<PreliminaryProject>) 
                FXCollections.observableArrayList(listOfPreliminaryProject);
        return observableListPreliminaryProject;
    }


    @FXML
    private void updateTableOnMouseClicked(MouseEvent event) {
        try {
            observableListAdvance.clear();
            ProfessorDAO professorDAO = new ProfessorDAO();
            observableListAdvance.addAll(getTableAdvance(professorDAO.getProfessorNumberByEmail(
                    classHolder.getUser().getEmail())));
            tableViewPreliminaryProject.refresh();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }
    
}
