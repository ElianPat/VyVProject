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
import mx.uv.fei.logic.AcademicGroup;
import mx.uv.fei.logic.AcademicGroupDAO;
import mx.uv.fei.logic.Codirector;
import mx.uv.fei.logic.CodirectorDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * esta clase permite crear anteproyectos.
 * @author alexs
 */
public class RegisterPreliminaryProjectController implements Initializable {

    @FXML
    private TextArea textAreaRecommendedBibliography;
    @FXML
    private TextField textFieldProjectName;
    @FXML
    private TextArea textAreaNotes;
    @FXML
    private TextArea textAreaObjective;
    @FXML
    private TextField textFieldLineOfResearch;
    @FXML
    private TextArea textAreaStudentRequirements;
    @FXML
    private TextArea textAreaDescription;
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

    ClassHolder classHolder = ClassHolder.getInstance();
    private final int QUOTA = 4;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboBoxDuration();
        initComboBoxQuota();
        initComboBoxCoDirector();
        initComboBoxAcademicGroup();
        initComboBoxLgac();
    }

    @FXML
    private void createOnAction(ActionEvent event) {
        try {
            invokePreliminaryProjectRegistration(getDataFromForm());
        } catch (DAOException ex) {
            DialogGenerator.getDialog(ex.getMessage(), ex.getStatus());
        } catch (IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void setStyleComponents() {
        textFieldLineOfResearch.setStyle("-fx-control-inner-background: #0B5569;-fx-prompt-text-fill: white;");
        textFieldProjectName.setStyle("-fx-control-inner-background: #0B5569;-fx-prompt-text-fill: white;");
        textAreaObjective.setStyle("-fx-control-inner-background: #0B5569;-fx-prompt-text-fill: white;");
        textAreaDescription.setStyle("-fx-control-inner-background: #0B5569;-fx-prompt-text-fill: white;");
        textAreaRecommendedBibliography.setStyle("-fx-control-inner-background: #0B5569;-fx-prompt-text-fill: white;");
        textAreaStudentRequirements.setStyle("-fx-control-inner-background: #0B5569;-fx-prompt-text-fill: white;");
        textAreaNotes.setStyle("-fx-control-inner-background: #0B5569;-fx-prompt-text-fill: white;");
    }

    public void initComboBoxDuration() {
        ObservableList<Integer> month = FXCollections.observableArrayList(6, 12, 18, 24);
        comboBoxDuration.setItems(month);
        comboBoxDuration.getSelectionModel().select(0);
    }

    public void initComboBoxQuota() {
        List<Integer> numbersQuota = new ArrayList<>();
        for (int i = 1; i <= QUOTA; i++) {
            numbersQuota.add(i);
        }
        ObservableList<Integer> quota = FXCollections.observableArrayList(numbersQuota);
        comboBoxQuota.setItems(quota);
        comboBoxQuota.getSelectionModel().select(0);
    }

    public void initComboBoxLgac() {
        ObservableList<String> lgac = FXCollections.observableArrayList("Tecnologías de Software",
                "Gestión, modelado y desarrollo de software", "Modelado, Desarrollo y Gestión del Software",
                "Aprendizaje Computacional");
        comboBoxLgac.setItems(lgac);
        comboBoxLgac.getSelectionModel().select(0);
    }

    public void initComboBoxCoDirector() {
        CodirectorDAO coDirectorDAO = new CodirectorDAO();
        List<Codirector> listOfCodirector = new ArrayList<>();
        try {
            listOfCodirector = coDirectorDAO.getAllCodirector();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog("No fue posible mostrar la lista de codirectores", Status.WARNING);
        }
        ObservableList<Codirector> getAllCoDirector = FXCollections.observableArrayList(listOfCodirector);
        comboBoxCodirector.setItems(getAllCoDirector);
        comboBoxCodirector.getSelectionModel().select(0);
    }

    public void initComboBoxAcademicGroup() {
        AcademicGroupDAO academicGroupDAO = new AcademicGroupDAO();
        List<AcademicGroup> listOfAcademicGroup = new ArrayList<>();
        try {
            listOfAcademicGroup = academicGroupDAO.getAllAcademicGroup();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog("No fue posible mostrar la lista del grupo academico", Status.WARNING);
        }
        ObservableList<AcademicGroup> getAllAcademicGroup = FXCollections.observableArrayList(listOfAcademicGroup);
        comboBoxAcademicGroup.setItems(getAllAcademicGroup);
        comboBoxAcademicGroup.getSelectionModel().select(0);
    }

    private void invokePreliminaryProjectRegistration(PreliminaryProject preliminaryProject) throws DAOException {
        PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
        try {
            if (preliminaryProjectDAO.isPreliminaryProjectExisting(preliminaryProject.getProjectName())==false) {
                if (preliminaryProjectDAO.createPreliminaryProject(preliminaryProject) > 0) {
                    DialogGenerator.getDialog("Anteproyecto registrado", Status.SUCCESS);
                } else {
                    DialogGenerator.getDialog("No fue posible registrar el anteproyecto", Status.WARNING);
                }
            } else {
                DialogGenerator.getDialog("No puede duplicar nombres de anteproyectos", Status.WARNING);
            }
          
        } catch (IllegalArgumentException ex) {
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
        preliminaryProject.setDirectorNumber(classHolder.getDirector().getDirectorNumber());
        preliminaryProject.setKeyAcademicGroup(comboBoxAcademicGroup.getSelectionModel().getSelectedItem().getKey());
        preliminaryProject.setQuota(comboBoxQuota.getSelectionModel().getSelectedItem());
        preliminaryProject.setNotes(textAreaNotes.getText());
        return preliminaryProject;
    }
}