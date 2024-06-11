/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import mx.uv.fei.logic.Advance;
import mx.uv.fei.logic.AdvanceDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * Sirve para alojar las acciones necesarias que el estudiante podrá realizar.
 * @author alexs
 */
public class HomeStudentController implements Initializable {
    
    @FXML
    private Label labelName;
    @FXML
    private Label labelEducativeExperience;
    @FXML
    private TableView<Advance> tableViewAdvance;
    @FXML
    private TableColumn<Advance, String> columnAdvanceTitle;
    @FXML
    private TableColumn<Advance, String> columnAdvanceDateEnd;
    @FXML
    private TableColumn<Advance, String> columnAdvanceState;
    @FXML
    private Button buttonDeliver;
    @FXML
    private Button buttonModify;
    
    public AnchorPane anchorPaneRoot;
    private String enrollment;
    private ObservableList<Advance> observableListAdvance;
    ClassHolder classHolder = ClassHolder.getInstance();
    ClassHolder advanceHolder = ClassHolder.getInstance();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnAdvanceTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnAdvanceDateEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        columnAdvanceState.setCellValueFactory(new PropertyValueFactory<>("advanceState"));
        observableListAdvance = getTableAdvance();
        tableViewAdvance.setItems(observableListAdvance);
        String email = classHolder.getUser().getEmail();
        initComponents(email);
    }
    
    public void initComponents(String email) {
        try {
            Student student = new Student();
            StudentDAO studentDAO = new StudentDAO();
            student = studentDAO.getInformation(email);
            this.labelName.setText(String.format("%s", student.getName().split(" ")));
            this.labelEducativeExperience.setText(String.format("%s", student.getEducativeExperience()));
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }
    
    public void updateTable() {
        observableListAdvance.clear();
        observableListAdvance.addAll(getTableAdvance());
        tableViewAdvance.refresh();
    }
    
    private ObservableList<Advance> getTableAdvance() {
        StudentDAO studentDAO = new StudentDAO();
        AdvanceDAO advanceDAO = new AdvanceDAO();
        List<Advance> listOfAdvance = new ArrayList();
        try {
            enrollment = studentDAO.getEnrollmentStudent(classHolder.getUser().getEmail());
            listOfAdvance = advanceDAO.showAdvanceFromStudent(enrollment);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Advance> observableListAdvances = FXCollections.observableArrayList(listOfAdvance);
        return observableListAdvances;
    }
    
    @FXML
    void createOnAction(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScheduleAdvance.fxml"));
            Parent root = loader.load();
            ScheduleAdvanceController scheduleAdvanceController = loader.getController();
            scheduleAdvanceController.setHomeStudent(this);
            App.newView(root);
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }
    
    @FXML
    private void deliverAdvanceOnAction(ActionEvent event) throws IOException {
        Advance selectedAdvance = tableViewAdvance.getSelectionModel().getSelectedItem();
        if (selectedAdvance != null) {
            Date currentDate = new Date(System.currentTimeMillis());
            Date endDate = selectedAdvance.getEndDate();
            if (endDate.compareTo(currentDate) > 0 || endDate.equals(currentDate)) {
                try {
                    Advance advance = new Advance();
                    advance = tableViewAdvance.getSelectionModel().getSelectedItem();
                    advanceHolder.setAdvance(advance);
                    buttonDeliver.setDisable(true);
                    buttonModify.setDisable(true);
                    App.openNewWindow("/fxml/DeliverAdvance.fxml");
                } catch (IOException ex) {
                    App.getLogger().warn(ex.getMessage());
                    DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
                }
            } else {
                DialogGenerator.getDialog("No se pudo entregar el avance, la fecha de finalización ya ha pasado.",
                        Status.ERROR);
            }
        } else {
            DialogGenerator.getDialog("No se ha seleccionado ningún avance.", Status.ERROR);
        }
    }
    
    @FXML
    private void modifyAdvanceOnAction(ActionEvent event) {
        Advance selectedAdvance = tableViewAdvance.getSelectionModel().getSelectedItem();
        if (selectedAdvance != null) {
            Date currentDate = new Date(System.currentTimeMillis());
            Date endDate = selectedAdvance.getEndDate();
            if (endDate.compareTo(currentDate) > 0 || endDate.equals(currentDate)) {
                try {
                    Advance advance = new Advance();
                    advance = tableViewAdvance.getSelectionModel().getSelectedItem();
                    advanceHolder.setAdvance(advance);
                    buttonDeliver.setDisable(true);
                    buttonModify.setDisable(true);
                    App.openNewWindow("/fxml/ModifyAdvance.fxml");
                } catch (IOException ex) {
                    App.getLogger().warn(ex.getMessage());
                    DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
                }
            } else {
                DialogGenerator.getDialog("No se puede modificar, la fecha de finalización ya ha pasado.",
                        Status.ERROR);
            }
        } else {
            DialogGenerator.getDialog("No se ha seleccionado ningún avance.", Status.ERROR);
        }
    }
    
    @FXML
    void selectedRow(MouseEvent event) {
        try {
            Advance selectedAdvance = new Advance();
            AdvanceDAO advanceDAO = new AdvanceDAO();
            StudentDAO studentDAO = new StudentDAO();
            String studentEnrollment = studentDAO.getEnrollmentStudent(classHolder.getUser().getEmail());
            selectedAdvance = tableViewAdvance.getSelectionModel().getSelectedItem();
            if (selectedAdvance != null) {
                if (advanceDAO.isDeliverAdvance(selectedAdvance.getTitle(), studentEnrollment) == false) {
                    buttonDeliver.setDisable(false);
                    buttonModify.setDisable(true);
                } else {
                    buttonDeliver.setDisable(true);
                    buttonModify.setDisable(false);
                }
            } else {
                buttonDeliver.setDisable(true);
                buttonModify.setDisable(true);
            }
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }
    
    @FXML
    void exitOnAction(MouseEvent event) {
        try {
            App.openNewWindow("/fxml/Login.fxml");
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }
}
