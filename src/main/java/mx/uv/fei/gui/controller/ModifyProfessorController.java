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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.User;
import mx.uv.fei.logic.UserDAO;

/**
 * FXML Controller class
 * permite modificar la información de un profesor.
 * @author Palom
 */
public class ModifyProfessorController implements Initializable {

    @FXML
    private TableView<Professor> tableViewProfessor;
    @FXML
    private CheckBox checkBoxEnable;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPersonalNumber;
    @FXML
    private TableColumn<Professor, String> columnProfessorName;
    @FXML
    private TableColumn<Professor, Integer> columnProfessorNumber;
    @FXML
    private Button buttonSaveChanges;

    private ObservableList<Professor> observableListProfessor;
    private String email = "";
    private int personalNumber = 0;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limitTextField(5);
        columnProfessorNumber.setCellValueFactory(new PropertyValueFactory<>("professorNumber"));
        columnProfessorName.setCellValueFactory(new PropertyValueFactory<>("name"));
        observableListProfessor = getTableProfessor();
        tableViewProfessor.setItems(observableListProfessor);
    }

    @FXML
    private void saveChangesOnAction(ActionEvent event) {
        Professor professor = new Professor();
        ProfessorDAO professorDAO = new ProfessorDAO();
        UserDAO userDAO = new UserDAO();
        try {
            if (textFieldPersonalNumber.getText().isEmpty()) {
                DialogGenerator.getDialog("No puede dejar campos vacio", Status.WARNING);
            } else {
                professor = getDataFromForm();
                if (email.equals(professor.getEmail())) {
                    if (personalNumber == professor.getProfessorNumber()) {
                        invokeSaveChangesProfessor(getDataFromForm());
                    } else {
                        if (professorDAO.isPersonalNumberExisting(professor.getProfessorNumber()) == false) {
                            invokeSaveChanges(getDataFromForm());
                        } else {
                            DialogGenerator.getDialog("No puede duplicar numeros de personal", Status.WARNING);
                        }
                    }
                } else {
                    
                    if (userDAO.isUserExisting(professor.getEmail()) == false) {
                        if (personalNumber == professor.getProfessorNumber()) {
                            invokeSaveChanges(getDataFromForm());
                        } else {
                            if (professorDAO.isPersonalNumberExisting(professor.getProfessorNumber()) == false) {
                                invokeSaveChanges(getDataFromForm());
                            } else {
                                DialogGenerator.getDialog("No puede duplicar numeros de personal", Status.WARNING);
                            }
                        }
                    } else {
                        DialogGenerator.getDialog("No puede duplicar correos", Status.WARNING);
                    }
                }
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private void invokeSaveChanges(Professor professor) throws DAOException {
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            User user = new User();
            UserDAO userDAO = new UserDAO();
            professor = getDataFromForm();
            user.setName(professor.getName());
            user.setEmail(professor.getEmail());
            user.setEnable(professor.getEnable());
            if (userDAO.modifyUser(user, email) > 0 && professorDAO.updateProfessor(professor,professor.getEmail()) > 0) {
                DialogGenerator.getConfirmationDialog("Se ha realizado la modificacion exitosamente", "Confirmacion");
            }
            
            tableViewProfessor.setItems(getTableProfessor());
            buttonSaveChanges.setDisable(true);
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }
    
    private void invokeSaveChangesProfessor(Professor professor) throws DAOException {
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            User user = new User();
            UserDAO userDAO = new UserDAO();
            professor = getDataFromForm();
            user.setName(professor.getName());
            user.setEmail(professor.getEmail());
            user.setEnable(professor.getEnable());
            if (userDAO.modifyUser(user, email) > 0 && professorDAO.updateProfessor(professor,email) > 0) {
                DialogGenerator.getConfirmationDialog("Se ha realizado la modificacion exitosamente", "Confirmacion");
            }    
            tableViewProfessor.setItems(getTableProfessor());
            buttonSaveChanges.setDisable(true);
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private Professor getDataFromForm() {
        Professor professor = new Professor();
        String name = textFieldName.getText();
        String secondEmail = textFieldEmail.getText();
       
        String professorNumber = textFieldPersonalNumber.getText();
        if (name.length() > 255) {
            throw new IllegalArgumentException("El nombre no puede exceder los 255 caracteres. "
                    + "Se ingresaron " + name.length() + " caracteres.");
        }
        if (secondEmail.length() > 255) {
            throw new IllegalArgumentException("La matrícula no puede exceder los 255 caracteres."
                    + " Se ingresaron " + secondEmail.length() + " caracteres.");
        }
        professor.setName(name);
        professor.setEmail(secondEmail);
        professor.setProfessorNumber(Integer.parseInt(professorNumber));
        if (checkBoxEnable.isSelected()) {
            professor.setEnable(1);
        } else {
            professor.setEnable(0);
        }
        return professor;
    }

    @FXML
    private void selectedRow(MouseEvent event) {
        try {
            int personalNumberSelected = this.tableViewProfessor.getSelectionModel().getSelectedItem().getProfessorNumber();
            Professor professor = new Professor();
            ProfessorDAO professorDAO = new ProfessorDAO();
            professor = professorDAO.getInfoToModifyProfessor(personalNumberSelected);
            initComponents(professor);
            buttonSaveChanges.setDisable(false);
        } catch (DAOException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public ObservableList<Professor> getTableProfessor() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        List<Professor> listOfProfessor = new ArrayList();
        try {
            listOfProfessor = professorDAO.getProfessorToModify();
        } catch (DAOException ex) {
            App.getLogger().error(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Professor> observableListProfessorName = FXCollections.observableArrayList(listOfProfessor);
        return observableListProfessorName;
    }

    public void initComponents(Professor professor) {
        email = professor.getEmail();
        personalNumber = professor.getProfessorNumber();
        this.textFieldName.setText(professor.getName());
        this.textFieldEmail.setText(professor.getEmail());
        this.textFieldPersonalNumber.setText(professor.getProfessorNumber() + "");
        if (professor.getEnable() > 0) {
            checkBoxEnable.setSelected(true);
        } else {
            checkBoxEnable.setSelected(false);
        }
    }

    public void limitTextField(int limit) {
        UnaryOperator<TextFormatter.Change> textFilter = change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("^(?!0+$)[0-9]{0," + limit + "}$")) {
                DialogGenerator.getDialog("El número de personal solo puede contener números "
                        + "y tener una longitud de 1 a 5 dígitos", Status.WARNING);
                return null;
            }
            int newLength = newText.length();
            if (newLength > limit) {
                int exceedLength = newLength - limit;
                int replaceEndIndex = change.getRangeEnd() - exceedLength;
                String trimmedText = newText.substring(0, limit);
                change.setText(trimmedText);
                change.setRange(change.getRangeStart(), replaceEndIndex);
                change.setCaretPosition(replaceEndIndex);
            }
            return change;
        };
        textFieldPersonalNumber.setTextFormatter(new TextFormatter<>(textFilter));
    }
}
