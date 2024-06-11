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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mx.uv.fei.logic.AcademicGroup;
import mx.uv.fei.logic.AcademicGroupDAO;
import mx.uv.fei.logic.ClassHolder;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;

/**
 * FXML Controller class
 * Este controlador sirve para poder modificar los miembros de un cuerpo
 * académico.
 * @author aresj
 */
public class ModifyAcademicGroupController implements Initializable {

    @FXML
    private ImageView imageViewExit;
    @FXML
    private ComboBox<Professor> comboBoxMemberName;
    @FXML
    private ChoiceBox<String> comboBoxMemberRole;
    @FXML
    private TableView<Professor> tableViewMembers;
    @FXML
    private TableColumn<Professor, String> columnMemberRol;
    @FXML
    private TableColumn<Professor, String> columnMemberName;

    private int personalMemberNumber;
    private String keyAcademicGroup;
    private final String[] role = {"Integrante", "Coordinador"};
    private final ClassHolder classHolder = ClassHolder.getInstance();
    private Professor academicGroupMember = new Professor();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        keyAcademicGroup = classHolder.getAcademicGroup().getKey();
        columnMemberRol.setCellValueFactory(new PropertyValueFactory<>("role"));
        columnMemberName.setCellValueFactory(new PropertyValueFactory<>("name"));
        comboBoxMemberRole.getItems().addAll(role);
        initComboBoxProfessor(keyAcademicGroup);
        tableViewMembers.setItems(getTableMembers());
    }

    private ObservableList<Professor> getTableMembers() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        List<Professor> listOfProfessor = new ArrayList();
        try {
            listOfProfessor = professorDAO.getAllProfessorOfAcademicGroup(keyAcademicGroup);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Professor> observableListProfessor = FXCollections.observableArrayList(listOfProfessor);
        return observableListProfessor;
    }

    @FXML
    void exitOnAction(MouseEvent event) {
        Stage stage = (Stage) this.imageViewExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void modifyAcademicGroupMemberOnAction(ActionEvent event) {
        AcademicGroupDAO academicGroupDAO = new AcademicGroupDAO();
        Professor professor = this.tableViewMembers.getSelectionModel().getSelectedItem();
        try {
            if (professor == null) {
                DialogGenerator.getDialog("Debes seleccionar un profesor de la tabla para modificar", Status.WARNING);
            } else {
                String name = comboBoxMemberName.getSelectionModel().getSelectedItem().toString();
                String memberRole = comboBoxMemberRole.getValue();
                professor.setName(name);
                professor.setRole(memberRole);
                professor.setProfessorNumber(personalNumber(name));
                academicGroupDAO.modifyMember(professor, personalMemberNumber, keyAcademicGroup);
                DialogGenerator.getConfirmationDialog("Da click en el boton de " + "OK" + " para continuar",
                        "Los datos del Miembro del Cuerpo Academico fueron actualizados exitosamente");
                tableViewMembers.setItems(getTableMembers());
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void deleteMemberAcademicGroupOnAction(ActionEvent event) {
        AcademicGroupDAO academicGroupDAO = new AcademicGroupDAO();
        Professor professor = new Professor();
        ProfessorDAO professorDAO = new ProfessorDAO();
        professor = this.tableViewMembers.getSelectionModel().getSelectedItem();
        try {
            if (professor == null) {
                DialogGenerator.getDialog("Debes seleccionar un profesor para eliminar de la tabla", Status.WARNING);
            } else {
                String name = comboBoxMemberName.getSelectionModel().getSelectedItem().toString();
                professor.setName(name);
                int personalNumber = professorDAO.getProfessorNumber(name);
                if (academicGroupDAO.isProfessorMember(personalNumber, keyAcademicGroup) == true) {
                    academicGroupDAO.deleteProfessorOfAcademicGroup(personalNumber, keyAcademicGroup);
                    DialogGenerator.getConfirmationDialog("Da click en el boton de " + "OK" + " para continuar",
                            "El Miembro del Cuerpo Academico fue eliminado exitosamente");
                    tableViewMembers.setItems(getTableMembers());
                } else {
                    DialogGenerator.getDialog("No puedes eliminar a alguien que no es miembro en el Cuerpo academico", 
                            Status.WARNING);
                }
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void selectedRow(MouseEvent event) {
        ProfessorDAO professorDAO = new ProfessorDAO();
        Professor selectedProfessor = this.tableViewMembers.getSelectionModel().getSelectedItem();
        if (selectedProfessor != null) {
            String showData = selectedProfessor.getName();
            try {
                academicGroupMember = professorDAO.modifyMembersAcademicGroup(showData, keyAcademicGroup);
                initComponents();
            } catch (DAOException ex) {
                App.getLogger().error(ex.getMessage());
                DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
            }
        } else {
            DialogGenerator.getDialog("Selecciona una columna con información para poder modificar la información del "
                    + "cuerpo académico seleccionado.", Status.WARNING);
        }
    }

    public void initComponents() {
        try {
            Professor professor = new Professor();
            ProfessorDAO professorDAO = new ProfessorDAO();
            List<Professor> listOfProfessor = new ArrayList();
            professor.setName(academicGroupMember.getName());
            personalMemberNumber = professorDAO.getProfessorNumber(professor.getName());
            listOfProfessor.add(professor);
            this.comboBoxMemberName.getSelectionModel().select(professor);
            this.comboBoxMemberRole.setValue(academicGroupMember.getRole());
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void addMemberOnAction(ActionEvent event) {
        Professor professor = new Professor();
        AcademicGroup academicGroup = new AcademicGroup();
        AcademicGroupDAO academicBodyDAO = new AcademicGroupDAO();
        Professor selectedProfessor = comboBoxMemberName.getSelectionModel().getSelectedItem();
        String selectedRole = comboBoxMemberRole.getValue();
        try {
            if (selectedProfessor != null) {
                if (selectedRole != null) {
                    String names = comboBoxMemberName.getSelectionModel().getSelectedItem().toString();
                    String roles = comboBoxMemberRole.getValue();
                    professor.setName(names);
                    professor.setRole(roles);
                    professor.setProfessorNumber(personalNumber(names));
                    academicGroup.setKey(keyAcademicGroup);
                    if (academicBodyDAO.isProfessorMember(professor.getProfessorNumber(), keyAcademicGroup) == false) {
                        academicBodyDAO.addProfessorInAcademicGroup(academicGroup, professor);
                        initComboBoxProfessor(keyAcademicGroup);
                        DialogGenerator.getConfirmationDialog("Miembro agregado al cuerpo academico de forma exitosa", 
                                "Cuerpo Academico Actualizado");
                        tableViewMembers.setItems(getTableMembers());
                    } else {
                        DialogGenerator.getDialog("No se puede agregar a alguien que ya es miembro en el Cuerpo academico", 
                                Status.WARNING);
                    }
                } else {
                    throw new IllegalArgumentException("Debe seleccionar un rol de la caja de opciones.");
                }
            } else {
                throw new IllegalArgumentException("Debe seleccionar un profesor de la caja de opciones.");
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public int personalNumber(String name) {
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

    public void initComboBoxProfessor(String keyAcademicGroup) {
        ProfessorDAO professor = new ProfessorDAO();
        List<Professor> listOfProfessor = new ArrayList();
        try {
            listOfProfessor = professor.getProfessorNameOfAcademicGroup(keyAcademicGroup);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Professor> observableListProfessor = FXCollections.observableArrayList(listOfProfessor);
        comboBoxMemberName.setItems(observableListProfessor);
    }
}
