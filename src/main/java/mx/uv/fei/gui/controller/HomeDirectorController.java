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
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * Esta clase sirve como contenedor, sirve para separar los tabs.
 * @author alexs
 */
public class HomeDirectorController implements Initializable {

    @FXML
    private Label labelName;
    @FXML
    private BorderPane borderPane;

    private Professor professor = new Professor();
    private final ClassHolder classHolder = ClassHolder.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
    private void generateReportOnAction(ActionEvent event) {
        showFXML("DirectorReport");
    }

    @FXML
    private void registerReceptionalWorkOnAction(ActionEvent event) {
        showFXML("RegisterReceptionalWork");
    }

    @FXML
    private void modifyReceptionalWorkOnAction(ActionEvent event) {
        showFXML("ModifyReceptionalWork");
    }

    @FXML
    private void assingPreliminaryProjectOnAction(ActionEvent event) {
        showFXML("AssignPreliminaryProject");
    }

    @FXML
    private void modifyPreliminaryProjectOnAction(ActionEvent event) {
        showFXML("TableModifyPreliminaryProject");
    }

    @FXML
    void showAdvanceOnAction(ActionEvent event) {
        showFXML("ProgressPreliminaryProjectDirector");
    }

    @FXML
    void registerPreliminaryProjectOnAction(ActionEvent event) {
        showFXML("RegisterPreliminaryProject");
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
