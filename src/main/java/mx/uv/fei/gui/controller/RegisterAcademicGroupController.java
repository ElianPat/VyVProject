/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import mx.uv.fei.logic.AcademicGroup;
import mx.uv.fei.logic.AcademicGroupDAO;
import mx.uv.fei.logic.ClassHolder;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;

/**
 * FXML Controller class
 * esta clase se encarga de registrar cuerpos académicos.
 * author aresj
 */
public class RegisterAcademicGroupController implements Initializable {

    @FXML
    private TextField textFieldKeyAcademicGroup;
    @FXML
    private TextField textFieldNameAcademicGroup;
    @FXML
    private ChoiceBox<String> choiceBoxRole;
    @FXML
    private ComboBox<Professor> comboBoxProfessor;
    @FXML
    private Button buttonRegisterAcademicGroup;
    @FXML
    private Pane paneAddMember;

    private final String[] role = {"Integrante", "Coordinador"};
    private boolean fieldsBlocked = false;
    private boolean comboBoxEmpty = false;
    private final ClassHolder classHolder = ClassHolder.getInstance();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choiceBoxRole.getItems().addAll(role);
        paneAddMember.setVisible(false);
        textFieldKeyAcademicGroup.setEditable(true);
        textFieldNameAcademicGroup.setEditable(true);
        initComboBoxProfessor();
        if (comboBoxProfessor.getItems().isEmpty()) {
            comboBoxEmpty = true;
            DialogGenerator.getDialog("No se puede crear cuerpos académicos porque no hay profesores disponibles.",
                    Status.WARNING);
            textFieldKeyAcademicGroup.setEditable(false);
            textFieldNameAcademicGroup.setEditable(false);
            fieldsBlocked = true;
        }
        limitTextField(9);
    }

    @FXML
    private void registerAcademicGroupOnAction(ActionEvent event) {
        if (!fieldsBlocked) {
            try {
                AcademicGroup academicGroup = getDataFromFormAcademicGroup();
                String keyAcademicGroup = academicGroup.getKey();
                invokeRegistration(academicGroup, keyAcademicGroup);
            } catch (DAOException | IllegalArgumentException ex) {
                App.getLogger().warn(ex.getMessage());
                DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
            }
        }
    }

    private void invokeRegistration(AcademicGroup academicGroup, String keyAcademicGroup) throws DAOException {
        AcademicGroupDAO academicGroupDAO = new AcademicGroupDAO();
        if (!academicGroupDAO.isAcademicGroupExisting(keyAcademicGroup)) {
            academicGroupDAO.registerAcademicGroup(academicGroup);
            DialogGenerator.getConfirmationDialog("Los datos del cuerpo academico han sido registrados exitosamente",
                    "Cuerpo Academico Registrado");
            buttonRegisterAcademicGroup.setDisable(true);
            paneAddMember.setVisible(true);
            textFieldKeyAcademicGroup.setEditable(false);
            textFieldNameAcademicGroup.setEditable(false);
            classHolder.setAcademicGroup(academicGroup);
            fieldsBlocked = true;
        } else {
            DialogGenerator.getDialog("La clave del cuerpo academico que tratas de ingresar ya existe en el sistema",
                    Status.WARNING);
        }
    }

    private AcademicGroup getDataFromFormAcademicGroup() {
        AcademicGroup academicGroup = new AcademicGroup();
        String key = textFieldKeyAcademicGroup.getText();
        String nameAcademicGroup = textFieldNameAcademicGroup.getText();
        if (key.length() > 255) {
            throw new IllegalArgumentException("La clave del cuerpo academico no puede exceder los 255 caracteres."
                    + " Se ingresaron " + key.length() + " caracteres.");
        }
        if (nameAcademicGroup.length() > 255) {
            throw new IllegalArgumentException("El nombre del cuerpo academico no puede exceder los 255 caracteres. "
                    + "Se ingresaron " + nameAcademicGroup.length() + " caracteres.");
        }
        academicGroup.setKey(key);
        academicGroup.setNameAcademicGroup(nameAcademicGroup);
        return academicGroup;
    }

    @FXML
    private void addMemberOnAction(ActionEvent event) {
        Professor professor = new Professor();
        AcademicGroup academicGroup = new AcademicGroup();
        AcademicGroupDAO academicGroupDAO = new AcademicGroupDAO();
        Professor selectedTeacher = comboBoxProfessor.getSelectionModel().getSelectedItem();
        String keyAcademicGroup = classHolder.getAcademicGroup().getKey();
        try {
            if (selectedTeacher != null) {
                if (role != null) {
                    String names = comboBoxProfessor.getSelectionModel().getSelectedItem().toString();
                    String roles = choiceBoxRole.getValue();
                    professor.setName(names);
                    professor.setRole(roles);
                    professor.setProfessorNumber(personalNumber(names));
                    academicGroup.setKey(keyAcademicGroup);
                    academicGroupDAO.addProfessorInAcademicGroup(academicGroup, professor);
                    initComboBoxProfessorInAcademicGroup(keyAcademicGroup);
                    DialogGenerator.getConfirmationDialog("Miembro agregado al cuerpo academico de forma exitosa",
                            "Cuerpo Academico Actualizado");
                } else {
                    throw new IllegalArgumentException("Debe seleccionar un rol.");
                }
            } else {
                throw new IllegalArgumentException("Debe seleccionar un profesor.");
            }
        } catch (DAOException | SQLException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void initComboBoxProfessorInAcademicGroup(String keyAcademicGroup) {
        ProfessorDAO professorDAO = new ProfessorDAO();
        List<Professor> listOfProfessor = new ArrayList();
        try {
            listOfProfessor = professorDAO.getProfessorNameOfAcademicGroup(keyAcademicGroup);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Professor> getNameTeachers = FXCollections.observableArrayList(listOfProfessor);
        comboBoxProfessor.setItems(getNameTeachers);
    }

    public int personalNumber(String name) throws SQLException {
        int personalNumber = 0;
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            personalNumber = professorDAO.getProfessorNumber(name);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        return personalNumber;
    }

    public void initComboBoxProfessor() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        List<Professor> listOfProfessor = new ArrayList();
        try {
            listOfProfessor = professorDAO.getAllProfessor();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Professor> observableListProfessor = FXCollections.observableArrayList(listOfProfessor);
        comboBoxProfessor.setItems(observableListProfessor);
    }

    public void limitTextField(int limit) {
        UnaryOperator<TextFormatter.Change> textLimitFilter = change -> {
            if (change.isContentChange()) {
                int newLength = change.getControlNewText().length();
                if (newLength > limit) {
                    String trimmedText = change.getControlNewText().substring(0, limit);
                    change.setText(trimmedText);
                    int oldLength = change.getControlText().length();
                    change.setRange(0, oldLength);
                }
            }
            return change;
        };
        textFieldKeyAcademicGroup.setTextFormatter(new TextFormatter(textLimitFilter));
    }
}
