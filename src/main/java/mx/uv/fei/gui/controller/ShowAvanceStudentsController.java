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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uv.fei.logic.Advance;
import mx.uv.fei.logic.AdvanceDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Document;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * muestra los avances hechos por un estudiante, así como los documentos
 * adjuntos por el estudiante.
 * @author aresj
 */

public class ShowAvanceStudentsController implements Initializable {

    @FXML
    private ListView<Document> listViewEvidence;
    @FXML
    private ListView<Advance> listViewAdvance;
    @FXML
    private Label labelDateStart;
    @FXML
    private Label labelDateFinish;
    @FXML
    private Label labelDescription;
    @FXML
    private ImageView imageViewExit;
    @FXML
    private Pane paneMessage;

    ClassHolder classHolder = ClassHolder.getInstance();
    private int idAdvance;
    private String enrollment;
    private List<Blob> listOfDocument;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAdvance();
        paneMessage.setVisible(false);
    }

    public void loadAdvance() {
        AdvanceDAO advanceDAO = new AdvanceDAO();
        try {
            enrollment = classHolder.getStudent().getEnrollment();
            List<Advance> listOfAdvance = advanceDAO.showNameAdvance(enrollment);
            listViewAdvance.getItems().clear();
            listViewAdvance.getItems().addAll(listOfAdvance);
            if (listOfAdvance.isEmpty()) {
                DialogGenerator.getDialog("El estudiante seleccionado no ha entregado avances.", Status.WARNING);
                Platform.runLater(() -> {
                    Stage stage = (Stage) imageViewExit.getScene().getWindow();
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
            ObservableList<Document> observableListDocument = FXCollections.observableArrayList();
            if (!listOfDocument.isEmpty()) {
                paneMessage.setVisible(false);
                for (int i = 0; i < listOfDocument.size(); i++) {
                    URL imagePath = getClass().getResource("/images/Icon PDF.png");
                    Image image = new Image(imagePath.toString());
                    String fileName = "Evidencia " + (i + 1);
                    Document document = new Document(image, fileName);
                    document.setBlob(listOfDocument.get(i));
                    observableListDocument.add(document);
                }
            } else {
                paneMessage.setVisible(true);
            }
            listViewEvidence.setItems(observableListDocument);
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
    private void selectedAdvance(MouseEvent event) throws DAOException {
        if (event.getClickCount() == 2) {
            Advance selectedAdvance = listViewAdvance.getSelectionModel().getSelectedItem();
            if (selectedAdvance != null) {
                loadAvanceDocuments(selectedAdvance);
                initComponents();
            } else {
                DialogGenerator.getDialog("Debes seleccionar un avance para consultar sus evidencias", Status.WARNING);
            }
        }
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
        labelDescription.setWrapText(true);
    }

    @FXML
    private void exitOnAction(MouseEvent event) {
        Stage stage = (Stage) this.imageViewExit.getScene().getWindow();
        stage.close();
    }
}
