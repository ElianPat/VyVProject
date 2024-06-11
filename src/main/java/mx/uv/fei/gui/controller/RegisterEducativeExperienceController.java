/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.EducativeExperience;
import mx.uv.fei.logic.EducativeExperienceDAO;
import mx.uv.fei.logic.Status;

/**
 * FXML Controller class
 * esta clase se encarga de registrar experiencias educativas.
 * @author alexs
 */
public class RegisterEducativeExperienceController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private ComboBox comboBoxEducativeExperience;
    @FXML
    private Button buttonRegisterEducativeExperience;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboBoxNrc();
        if (comboBoxEducativeExperience.getItems().isEmpty()) {
            DialogGenerator.getDialog("Las Experiencias educativas ya fueron registradas", Status.WARNING);
            buttonRegisterEducativeExperience.setDisable(true);
        }
    }

    private void initComboBoxNrc() {
        ObservableList<String> educativeExperience = FXCollections.observableArrayList("Proyecto Guiado", "Experiencia Recepcional");
        EducativeExperienceDAO educativeExperienceDAO = new EducativeExperienceDAO();
        try {
            List<EducativeExperience> registeredExperiences = educativeExperienceDAO.getAllEducativeExperience();
            for (EducativeExperience experience : registeredExperiences) {
                educativeExperience.remove(experience.getEducativeExperienceName());
            }
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        comboBoxEducativeExperience.setItems(educativeExperience);
    }

    @FXML
    void registerEducativeExperienceOnAction(ActionEvent event) {
        try {
            EducativeExperience selectedExperience = getDataFromForm();
            invokeEducativeExperienceRegistration(selectedExperience);
            comboBoxEducativeExperience.getSelectionModel().clearSelection();
            comboBoxEducativeExperience.getItems().remove(selectedExperience.getEducativeExperienceName());
            if (comboBoxEducativeExperience.getItems().isEmpty()) {
                DialogGenerator.getDialog("Las Experiencias educativas ya fueron registradas", Status.WARNING);
                buttonRegisterEducativeExperience.setDisable(true);
            }
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        } catch (NullPointerException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog("Debe que seleccionar una experiencia educativa", Status.WARNING);
        }
    }

    private void invokeEducativeExperienceRegistration(EducativeExperience educativeExperience) throws DAOException {
        EducativeExperienceDAO educativeExperienceDAO = new EducativeExperienceDAO();
        String selectedEducativeExperience = comboBoxEducativeExperience.getSelectionModel().getSelectedItem().toString();
        try {

            if (educativeExperienceDAO.createEducativeExperience(educativeExperience) > 0) {
                DialogGenerator.getDialog("Experiencia educativa registrada", Status.SUCCESS);
            } else {
                DialogGenerator.getDialog("No fue posible registrar la experiencia educativa", Status.WARNING);
            }

        } catch (IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private EducativeExperience getDataFromForm() {
        EducativeExperience educativeExperience = new EducativeExperience();
        educativeExperience.setEducativeExperienceName(comboBoxEducativeExperience.getSelectionModel().getSelectedItem().toString());
        return educativeExperience;
    }
}
