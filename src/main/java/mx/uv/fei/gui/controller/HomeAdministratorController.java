/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import mx.uv.fei.logic.Status;

/**
 * FXML Controller class
 * Esta clase aloja los componentes que se encargará de realizar las acciones
 * para el administrador, sirve como contenedor.
 * @author Palom
 */
public class HomeAdministratorController implements Initializable {

    @FXML
    private BorderPane borderPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showFXML("AdminImage");
    }

    private void showFXML(String fxml) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/" + fxml + ".fxml"));
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        borderPane.setCenter(root);
    }

    @FXML
    private void registerStudentOnAction(ActionEvent event) {
        showFXML("RegisterStudents");
    }

    @FXML
    private void modifyStudentOnAction(ActionEvent event) {
        showFXML("ShowStudentsForModify");
    }

    @FXML
    private void registerProfessorOnAction(ActionEvent event) {
        showFXML("RegisterProfessor");
    }

    @FXML
    private void generateReportOnAction(ActionEvent event) {
        showFXML("AdminReport");
    }

    @FXML
    private void modifyProfessorOnAction(ActionEvent event) {
        showFXML("ModifyProfessor");
    }

    @FXML
    private void registerEducativeExperienceOnAction(ActionEvent event) {
        showFXML("RegisterEducativeExperience");

    }

    @FXML
    private void registerAcademicGroupOnAction(ActionEvent event) {
        showFXML("RegisterAcademicGroup");
    }

    @FXML
    private void modifyAcademicGroupOnAction(ActionEvent event) {
        showFXML("ShowAcademicGroupForModify");
    }

    @FXML
    private void registerCourseOnAction(ActionEvent event) {
        showFXML("RegisterCourse");
    }

    @FXML
    private void modifyCourseOnAction(ActionEvent event) {
        showFXML("ModifyCourse");
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
    private void returnHomeOnAction(MouseEvent event) {
        showFXML("AdminImage");
    }
}
