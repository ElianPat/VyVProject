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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import javafx.scene.control.DateCell;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.ReceptionalWork;
import mx.uv.fei.logic.ReceptionalWorkDAO;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * esta clase registra trabajos recepcionales.
 * @author aresj
 */
public class RegisterReceptionalWorkController implements Initializable {

    @FXML
    private TextField textFieldReceptionalWorkName;
    @FXML
    private DatePicker datePickerDateStart;
    @FXML
    private ComboBox<PreliminaryProject> comboBoxPreliminaryProject;
    @FXML
    private ComboBox<Professor> comboBoxSynodal;

    private int directorNumber;
    ClassHolder classHolder = ClassHolder.getInstance();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            directorNumber = professorDAO.getProfessorNumberByEmail(classHolder.getUser().getEmail());
            initComboBoxPreliminaryProject();
            initComboBoxSynodal();
            validateDatePicker();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void registerReceptionalWorkOnAction(ActionEvent event) {
        try {
            invokeRegistration(getDataFromForm());
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private void invokeRegistration(ReceptionalWork receptionalWork) throws DAOException {
        Professor selectedSynodal = comboBoxSynodal.getSelectionModel().getSelectedItem();
        PreliminaryProject selectedPreliminaryProject = comboBoxPreliminaryProject.getSelectionModel().getSelectedItem();
        if (selectedSynodal == null) {
            throw new IllegalArgumentException("Debe seleccionar un Sinodal para el trabajo recepcional.");
        } else if (selectedPreliminaryProject == null) {
            throw new IllegalArgumentException("Debe seleccionar un anteproyecto para el trabajo recepcional.");
        } else if (receptionalWork.getStartDate() == null) {
            throw new IllegalArgumentException("Debe seleccionar la fecha en la cual iniciará el trabajo recepcional.");
        } else {
            LocalDate startDate = datePickerDateStart.getValue();
            LocalDate currentDate = LocalDate.now();
            if (startDate.isBefore(currentDate)) {
                DialogGenerator.getDialog("La fecha de inicio debe ser posterior a la fecha actual", Status.WARNING);
            } else {
                String nameSynodal = selectedSynodal.toString();
                String namePreliminaryProject = selectedPreliminaryProject.toString();
                ReceptionalWorkDAO receptionalWorkDAO = new ReceptionalWorkDAO();
                PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
                ProfessorDAO professorDAO = new ProfessorDAO();

                if (receptionalWorkDAO.isReceptionalWorkIsExisting(textFieldReceptionalWorkName.getText(),
                        directorNumber) == false) {
                    int preliminaryProjectId = preliminaryProjectDAO.getIdPreliminaryProject(
                            namePreliminaryProject);
                    int synodalNumber = professorDAO.getProfessorNumber(nameSynodal);
                    receptionalWorkDAO.registerReceptionalWork(receptionalWork, preliminaryProjectId,
                            directorNumber, synodalNumber);
                    DialogGenerator.getConfirmationDialog("El Trabajo Recepcional ha sido registrado exitosamente, "
                            + "Haz clic en el botón para continuar", "Trabajo Recepcional registrado");
                    initComboBoxPreliminaryProject();
                } else {
                    DialogGenerator.getDialog("Trabajo recepcional previamente registrado en el sistema",
                            Status.WARNING);
                }
            }
        }
    }

    private void validateDatePicker() {
        datePickerDateStart.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });
        datePickerDateStart.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBefore(LocalDate.now())) {
                datePickerDateStart.setValue(LocalDate.now());
            }
        });
    }

    private ReceptionalWork getDataFromForm() {
        ReceptionalWork receptionalWork = new ReceptionalWork();
        String nameReceptionalWork = textFieldReceptionalWorkName.getText();
        if (nameReceptionalWork.length() > 255) {
            throw new IllegalArgumentException("El nombre no puede exceder los 255 caracteres. Se ingresaron "
                    + nameReceptionalWork.length() + " caracteres.");
        }
        LocalDate localDateStart = datePickerDateStart.getValue();
        receptionalWork.setReceptionalWorkName(nameReceptionalWork);
        if (localDateStart != null) {
            java.sql.Date dateStart = java.sql.Date.valueOf(localDateStart);
            receptionalWork.setStartDate(dateStart);
        }
        receptionalWork.setReceptionalWorkName(nameReceptionalWork);
        return receptionalWork;
    }

    public void initComboBoxPreliminaryProject() {
        PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList();
        try {
            listOfPreliminaryProject = preliminaryProjectDAO.getPreliminaryProjectsFromDirector(directorNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<PreliminaryProject> observableListPreliminaryProject = FXCollections.observableArrayList(
                listOfPreliminaryProject);
        comboBoxPreliminaryProject.setItems(observableListPreliminaryProject);
    }

    public void initComboBoxSynodal() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        List<Professor> listOfSynodal = new ArrayList();
        try {
            listOfSynodal = professorDAO.getSynodal();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Professor> observableListSynodal = FXCollections.observableArrayList(listOfSynodal);
        comboBoxSynodal.setItems(observableListSynodal);
    }
}
