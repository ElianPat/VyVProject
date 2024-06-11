/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import mx.uv.fei.logic.Course;
import mx.uv.fei.logic.CourseDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.EducativeExperience;
import mx.uv.fei.logic.EducativeExperienceDAO;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;

/**
 * FXML Controller class
 * este controlador permite crear cursos.
 * @author Palom
 */
public class RegisterCourseController implements Initializable {

    @FXML
    private TextField textFieldNrc;
    @FXML
    private ComboBox<EducativeExperience> comboBoxEducativeExperience;
    @FXML
    private ComboBox<Professor> comboBoxProfessor;
    @FXML
    private ChoiceBox<String> choiceBoxPeriod;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limitTextField(5);
        initComboBoxProfesor();
        initComboBoxPeriod();
        initComboBoxEducativeExperience();
    }

    @FXML
    private void registerCourseOnAction(ActionEvent event) {
        try {
            Course course = new Course();
            CourseDAO courseDAO = new CourseDAO();
            course = getDataFromForm();
            if (textFieldNrc.getText().isEmpty()) {
                DialogGenerator.getDialog("No puede dejar campos vacio", Status.WARNING);
            } else if (!textFieldNrc.getText().matches("^[0-9]{5}$")) {
                DialogGenerator.getDialog("El NRC del curso debe ser de 5 dígitos", Status.WARNING);
            } else {
                if (courseDAO.isNrcExisting(course.getNrc()) == false) {
                    courseDAO.addCourse(course);
                    DialogGenerator.getConfirmationDialog("Curso registrado en el sistema exitosamente",
                            "Registro exitoso");
                } else {
                    DialogGenerator.getConfirmationDialog("Curso registrado previamente en el sistema",
                            "Registro fallido");
                }
            }
        } catch (DAOException | IllegalArgumentException ex) {
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void initComboBoxProfesor() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        List<Professor> listProfessors = new ArrayList();
        try {
            listProfessors = professorDAO.getAllProfessor();
        } catch (DAOException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Professor> getAllProfesor = FXCollections.observableArrayList(listProfessors);
        comboBoxProfessor.setItems(getAllProfesor);
        comboBoxProfessor.getSelectionModel().select(0);
    }

    public void initComboBoxPeriod() {
        CourseDAO courseDAO = new CourseDAO();
        List<Course> listOfCourse = new ArrayList();
        try {
            listOfCourse = courseDAO.getPeriod();
        } catch (DAOException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        String[] arrayCourse = new String[listOfCourse.size()];
        for (int i = 0; i < listOfCourse.size(); i++) {
            Course courseArray = listOfCourse.get(i);
            String coursePeriod = courseArray.getPeriod();
            arrayCourse[i] = coursePeriod;
        }
        choiceBoxPeriod.getItems().setAll(arrayCourse);
        this.choiceBoxPeriod.getSelectionModel().select(0);
    }

    public void initComboBoxEducativeExperience() {
        EducativeExperienceDAO educativeExperienceDAO = new EducativeExperienceDAO();
        List<EducativeExperience> listOfEducativeExperience = new ArrayList();
        try {
            listOfEducativeExperience = educativeExperienceDAO.getAllEducativeExperience();
        } catch (DAOException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<EducativeExperience> observableListEducativeExperience
                = FXCollections.observableArrayList(listOfEducativeExperience);
        comboBoxEducativeExperience.setItems(observableListEducativeExperience);
        comboBoxEducativeExperience.getSelectionModel().select(0);
    }

    private Course getDataFromForm() throws DAOException {
        Course course = new Course();
        ProfessorDAO professorDAO = new ProfessorDAO();
        EducativeExperienceDAO educativeExperienceDAO = new EducativeExperienceDAO();
        EducativeExperience educativeExperience = comboBoxEducativeExperience.getSelectionModel().getSelectedItem();
        Professor professor = comboBoxProfessor.getSelectionModel().getSelectedItem();
        String selectedCourse = choiceBoxPeriod.getSelectionModel().getSelectedItem();
        if (educativeExperience == null) {
            throw new IllegalArgumentException("Debe seleccionar una experiencia educativa");
        } else if (professor == null) {
            throw new IllegalArgumentException("Seleccione un profesor a asignar");
        } else if (selectedCourse == null) {
            throw new IllegalArgumentException("Seleccione un periodo a asignar");
        } else {
            course.setIdEducativeExperience(educativeExperienceDAO.getIdEducativeExperience(
                    comboBoxEducativeExperience.getSelectionModel().getSelectedItem().toString()));
            course.setProfessorNumber(professorDAO.getProfessorNumber(
                    comboBoxProfessor.getSelectionModel().getSelectedItem().toString()));
            course.setNrc(textFieldNrc.getText());
            course.setPeriod(choiceBoxPeriod.getValue());
        }
        return course;
    }

    public void limitTextField(int limit) {
        UnaryOperator<TextFormatter.Change> textFilter = change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("^(?!0+$)[0-9]{0," + limit + "}$")) {
                return null;
            }
            int newLength = newText.length();
            if (newLength > limit) {
                int replaceEndIndex = change.getRangeEnd() - (newLength - limit);
                String trimmedText = newText.substring(0, limit);
                change.setText(trimmedText);
                change.setRange(change.getRangeStart(), replaceEndIndex);
                change.setCaretPosition(replaceEndIndex);
            }
            return change;
        };
        textFieldNrc.setTextFormatter(new TextFormatter<>(textFilter));
    }
}
