package mx.uv.fei.gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import mx.uv.fei.logic.Advance;
import mx.uv.fei.logic.AdvanceDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Document;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.ClassHolder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import javafx.scene.layout.Pane;
import mx.uv.fei.logic.Student;
import mx.uv.fei.logic.StudentDAO;

/**
 * FXML Controller class
 * Este controlador se encarga de modificar la evidencia o
 * descripción de un avance.
 *
 * @author Palom
 */
public class ModifyAdvanceController implements Initializable {

    @FXML
    private Label labelTitle;
    @FXML
    private Label labelDateStart;
    @FXML
    private Label labelEndDate;
    @FXML
    private Label labelTime;
    @FXML
    private TextArea textAreaDescription;
    @FXML
    private Label labelName;
    @FXML
    private Label labelEducativeExperience;
    @FXML
    private ListView<Document> listViewEvidence;
    @FXML
    private Button buttonReplace;
    @FXML
    private Pane paneMessage;

    private List<Blob> documents;
    private int idAdvance;
    ClassHolder classHolder = ClassHolder.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            String email = classHolder.getUser().getEmail();
            initComponents(email);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    public void initComponents(String email) throws DAOException {
        AdvanceDAO advanceDAO = new AdvanceDAO();
        Advance advance = new Advance();
        Student student = new Student();
        StudentDAO studentDAO = new StudentDAO();
        student = studentDAO.getInformation(email);
        idAdvance = advanceDAO.getIdAdvance(classHolder.getAdvance().getTitle());
        advance = advanceDAO.getDataAdvance(idAdvance);
        setInformation(advance);
        loadAdvanceDocuments(advance);
        this.labelName.setText(String.format("%s", student.getName().split(" ")));
        this.labelEducativeExperience.setText(String.format("%s", student.getEducativeExperience()));
        textAreaDescription.setWrapText(true);
    }

    public void setInformation(Advance advance) {
        this.labelTitle.setText(String.format("%s", advance.getTitle()));
        this.labelEndDate.setText(String.format("%s", advance.getEndDate()));
        this.labelDateStart.setText(String.format("%s", advance.getStartDate()));
        this.labelTime.setText(String.format("%s", advance.getEndTime()));
        this.textAreaDescription.setText(String.format("%s", advance.getDescription()));
    }

    @FXML
    void exitOnAction(MouseEvent event) {
        try {
            App.openNewWindow("/fxml/HomeStudent.fxml");
        } catch (IOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void modifyAdvanceOnAction(ActionEvent event) {
        try {
            String newDescription = textAreaDescription.getText();
            Advance advance = new Advance();
            advance.setDescription(newDescription);
            AdvanceDAO advanceDAO = new AdvanceDAO();
            advanceDAO.updateAdvance(advance.getDescription(), idAdvance);
            DialogGenerator.getConfirmationDialog("La descripción fue actualizada exitosamente. Haz clic en el botón para continuar.", "Aviso");
            Advance updatedAdvance = advanceDAO.getDataAdvance(idAdvance);
            setInformation(updatedAdvance);
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void selectedEvidence(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Document selectedDocument = listViewEvidence.getSelectionModel().getSelectedItem();
            if (selectedDocument != null) {
                try {
                    Blob selectedBlob = selectedDocument.getBlob();
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Guardar archivo");
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

    public void loadAdvanceDocuments(Advance advance) {
        AdvanceDAO advanceDAO = new AdvanceDAO();
        try {
            idAdvance = advanceDAO.getIdAdvance(advance.getTitle());
            documents = advanceDAO.showAdvanceDocuments(idAdvance);
            ObservableList<Document> observableListDocument = FXCollections.observableArrayList();
            if (!documents.isEmpty()) {
                paneMessage.setVisible(false);
                for (int i = 0; i < documents.size(); i++) {
                    URL imagePath = getClass().getResource("/images/Icon PDF.png");
                    Image image = new Image(imagePath.toString());
                    String fileName = "Evidencia " + (i + 1);
                    Document document = new Document(image, fileName);
                    document.setBlob(documents.get(i));
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
        String fullName = "";
        try {
            if (blob != null) {
                fullName = blob.getBinaryStream().toString();
                int startIndex = fullName.lastIndexOf('/') + 1;
                int endIndex = fullName.length();
                fullName = fullName.substring(startIndex, endIndex);
                int extensionIndex = fullName.lastIndexOf('.');
                if (extensionIndex != -1) {
                    fullName = fullName.substring(0, extensionIndex);
                }
            }
        } catch (SQLException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        return fullName;
    }

    @FXML
    private void replaceOnAction(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
            File file = fileChooser.showOpenDialog(buttonReplace.getScene().getWindow());
            if (file != null) {
                long fileSize = file.length();
                long maxSize = 10 * 1024 * 1024;
                if (fileSize > maxSize) {
                    throw new IllegalArgumentException("El archivo supera el tamaño máximo permitido de 10 MB.");
                }
                FileInputStream inputStream = new FileInputStream(file);
                byte[] blobData = createBlobData(inputStream);
                AdvanceDAO advanceDAO = new AdvanceDAO();
                Advance advanceData = new Advance();
                advanceData = advanceDAO.getDataAdvance(idAdvance);
                int documentsAmount = advanceDAO.showAdvanceDocuments(idAdvance).size();
                String newDocumentTitle = "Evidencia " + (documentsAmount + 1);
                advanceDAO.updateEvidence(idAdvance, blobData);
                loadAdvanceDocuments(advanceData);
                DialogGenerator.getConfirmationDialog("Se ha agregado el nuevo archivo con exito", "Confirmación de archivo");
            }
        } catch (IOException | IllegalArgumentException | DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private byte[] createBlobData(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }
}
