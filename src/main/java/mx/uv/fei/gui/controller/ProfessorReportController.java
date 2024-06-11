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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import mx.uv.fei.logic.Course;
import mx.uv.fei.logic.CourseDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;
import mx.uv.fei.logic.GenerateSubreport;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * FXML Controller class 0
 * este controlador permite generar un reporte para el profesor.
 * @author Palom
 */
public class ProfessorReportController implements Initializable {

    @FXML
    private Label labelName;
    @FXML
    private ComboBox<Course> comboBoxNrc;
    @FXML
    private ComboBox<Student> comboBoxEnrollment;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ImageView imageView;

    private Professor professor = new Professor();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    private final String REPORT_FILE = "/reports/ProfessorReport.jrxml";
    private final String SUBREPORT1_FILE = "/reports/SubreportProfessor1.jrxml";
    private final String SUBREPORT2_FILE = "/reports/SubreportProfessor2.jrxml";
    private final String SUBREPORT3_FILE = "/reports/SubreportProfessor3.jrxml";
    private final String IMAGE_SGPG = "/images/SGPGyER-azul.png";
    private final String IMAGE_UV = "/images/Uv-logo.png";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initComponents(String email) throws DAOException {
        ProfessorDAO professorDAO = new ProfessorDAO();
        professor = professorDAO.getInformation(email);
        labelName.setText(String.format("%s", professor.getName().split(" ")));
        initComboBoxNrc(professor.getProfessorNumber());
        initComboBoxEnrollment(professor.getProfessorNumber());
    }

    public void initComboBoxNrc(int personalNumber) {
        CourseDAO courseDAO = new CourseDAO();
        List<Course> listOfNrc = new ArrayList<>();
        try {
            listOfNrc = courseDAO.getNrcPerProfessor(personalNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog("No fue posible mostrar la lista de NRC del profesor", Status.WARNING);
        }
        ObservableList<Course> observableListCourse = FXCollections.observableArrayList(listOfNrc);
        comboBoxNrc.setItems(observableListCourse);
    }

    public void initComboBoxEnrollment(int personalNumber) {
        StudentDAO studentDAO = new StudentDAO();
        List<Student> listOfEnrollment = new ArrayList<>();
        try {
            listOfEnrollment = studentDAO.getEnrollmentPerCourse(personalNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog("No fue posible mostrar la lista de matricula de los alumnos del profesor", Status.WARNING);
        }
        ObservableList<Student> observableListStudent = FXCollections.observableArrayList(listOfEnrollment);
        comboBoxEnrollment.setItems(observableListStudent);
    }

    @FXML
    private void generateReportOnAction(ActionEvent event) {
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
    private void setNrcOnImagen(ActionEvent event) {
        generatePreview();
    }

    @FXML
    private void setEnrollmentOnImage(ActionEvent event) {
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
        Course selectedNrc = comboBoxNrc.getSelectionModel().getSelectedItem();

        if (selectedEnrollment != null && selectedNrc != null) {
            parameters.put("matricula", selectedEnrollment.getEnrollment());
            parameters.put("nrc", selectedNrc.getNrc());
        } else if (selectedEnrollment != null) {
            parameters.put("matricula", selectedEnrollment.getEnrollment());
            parameters.put("nrc", null);
        } else if (selectedNrc != null) {
            parameters.put("matricula", null);
            parameters.put("nrc", selectedNrc.getNrc());
        } else {
            DialogGenerator.getDialog("No se pudo obtener ningún elemento", Status.WARNING);
        }
        parameters.put("logoSGPG", getClass().getResourceAsStream(IMAGE_SGPG));
        parameters.put("logoUV", getClass().getResourceAsStream(IMAGE_UV));
        return parameters;
    }
}
