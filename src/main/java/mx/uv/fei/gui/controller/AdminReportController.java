/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.File;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import mx.uv.fei.logic.Course;
import mx.uv.fei.logic.CourseDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Status;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import java.io.IOException;
import javafx.scene.control.Button;
import mx.uv.fei.logic.GenerateReport;

/**
 * FXML Controller class
 * Esta clase se encarga de generar los reportes para el administrador.
 * @author alexs
 */
public class AdminReportController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private ComboBox<Course> comboBoxNrc;
    @FXML
    private ComboBox<String> comboBoxPeriod;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ImageView imageView;
    @FXML
    private Button buttonGenerateReport;

    private final String REPORT_FILE = "/reports/AdminReport.jasper";
    private final String IMAGE_SGPG = "/images/SGPGyER-azul.png";
    private final String IMAGE_UV = "/images/Uv-logo.png";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboBoxNrc();
        initComboBoxPeriod();
        if (!comboBoxNrc.getItems().isEmpty()) {
            buttonGenerateReport.setDisable(false);
            comboBoxPeriod.setDisable(false);
            comboBoxNrc.setDisable(false);
        }
    }

    @FXML
    void generateReportOnAction(ActionEvent event) {
        GenerateReport generateReport = new GenerateReport();
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

            File selectedFile = fileChooser.showSaveDialog(comboBoxNrc.getScene().getWindow());
            if (selectedFile != null) {
                JasperExportManager.exportReportToPdfFile(generateReport.getReport(REPORT_FILE, getParameters()),
                        selectedFile.getAbsolutePath());
                DialogGenerator.getConfirmationDialog("Reporte guardado", "Reporte");
            }
        } catch (JRException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    void setNrcOnImage(ActionEvent event) {
        generatePreview();
    }

    @FXML
    void setPeriodOnImage(ActionEvent event) {
        generatePreview();
    }

    public void generatePreview() {
        try {
            GenerateReport generateReport = new GenerateReport();
            imageView.setImage(generateReport.getPreviewReport(REPORT_FILE, getParameters()));
            scrollPane.setContent(imageView);
        } catch (JRException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public Map getParameters() {
        Map parameters = new HashMap();
        parameters.put("NRC", comboBoxNrc.getSelectionModel().getSelectedItem());
        parameters.put("Period", comboBoxPeriod.getSelectionModel().getSelectedItem());
        parameters.put("logoSGPG", this.getClass().getResourceAsStream(IMAGE_SGPG));
        parameters.put("logoUV", this.getClass().getResourceAsStream(IMAGE_UV));
        return parameters;
    }

    private void initComboBoxNrc() {
        CourseDAO courseDAO = new CourseDAO();
        List<Course> listOfCourse = new ArrayList<>();
        try {
            listOfCourse = courseDAO.getAllCourse();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog("No fue posible mostrar la lista de NRC", Status.WARNING);
        }
        ObservableList<Course> observableListCourse = FXCollections.observableArrayList(listOfCourse);
        comboBoxNrc.setItems(observableListCourse);
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
        String[] arrayPeriod = new String[listOfCourse.size()];
        for (int i = 0; i < listOfCourse.size(); i++) {
            Course courseArray = listOfCourse.get(i);
            String coursePeriod = courseArray.getPeriod();
            arrayPeriod[i] = coursePeriod;
        }
        ObservableList<String> observableListPeriod = FXCollections.observableArrayList(arrayPeriod);
        comboBoxPeriod.setItems(observableListPeriod);
    }
}
