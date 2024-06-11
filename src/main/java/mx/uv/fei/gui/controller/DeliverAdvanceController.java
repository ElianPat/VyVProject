/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import mx.uv.fei.logic.Advance;
import mx.uv.fei.logic.AdvanceDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Pdf;
import mx.uv.fei.logic.PdfDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * Permite adjuntar evidencias a los avances.
 * @author Palom
 */
public class DeliverAdvanceController implements Initializable {

    @FXML
    private Label labelEducativeExperience;
    @FXML
    private Label labelName;
    @FXML
    private Label labelTitle;
    @FXML
    private Label labelDateStart;
    @FXML
    private Label labelEndDate;
    @FXML
    private Label labelTime;
    @FXML
    private Label labelFile;
    @FXML
    private TextArea textAreaDescription;

    private String pathFile = "";
    private String studentEnrollment = "";
    private int advanceNumber = 0;
    private final String[] listFiles = {"*.pdf"};

    ClassHolder classHolder = ClassHolder.getInstance();
    ClassHolder advanceHolder = ClassHolder.getInstance();

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Student student = new Student();
            StudentDAO studentDAO = new StudentDAO();
            Advance advance = new Advance();
            AdvanceDAO advanceDAO = new AdvanceDAO();
            student = studentDAO.getInformation(classHolder.getUser().getEmail());
            advance = advanceDAO.getDataAdvance(advanceDAO.getIdAdvance(advanceHolder.getAdvance().getTitle()));
            studentEnrollment = student.getEnrollment();
            advanceNumber = advanceDAO.getIdAdvance(advanceHolder.getAdvance().getTitle());
            initComponents(student, advance);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void initComponents(Student student, Advance advance) {
        labelName.setText(String.format("%s", student.getName().split(" ")));
        labelEducativeExperience.setText(String.format("%s", student.getEducativeExperience()));
        labelTitle.setText(String.format(" %s", advance.getTitle()));
        textAreaDescription.setText(String.format("%s", advance.getDescription()));
        textAreaDescription.setWrapText(true);
        labelDateStart.setText(String.format("%s", advance.getStartDate()));
        labelEndDate.setText(String.format("%s", advance.getEndDate()));
        labelTime.setText(String.format("%s", advance.getEndTime()));
    }

    @FXML
    private void selectFileOnAction(ActionEvent event) {
        select();
    }

    @FXML
    private void deliverAdvanceOnAction(ActionEvent event) throws DAOException {
        File path = new File(pathFile);
        if (pathFile.trim().length() != 0) {
            if (path.length() <= 10000000) {
                try {
                    save(path);
                    pathFile = "";
                    DialogGenerator.getConfirmationDialog("Avance entregado exitosamente", "Entrega exitosa");
                } catch (SQLException | IllegalArgumentException ex) {
                    App.getLogger().error(ex.getMessage());
                    throw new DAOException("No fue posible guardar el archivo ", Status.ERROR);
                }
            } else {
                DialogGenerator.getConfirmationDialog("Archivo fuera de limite", "Error");
            }
        }
    }

    public void save(File path) throws SQLException {
        try {
            Pdf pdf = new Pdf();
            PdfDAO pdfDAO = new PdfDAO();
            try {
                byte[] evidence = new byte[(int) path.length()];
                InputStream input = new FileInputStream(path);
                input.read(evidence);
                pdf.setFile(evidence);
            } catch (IOException ex) {
                pdf.setFile(null);
            }
            pdfDAO.addEvidence(pdf, studentEnrollment, advanceNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void select() {
        FileChooser filechooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF Files", listFiles);
        filechooser.getExtensionFilters().add(extFilter);
        File file = filechooser.showOpenDialog(null);
        if (file != null) {
            this.labelFile.setText(file.getName());
            pathFile = file.getAbsolutePath();
        }
    }

    @FXML
    private void exitOnAction(MouseEvent event) {
        try {
            App.openNewWindow("/fxml/HomeStudent.fxml");
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }
}
