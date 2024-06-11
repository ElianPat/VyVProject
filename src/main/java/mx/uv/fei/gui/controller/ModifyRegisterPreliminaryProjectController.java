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
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import mx.uv.fei.logic.AcademicGroup;
import mx.uv.fei.logic.AcademicGroupDAO;
import mx.uv.fei.logic.Codirector;
import mx.uv.fei.logic.CodirectorDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * permite modificar la información de un anteproyecto.
 * @author Palom
 */
public class ModifyRegisterPreliminaryProjectController implements Initializable {

    @FXML
    private TextField textFieldProjectName;
    @FXML
    private TextArea textAreaObjective;
    @FXML
    private TextArea textAreaDescription;
    @FXML
    private TextField textFieldLineOfResearch;
    @FXML
    private TextArea textAreaStudentRequirements;
    @FXML
    private TextArea textAreaRecommendedBibliography;
    @FXML
    private ComboBox<Integer> comboBoxQuota;
    @FXML
    private ComboBox<Codirector> comboBoxCodirector;
    @FXML
    private ComboBox<Integer> comboBoxDuration;
    @FXML
    private ComboBox<AcademicGroup> comboBoxAcademicGroup;
    @FXML
    private ComboBox<String> comboBoxLgac;
    @FXML
    private TextArea textAreaNotes;

    ClassHolder preliminaryProjectHolder = ClassHolder.getInstance();
    ClassHolder classHolder = ClassHolder.getInstance();
    private int personalNumber;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            Professor professor = new Professor();
            professor = professorDAO.getInformation(classHolder.getUser().getEmail());
            String preliminaryProjectTitle = preliminaryProjectHolder.getPreliminaryProject().getProjectName();
            initComponents(preliminaryProjectTitle, professor);
            initChoiceBoxDuration();
            initChoiceBoxQuota();
            initComboBoxCodirector();
            initComboBoxAcademicGroup();
            initChoiceBoxLgac();
            setStyleComponents();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void modifyPreliminaryProjectOnAction(ActionEvent event) {
        try {
            PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
            if (preliminaryProjectDAO.updatePreliminaryProject(getDataFromForm(), 
                    preliminaryProjectHolder.getPreliminaryProject().getProjectName()) > 0) {
                DialogGenerator.getConfirmationDialog("Anteproyecto modificado con exito", "Actualizacion exitosa");
            } else {
                DialogGenerator.getDialog("No se pudo modificar el anteproyecto", Status.WARNING);
            }
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    void exitOnAction(MouseEvent event) {
        try {
            App.openNewWindow("/fxml/HomeDirector.fxml");
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void initComponents(String preliminaryProjectTitle, Professor professor) {
        try {
            personalNumber = professor.getProfessorNumber();
            PreliminaryProject preliminaryProject = new PreliminaryProject();
            PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
            preliminaryProject = preliminaryProjectDAO.getInformation(preliminaryProjectTitle);
            this.textFieldProjectName.setText(preliminaryProject.getProjectName());
            this.textAreaDescription.setText(String.format("%s", preliminaryProject.getDescription()));
            textAreaDescription.setWrapText(true);
            this.textAreaObjective.setText(String.format("%s", preliminaryProject.getExpectedResults()));
            textAreaObjective.setWrapText(true);
            this.textAreaRecommendedBibliography.setText(String.format("%s", preliminaryProject.getRecommendedBibliography()));
            textAreaRecommendedBibliography.setWrapText(true);
            this.textAreaStudentRequirements.setText(preliminaryProject.getStudentRequirements());
            this.textAreaNotes.setText(preliminaryProject.getNotes());
            textAreaNotes.setWrapText(true);
            this.comboBoxDuration.setValue(preliminaryProject.getDuration());
            this.comboBoxLgac.setValue(preliminaryProject.getLgac());
            this.comboBoxQuota.setValue(preliminaryProject.getQuota());
            this.textFieldLineOfResearch.setText(preliminaryProject.getLineOfResearch());

            List<Codirector> listOfCodirector = new ArrayList<>();
            Codirector codirector = new Codirector();
            codirector.setName(preliminaryProject.getCodirectorName());
            listOfCodirector.add(codirector);
            this.comboBoxCodirector.getSelectionModel().select(codirector);

            List<AcademicGroup> listOfAcademicGroup = new ArrayList<>();
            AcademicGroup academicGroup = new AcademicGroup();
            academicGroup.setNameAcademicGroup(preliminaryProject.getAcademicGroupName());
            listOfAcademicGroup.add(academicGroup);
            this.comboBoxAcademicGroup.getSelectionModel().select(academicGroup);

        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void setStyleComponents() {
        textAreaObjective.setStyle("-fx-control-inner-background: #C6F3FA;-fx-border-color: #004aad;");
        textAreaDescription.setStyle("-fx-control-inner-background: #C6F3FA;-fx-border-color: #004AAD;");
        textAreaRecommendedBibliography.setStyle("-fx-control-inner-background: #C6F3FA;-fx-border-color: #004AAD;");
        textAreaStudentRequirements.setStyle("-fx-control-inner-background: #C6F3FA;-fx-border-color: #004AAD;");
        textAreaNotes.setStyle("-fx-control-inner-background: #C6F3FA;-fx-border-color: #004AAD;");
    }

    public void initChoiceBoxDuration() {
        Integer[] duration = {6, 12, 18, 24};
        comboBoxDuration.getItems().addAll(duration);
    }

    public void initChoiceBoxQuota() {
        Integer[] quota = {1, 2, 3, 4};
        comboBoxQuota.getItems().addAll(quota);
    }

    public void initChoiceBoxLgac() {
        String[] lgac = {"Tecnologías de Software",
            "Gestión, modelado y desarrollo de software", "Modelado, Desarrollo y Gestión del Software",
            "Aprendizaje Computacional"};
        comboBoxLgac.getItems().addAll(lgac);
    }

    public void initComboBoxCodirector() {
        try {
            CodirectorDAO coDirectorDAO = new CodirectorDAO();
            List<Codirector> listOfCodirector = new ArrayList<>();

            listOfCodirector = coDirectorDAO.getAllCodirector();

            ObservableList<Codirector> observableListCodirector = FXCollections.observableArrayList(listOfCodirector);
            comboBoxCodirector.setItems(observableListCodirector);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }

    }

    public void initComboBoxAcademicGroup() {
        try {
            AcademicGroupDAO academicGroupDAO = new AcademicGroupDAO();
            List<AcademicGroup> listOfAcademicGroup = new ArrayList<>();

            listOfAcademicGroup = academicGroupDAO.getAllAcademicGroup();

            ObservableList<AcademicGroup> observableListAcademicGroup = FXCollections.observableArrayList(listOfAcademicGroup);
            comboBoxAcademicGroup.setItems(observableListAcademicGroup);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }

    }

    private PreliminaryProject getDataFromForm() {
        PreliminaryProject preliminaryProject = new PreliminaryProject();

        preliminaryProject.setProjectName(textFieldProjectName.getText());
        preliminaryProject.setExpectedResults(textAreaObjective.getText());
        preliminaryProject.setLineOfResearch(textFieldLineOfResearch.getText());
        preliminaryProject.setStudentRequirements(textAreaStudentRequirements.getText());
        preliminaryProject.setDescription(textAreaDescription.getText());
        preliminaryProject.setLgac(comboBoxLgac.getSelectionModel().getSelectedItem());
        preliminaryProject.setRecommendedBibliography(textAreaRecommendedBibliography.getText());
        preliminaryProject.setDuration(comboBoxDuration.getSelectionModel().getSelectedItem());
        preliminaryProject.setCodirectorNumber(comboBoxCodirector.getSelectionModel().getSelectedItem().getCodirectorNumber());
        preliminaryProject.setDirectorNumber(personalNumber);
        preliminaryProject.setKeyAcademicGroup(comboBoxAcademicGroup.getSelectionModel().getSelectedItem().getKey());
        preliminaryProject.setQuota(comboBoxQuota.getSelectionModel().getSelectedItem());
        preliminaryProject.setNotes(textAreaNotes.getText());

        return preliminaryProject;
    }
}
