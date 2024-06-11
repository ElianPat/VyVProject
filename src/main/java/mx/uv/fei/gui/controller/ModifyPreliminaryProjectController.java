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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mx.uv.fei.logic.AcademicGroup;
import mx.uv.fei.logic.AcademicGroupDAO;
import mx.uv.fei.logic.ClassHolder;
import mx.uv.fei.logic.Codirector;
import mx.uv.fei.logic.CodirectorDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;

/**
 * FXML Controller class
 * Este controlador permite modificar la información de un anteproyecto.
 * @author Palom
 */
public class ModifyPreliminaryProjectController implements Initializable {

    @FXML
    private Label labelName;
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
    @FXML
    private ImageView imageViewExit;

    private ClassHolder preliminaryProjectHolder = ClassHolder.getInstance();
    private ClassHolder classHolder = ClassHolder.getInstance();
    private TableModifyPreliminaryProjectController homeDirector;
    private String namePreliminaryProject = "";
    private HomeAcademicGroupController homeAcademicGroup;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            Professor professor = new Professor();
            professor = professorDAO.getInformation(classHolder.getUser().getEmail());
            labelName.setText(String.format("%s", professor.getName()));
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

    public void setHomeDirector(TableModifyPreliminaryProjectController homeDirector) {
        this.homeDirector = homeDirector;
    }

    public void setHomeAcademicGroup(HomeAcademicGroupController homeAcademicGroup) {
        this.homeAcademicGroup = homeAcademicGroup;
    }

    @FXML
    private void exitOnAction(MouseEvent event) {
        Stage stage = (Stage) this.imageViewExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void modifyPreliminaryProjectOnAction(ActionEvent event) {
        try {
            PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
            String namePreliminaryProjectOriginal = preliminaryProjectHolder.getPreliminaryProject().getProjectName();
            namePreliminaryProject = textFieldProjectName.getText();
            if (namePreliminaryProject.equals(namePreliminaryProjectOriginal)) {
                if (preliminaryProjectDAO.updatePreliminaryProject(getDataFromForm(), 
                        namePreliminaryProjectOriginal) > 0) {
                    DialogGenerator.getConfirmationDialog("Anteproyecto modificado con exito", 
                            "Actualización exitosa");
                    if (homeDirector != null && homeAcademicGroup != null) {
                        homeDirector.updateTable();
                    }
                    Stage stage = (Stage) this.imageViewExit.getScene().getWindow();
                    stage.close();
                } else {
                    DialogGenerator.getDialog("No se pudo modificar el anteproyecto", Status.WARNING);
                }
            } else {
                if (preliminaryProjectDAO.isPreliminaryProjectExisting(namePreliminaryProject) == false) {
                    if (preliminaryProjectDAO.updatePreliminaryProject(getDataFromForm(), 
                            namePreliminaryProjectOriginal) > 0) {
                        DialogGenerator.getConfirmationDialog("Anteproyecto modificado con exito", 
                                "Actualizacion exitosa");
                        if (homeDirector != null && homeAcademicGroup != null) {
                            homeDirector.updateTable();
                        }
                        Stage stage = (Stage) this.imageViewExit.getScene().getWindow();
                        stage.close();
                    } else {
                        DialogGenerator.getDialog("No se pudo modificar el anteproyecto", Status.WARNING);
                    }
                } else {
                    DialogGenerator.getDialog("No puede duplicar nombres de anteproyectos", Status.WARNING);
                }
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void initComponents(String preliminaryProjectTitle, Professor professor) {
        try {
            PreliminaryProject preliminaryProject = new PreliminaryProject();
            PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
            preliminaryProject = preliminaryProjectDAO.getInformation(preliminaryProjectTitle);
            this.textFieldProjectName.setText(preliminaryProject.getProjectName());
            this.textAreaDescription.setText(String.format("%s", preliminaryProject.getDescription()));
            textAreaDescription.setWrapText(true);
            this.textAreaObjective.setText(String.format("%s", preliminaryProject.getExpectedResults()));
            textAreaObjective.setWrapText(true);
            this.textAreaRecommendedBibliography.setText(String.format("%s", 
                    preliminaryProject.getRecommendedBibliography()));
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
    }

    private PreliminaryProject getDataFromForm() throws DAOException {
        PreliminaryProject preliminaryProject = new PreliminaryProject();
        CodirectorDAO codirectorDAO = new CodirectorDAO();
        AcademicGroupDAO academicGroupDAO = new AcademicGroupDAO();

        preliminaryProject.setProjectName(textFieldProjectName.getText());
        preliminaryProject.setExpectedResults(textAreaObjective.getText());
        preliminaryProject.setLineOfResearch(textFieldLineOfResearch.getText());
        preliminaryProject.setStudentRequirements(textAreaStudentRequirements.getText());
        preliminaryProject.setDescription(textAreaDescription.getText());
        preliminaryProject.setLgac(comboBoxLgac.getSelectionModel().getSelectedItem());
        preliminaryProject.setRecommendedBibliography(textAreaRecommendedBibliography.getText());
        preliminaryProject.setDuration(comboBoxDuration.getSelectionModel().getSelectedItem());
        String nameCodirector = comboBoxCodirector.getSelectionModel().getSelectedItem().getName();
        int codirectorNumber = codirectorDAO.getCodirectorNumber(nameCodirector);
        preliminaryProject.setCodirectorNumber(codirectorNumber);
        String nameAcademicGroup = comboBoxAcademicGroup.getSelectionModel().getSelectedItem().getNameAcademicGroup();
        String keyAcademicGroup = academicGroupDAO.getKeyAcademicGroupByName(nameAcademicGroup);
        preliminaryProject.setKeyAcademicGroup(keyAcademicGroup);
        preliminaryProject.setQuota(comboBoxQuota.getSelectionModel().getSelectedItem());
        preliminaryProject.setNotes(textAreaNotes.getText());
        return preliminaryProject;
    }
}
