/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;
import mx.uv.fei.logic.ClassHolder;
import mx.uv.fei.logic.GenerateSubreport;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * FXML Controller class
 * Se encarga de generar los reportes para el codirector.
 * @author alexs
 */
public class CodirectorReportController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private ComboBox<Student> comboBoxEnrollment;
    @FXML
    private ComboBox<PreliminaryProject> comboBoxPreliminaryProject;
    @FXML
    private ImageView imageView;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label labelName;
    @FXML
    private Button btnGenerateReport;
    
    private Professor professor = new Professor();
    private final ClassHolder classHolder = ClassHolder.getInstance();

    private final String REPORT_FILE = "/reports/CodirectorReport.jrxml";
    private final String SUBREPORT1_FILE = "/reports/SubreportCodirector1.jrxml";
    private final String SUBREPORT2_FILE = "/reports/SubreportCodirector2.jrxml";
    private final String SUBREPORT3_FILE = "/reports/SubreportCodirector3.jrxml";
    private final String IMAGE_SGPG = "/images/SGPGyER-azul.png";
    private final String IMAGE_UV = "/images/Uv-logo.png";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            String email = classHolder.getUser().getEmail();
            initComponents(email);
            if (comboBoxEnrollment.getItems().isEmpty() && comboBoxPreliminaryProject.getItems().isEmpty()) {
                btnGenerateReport.setDisable(false);
                comboBoxEnrollment.setDisable(false);
                comboBoxPreliminaryProject.setDisable(false);
            }
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void initComponents(String email) throws DAOException {
        ProfessorDAO professorDAO = new ProfessorDAO();
        professor = professorDAO.getInformation(email);
        labelName.setText(String.format("%s", professor.getName()));
        initComboBoxEnrollment(professor.getProfessorNumber());
        initComboBoxPreliminaryProject(professor.getProfessorNumber());
    }

    public void initComboBoxEnrollment(int personalNumber) {
        StudentDAO studentDAO = new StudentDAO();
        List<Student> listOfEnrollment = new ArrayList<>();
        try {
            listOfEnrollment = studentDAO.getEnrollmentPerProjectCodirector(personalNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog("No fue posible mostrar la lista de matricula de los alumnos del director", Status.WARNING);
        }
        ObservableList<Student> observableListStudentEnrollment = FXCollections.observableArrayList(listOfEnrollment);
        comboBoxEnrollment.setItems(observableListStudentEnrollment);
    }

    public void initComboBoxPreliminaryProject(int codirectorNumber) {
        PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList<>();
        try {
            listOfPreliminaryProject = preliminaryProjectDAO.getNameProjectPerCodirector(codirectorNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog("No fue posible mostrar la lista de proyectos del director", Status.WARNING);
        }
        ObservableList<PreliminaryProject> observableListPreliminaryProject = FXCollections.observableArrayList(listOfPreliminaryProject);
        comboBoxPreliminaryProject.setItems(observableListPreliminaryProject);
    }

    @FXML
    void generateReportOnAction(ActionEvent event) {
        GenerateSubreport generateSubreport = new GenerateSubreport(REPORT_FILE)
                .addSubreport("subreporte1", SUBREPORT1_FILE)
                .addSubreport("subreporte2", SUBREPORT2_FILE)
                .addSubreport("subreporte3", SUBREPORT3_FILE)
                .addParameters(getParameters());
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

            File selectedFile = fileChooser.showSaveDialog(comboBoxEnrollment.getScene().getWindow());
            if (selectedFile != null) {
                JasperPrint jasperPrint = generateSubreport.printSubReports();
                JasperExportManager.exportReportToPdfFile(jasperPrint, selectedFile.getAbsolutePath());
                DialogGenerator.getConfirmationDialog("Reporte guardado", "Reporte");
            }
        } catch (JRException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    void setEnrollmentOnImageView(ActionEvent event) {
        generatePreview();
    }

    @FXML
    void setPreliminayProjectOnImageView(ActionEvent event) {
        generatePreview();
    }

    public void generatePreview() {
        GenerateSubreport generateSubreport = new GenerateSubreport(REPORT_FILE)
                .addSubreport("subreporte1", SUBREPORT1_FILE)
                .addSubreport("subreporte2", SUBREPORT2_FILE)
                .addSubreport("subreporte3", SUBREPORT3_FILE)
                .addParameters(getParameters());
        try {
            imageView.setImage(generateSubreport.getPreviewSubReports());
            scrollPane.setContent(imageView);
        } catch (JRException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        Student selectedEnrollment = comboBoxEnrollment.getSelectionModel().getSelectedItem();
        PreliminaryProject selectedPreliminaryProject = comboBoxPreliminaryProject.getSelectionModel().getSelectedItem();

        if (selectedEnrollment != null && selectedPreliminaryProject != null) {
            parameters.put("matricula", selectedEnrollment.getEnrollment());
            parameters.put("anteproyecto", selectedPreliminaryProject.getProjectName());
        } else if (selectedEnrollment != null) {
            parameters.put("matricula", selectedEnrollment.getEnrollment());
            parameters.put("anteproyecto", null);
        } else if (selectedPreliminaryProject != null) {
            parameters.put("matricula", null);
            parameters.put("anteproyecto", selectedPreliminaryProject.getProjectName());
        } else {
            DialogGenerator.getDialog("No se pudo obtener ningún elemento", Status.WARNING);
        }
        parameters.put("logoSGPG", getClass().getResourceAsStream(IMAGE_SGPG));
        parameters.put("logoUV", getClass().getResourceAsStream(IMAGE_UV));
        return parameters;
    }
}
