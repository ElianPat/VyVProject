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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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
 * Este controlador permite modificar la información de un curso.
 * @author Palom
 */
public class ModifyCourseController implements Initializable {

    @FXML
    private TableView<Course> tableViewCourse;
    @FXML
    private TextField textFieldNrc;
    @FXML
    private ChoiceBox<String> choiceBoxPeriod;
    @FXML
    private ComboBox<EducativeExperience> comboBoxEducativeExperience;
    @FXML
    private ComboBox<Professor> comboBoxProfessor;
    @FXML
    private TableColumn<Course, String> columnCourseSubject;
    @FXML
    private TableColumn<Course, String> columnCourseNrc;
    @FXML
    private Button buttonSaveChanges;

    private String nrc = "";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        buttonSaveChanges.setDisable(true);
        limitTextFieldNrc(5);
        columnCourseNrc.setCellValueFactory(new PropertyValueFactory<>("nrc"));
        columnCourseSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        tableViewCourse.setItems(getTableCourse());
        initComboBoxProfesor();
        initComboBoxEducativeExperience();
        initComboBoxPeriod();
    }

    @FXML
    private void selectedRow(MouseEvent event) {
        try {
            Course selectedCourse = this.tableViewCourse.getSelectionModel().getSelectedItem();
            if (selectedCourse != null) {
                String nrcSelected = this.tableViewCourse.getSelectionModel().getSelectedItem().getNrc();
                Course course = new Course();
                CourseDAO courseDAO = new CourseDAO();
                course = courseDAO.getInfoCourse(nrcSelected);
                initComponents(course);
                buttonSaveChanges.setDisable(false);
            } else {
                DialogGenerator.getDialog("Selecciona una fila con información para poder modificar.",
                        Status.WARNING);
            }
        } catch (DAOException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void saveChangesOnAction(ActionEvent event) {
        Course course = new Course();
        CourseDAO courseDAO = new CourseDAO();
        try {
            course = getDataFromForm();
            String newNrc = course.getNrc();
            if (textFieldNrc.getText().isEmpty()) {
                DialogGenerator.getDialog("No puede dejar campos vacios", Status.WARNING);
            } else if (!textFieldNrc.getText().matches("^[0-9]{5}$")) {
                DialogGenerator.getDialog("El NRC del curso debe ser de 5 dígitos", Status.WARNING);
            } else {
                if (newNrc.equals(nrc)) {
                    invokeSaveChanges(course);
                } else {
                    if (courseDAO.isNrcExisting(newNrc) == false) {
                        invokeSaveChanges(course);
                    } else {
                        DialogGenerator.getDialog("No puede duplicar nrc", Status.WARNING);
                    }
                }
            }
        } catch (DAOException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
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

    }

    private void invokeSaveChanges(Course course) throws DAOException {
        try {
            Course selectedCourse = this.tableViewCourse.getSelectionModel().getSelectedItem();
            if (selectedCourse == null) {
                DialogGenerator.getDialog("Debes seleccionar un curso para modificar", Status.WARNING);
            } else {
                CourseDAO courseDAO = new CourseDAO();
                if (courseDAO.updateCourse(course, nrc) > 0) {
                    DialogGenerator.getConfirmationDialog("Se ha realizado la modificacion exitosamente",
                            "Confirmacion");
                }
                tableViewCourse.setItems(getTableCourse());
                buttonSaveChanges.setDisable(true);
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private Course getDataFromForm() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        EducativeExperienceDAO educativeExperienceDAO = new EducativeExperienceDAO();
        Course course = new Course();
        try {
            String nrcCourse = this.textFieldNrc.getText();
            String professorName = comboBoxProfessor.getSelectionModel().getSelectedItem().toString();
            if (comboBoxProfessor.getSelectionModel().getSelectedItem() == null) {
                throw new IllegalArgumentException("Debe seleccionar un profesor.");
            }
            int professorNumber = professorDAO.getProfessorNumber(professorName);
            String educativeExperienceName = comboBoxEducativeExperience.getSelectionModel().getSelectedItem().toString();
            if (comboBoxEducativeExperience.getSelectionModel().getSelectedItem() == null) {
                throw new IllegalArgumentException("Debe seleccionar un experiencia educativa.");
            }
            int IdSubject = educativeExperienceDAO.getIdEducativeExperience(educativeExperienceName);
            course.setNrc(nrcCourse);
            course.setPeriod(choiceBoxPeriod.getValue());
            course.setProfessorNumber(professorNumber);
            course.setIdEducativeExperience(IdSubject);
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        return course;
    }

    public ObservableList<Course> getTableCourse() {
        CourseDAO courseDAO = new CourseDAO();
        List<Course> listOfCourse = new ArrayList();
        try {
            listOfCourse = courseDAO.getCoursesToModify();
        } catch (DAOException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Course> observableListCourse = (ObservableList<Course>) FXCollections.observableArrayList(listOfCourse);
        return observableListCourse;
    }

    public void initComboBoxProfesor() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        List<Professor> listOfProfessor = new ArrayList();
        try {
            listOfProfessor = professorDAO.getAllProfessor();
        } catch (DAOException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Professor> observableListProfessor = FXCollections.observableArrayList(listOfProfessor);
        comboBoxProfessor.setItems(observableListProfessor);
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
        ObservableList<EducativeExperience> observableListEducativeExperience = FXCollections.observableArrayList(
                listOfEducativeExperience);
        comboBoxEducativeExperience.setItems(observableListEducativeExperience);
    }

    public void initComponents(Course course) {
        nrc = course.getNrc();
        EducativeExperience educativeExperience = new EducativeExperience();
        Professor professor = new Professor();

        this.textFieldNrc.setText(course.getNrc());

        educativeExperience.setEducativeExperienceName(course.getSubject());
        this.comboBoxEducativeExperience.getSelectionModel().select(educativeExperience);

        List<Professor> listOfProfessor = new ArrayList<>();
        professor.setName(course.getProfessor());
        listOfProfessor.add(professor);
        this.comboBoxProfessor.getSelectionModel().select(professor);
        this.choiceBoxPeriod.setValue(course.getPeriod());
    }

    public void limitTextFieldNrc(int limit) {
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
