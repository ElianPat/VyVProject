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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mx.uv.fei.logic.ClassHolder;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.Status;

/**
 * FXML Controller class
 * Esta clase se encarga de validar o rechazar la propuesta de un 
 * anteproyecto (lo valida o rechaza el cuerpo académico).
 * @author Palom
 */
public class EvaluatePreliminaryProjectController implements Initializable {

    @FXML
    private Label labelDirector;
    @FXML
    private Label labelCodirector;
    @FXML
    private Label labelQuota;
    @FXML
    private Label labelAcademicGroup;
    @FXML
    private Label labelNameProject;
    @FXML
    private Label labelLgac;
    @FXML
    private Label labelDuration;
    @FXML
    private Label labelLineOfResearch;
    @FXML
    private Label labelAreaRecommendedBibliography;
    @FXML
    private Label labelAreaRequirements;
    @FXML
    private Label labelAreaDescription;
    @FXML
    private Label labelAreaResults;
    @FXML
    private Label labelAreaNotes;
    @FXML
    private Button buttonValidated;
    @FXML
    private Button buttonRejected;
    @FXML
    private ImageView imageViewExit;

    private PreliminaryProject preliminaryProject = new PreliminaryProject();
    private final ClassHolder classHolder = ClassHolder.getInstance();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initComponents(String title) throws DAOException {
        PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
        preliminaryProject = preliminaryProjectDAO.getInformation(title);
        setInformation(preliminaryProject);
        String preliminaryProjectState = classHolder.getPreliminaryProject().getState();
        if (preliminaryProjectState.equals("Validado") || preliminaryProjectState.equals("Rechazado")) {
            buttonRejected.setDisable(true);
            buttonValidated.setDisable(true);
        }
    }

    public void setInformation(PreliminaryProject preliminaryProject) {
        try {
            this.labelAcademicGroup.setText(String.format("%s", preliminaryProject.getAcademicGroupName()));
            this.labelNameProject.setText(preliminaryProject.getProjectName());
            this.labelDuration.setText(String.format("%s", preliminaryProject.getDuration()));
            this.labelLgac.setText(String.format("%s", preliminaryProject.getLgac()));
            this.labelQuota.setText(String.format("%s", preliminaryProject.getQuota()));
            this.labelLineOfResearch.setText(String.format("%s", preliminaryProject.getLineOfResearch()));
            this.labelDirector.setText(String.format("%s", preliminaryProject.getDirectorNumber()));
            this.labelCodirector.setText(String.format("%s", preliminaryProject.getCodirectorNumber()));
            this.labelAreaDescription.setText(String.format("%s", preliminaryProject.getDescription()));
            labelAreaDescription.setWrapText(true);
            this.labelAreaResults.setText(String.format("%s", preliminaryProject.getExpectedResults()));
            labelAreaResults.setWrapText(true);
            this.labelAreaRecommendedBibliography.setText(String.format("%s", preliminaryProject.getRecommendedBibliography()));
            labelAreaRecommendedBibliography.setWrapText(true);
            this.labelAreaRequirements.setText(preliminaryProject.getStudentRequirements());
            labelAreaRequirements.setWrapText(true);
            this.labelAreaNotes.setText(preliminaryProject.getNotes());
            labelAreaNotes.setWrapText(true);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @FXML
    private void validateOnAction(ActionEvent event) {
        try {
            PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
            preliminaryProjectDAO.updateStatePreliminaryProject("Validado", preliminaryProject.getProjectName());
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        DialogGenerator.getConfirmationDialog("El proyecto ha cambiado a estado validado correctamente", "Confirmación");
        Stage stage = (Stage) this.imageViewExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void rejectOnAction(ActionEvent event) {
        try {
            String title = this.labelNameProject.getText();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SendEmail.fxml"));
            Parent root = loader.load();
            SendEmailController controlador = loader.getController();
            controlador.initComponents(title);
            App.newView(root);
            Stage stage = (Stage) this.imageViewExit.getScene().getWindow();
            stage.close();
        } catch (IOException | DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    void exitOnAction(MouseEvent event) {
        Stage stage = (Stage) this.imageViewExit.getScene().getWindow();
        stage.close();
    }
}
