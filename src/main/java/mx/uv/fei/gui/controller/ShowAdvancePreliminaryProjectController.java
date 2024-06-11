/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uv.fei.logic.Advance;
import mx.uv.fei.logic.AdvanceDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.ClassHolder;
import mx.uv.fei.logic.Document;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;

/**
 * FXML Controller class permite mostrar los avances hechos por un estudiante de
 * un anteproyecto en especifico que fue seleccionado previamente.
 *
 * @author alexs
 */
public class ShowAdvancePreliminaryProjectController implements Initializable {

    @FXML
    private ListView<Advance> listViewAdvance;
    @FXML
    private Label labelDateStart;
    @FXML
    private Label labelDateFinish;
    @FXML
    private Label labelDescription;
    @FXML
    private ListView<Document> listViewEvidence;
    @FXML
    private Pane paneMessage;
    @FXML
    private Button buttonValidate;
    @FXML
    private Label labelNameDirector;
    @FXML
    private Label labelState;

    private ClassHolder classHolder = ClassHolder.getInstance();
    private Professor professor = new Professor();
    private List<Blob> listOfDocument;
    private int idAdvance;
    private final String VALIDATE = "Validado";
    private final String REJECTED = "Rechazado";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAdvance();
        paneMessage.setVisible(false);
        String email = classHolder.getUser().getEmail();
        try {
            initComponents(email);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void initComponents(String email) throws DAOException {
        ProfessorDAO professorDAO = new ProfessorDAO();
        professor = professorDAO.getInformation(email);
        labelNameDirector.setText(String.format("%s", professor.getName().split(" ")));
    }

    @FXML
    private void selectedAdvance(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Advance selectedAdvance = listViewAdvance.getSelectionModel().getSelectedItem();
            if (selectedAdvance != null) {
                try {
                    loadAvanceDocuments(selectedAdvance);
                    initComponents();
                } catch (DAOException ex) {
                    App.getLogger().warn(ex.getMessage());
                    DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
                }
            } else {
                DialogGenerator.getDialog("Debes seleccionar un avance para consultar sus evidencias", Status.WARNING);
            }
        }
    }

    @FXML
    private void selectedEvidence(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Document selectedDocument = listViewEvidence.getSelectionModel().getSelectedItem();
            if (selectedDocument != null) {
                try {
                    Blob selectedBlob = selectedDocument.getBlob();
                    String fileName = retrieveFileNameFromBlob(selectedBlob);
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Guardar archivo");
                    fileChooser.setInitialFileName(fileName);
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
                    File file = fileChooser.showSaveDialog(listViewEvidence.getScene().getWindow());
                    if (file != null) {
                        InputStream inputStream = selectedBlob.getBinaryStream();
                        FileOutputStream outputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        inputStream.close();
                        outputStream.close();
                    }
                } catch (IOException | SQLException ex) {
                    App.getLogger().warn(ex.getMessage());
                    DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
                }
            }
        }
    }

    @FXML
    private void validateAdvanceOnAction(ActionEvent event) {
        try {
            AdvanceDAO advanceDAO = new AdvanceDAO();
            Advance advance = listViewAdvance.getSelectionModel().getSelectedItem();
            if (advanceDAO.validationAdvanceDirector(advanceDAO.getIdAdvance(advance.toString()),
                    VALIDATE) > 0) {
                DialogGenerator.getConfirmationDialog("Avance validado", "Validación de avance");
            }
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void rejectAdvanceOnAction(ActionEvent event) throws DAOException {
        String enrollment = classHolder.getStudent().getEnrollment();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FeedbackStudent.fxml"));
            Parent root = loader.load();
            FeedbackStudentController controlador = loader.getController();
            controlador.initComponents(enrollment);
            App.newView(root);
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }

        AdvanceDAO advanceDAO = new AdvanceDAO();
        Advance advance = listViewAdvance.getSelectionModel().getSelectedItem();
        if (advanceDAO.validationAdvanceDirector(advanceDAO.getIdAdvance(advance.toString()),
                REJECTED) > 0) {
            DialogGenerator.getConfirmationDialog("Avance rechazado y correo enviado a estudiante", "Rechazo de avance");
        }
    }

    public void loadAdvance() {
        AdvanceDAO advanceDAO = new AdvanceDAO();
        try {
            String enrollment = classHolder.getStudent().getEnrollment();
            List<Advance> listOfAdvance = advanceDAO.showNameAdvance(enrollment);
            listViewAdvance.getItems().clear();
            listViewAdvance.getItems().addAll(listOfAdvance);
            if (listOfAdvance.isEmpty()) {
                DialogGenerator.getDialog("El estudiante seleccionado no ha entregado avances.", Status.WARNING);
                Platform.runLater(() -> {
                    Stage stage = (Stage) buttonValidate.getScene().getWindow();
                    stage.close();
                });
            }
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void loadAvanceDocuments(Advance advance) {
        AdvanceDAO advanceDAO = new AdvanceDAO();
        try {
            idAdvance = advanceDAO.getIdAdvance(advance.getTitle());
            listOfDocument = advanceDAO.showAdvanceDocuments(idAdvance);
            ObservableList<Document> observableList = FXCollections.observableArrayList();
            if (!listOfDocument.isEmpty()) {
                paneMessage.setVisible(false);
                for (int i = 0; i < listOfDocument.size(); i++) {
                    URL imagePath = getClass().getResource("/images/Icon PDF.png");
                    Image image = new Image(imagePath.toString());
                    String fileName = "Evidencia " + (i + 1);
                    Document document = new Document(image, fileName);
                    document.setBlob(listOfDocument.get(i));
                    observableList.add(document);
                }
            } else {
                paneMessage.setVisible(true);
            }
            listViewEvidence.setItems(observableList);
            listViewEvidence.setCellFactory(listView -> new DocumentCell());
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private String retrieveFileNameFromBlob(Blob blob) {
        try {
            if (blob != null) {
                String fullName = blob.getBinaryStream().toString();
                int startIndex = fullName.lastIndexOf('/') + 1;
                int endIndex = fullName.length();
                return fullName.substring(startIndex, endIndex);
            }
        } catch (SQLException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        return "";
    }

    public void initComponents() throws DAOException {
        AdvanceDAO advanceDAO = new AdvanceDAO();
        Advance advance = new Advance();
        advance = advanceDAO.getDataAdvance(idAdvance);
        setInformation(advance);
    }

    public void setInformation(Advance advance) {
        this.labelDateFinish.setText(String.format("%s", advance.getEndDate()));
        this.labelDateStart.setText(String.format("%s", advance.getStartDate()));
        this.labelDescription.setText(String.format("%s", advance.getDescription()));
        this.labelState.setText(String.format("%s", advance.getAdvanceState()));
        labelDescription.setWrapText(true);
    }

    @FXML
    private void exitOnAction(MouseEvent event) {
        Stage stage = (Stage) this.buttonValidate.getScene().getWindow();
        stage.close();
    }

}
