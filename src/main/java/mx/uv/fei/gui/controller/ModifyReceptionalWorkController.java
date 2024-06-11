/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.PreliminaryProject;
import mx.uv.fei.logic.PreliminaryProjectDAO;
import mx.uv.fei.logic.ReceptionalWork;
import mx.uv.fei.logic.ReceptionalWorkDAO;
import mx.uv.fei.logic.Professor;
import mx.uv.fei.logic.ProfessorDAO;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.ClassHolder;

/**
 * FXML Controller class
 * permite modificar la información de un trabajo recepcional.
 * @author aresj
 */
public class ModifyReceptionalWorkController implements Initializable {

    @FXML
    private TableColumn<ReceptionalWork, String> columnReceptionalWorkName;
    @FXML
    private TableColumn<ReceptionalWork, String> columnReceptionalWorkDate;
    @FXML
    private TextField textFieldReceptionalWorkName;
    @FXML
    private ComboBox<PreliminaryProject> comboBoxPreliminaryProject;
    @FXML
    private ComboBox<Professor> comboBoxSynodal;
    @FXML
    private DatePicker datePickerDateStart;
    @FXML
    private TableView<ReceptionalWork> tableViewReceptionalWork;
    @FXML
    private Pane paneDates;
    @FXML
    private Button buttonModifyReceptionalWork;
    @FXML
    private DatePicker datePickerDateFinish;

    private String receptionalWorkName;
    private int directorNumber;
    ClassHolder classHolder = ClassHolder.getInstance();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            directorNumber = professorDAO.getProfessorNumberByEmail(classHolder.getUser().getEmail());
            columnReceptionalWorkName.setCellValueFactory(new PropertyValueFactory<>("receptionalWorkName"));
            columnReceptionalWorkDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            tableViewReceptionalWork.setItems(getTableReceptionalWork());
            validateDatePicker();
            paneDates.setVisible(false);
            if (tableViewReceptionalWork.getItems().isEmpty()) {
                throw new IllegalArgumentException("La tabla de trabajos recepcionales está vacía."
                        + " Crea un trabajo recepcional para poder acceder a esta opción.");
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private void validateDatePicker() {
        datePickerDateStart.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });
        datePickerDateStart.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBefore(LocalDate.now())) {
                datePickerDateStart.setValue(LocalDate.now());
            }
            if (datePickerDateFinish.getValue() != null && datePickerDateFinish.getValue().isBefore(newValue)) {
                datePickerDateFinish.setValue(null);
            }
        });

        datePickerDateStart.setOnAction(event -> {
            LocalDate selectedDate = datePickerDateStart.getValue();
            datePickerDateFinish.setDayCellFactory(datePicker -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setDisable(empty || item.isBefore(selectedDate.plusDays(1)));
                }
            });
        });
    }

    @FXML
    private void selectedRow(MouseEvent event) {
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            directorNumber = professorDAO.getProfessorNumberByEmail(classHolder.getUser().getEmail());
            ReceptionalWork receptionalWork = this.tableViewReceptionalWork.getSelectionModel().getSelectedItem();
            if (receptionalWork != null) {
                receptionalWorkName = receptionalWork.getReceptionalWorkName();
                this.textFieldReceptionalWorkName.setText(receptionalWork.getReceptionalWorkName());
                this.datePickerDateStart.setValue(receptionalWork.getStartDate().toLocalDate());
                initComponents(receptionalWork.getReceptionalWorkName());
                enableInformation(receptionalWorkName);
                initComboBoxPreliminaryProject();
                initComboBoxSynodal();
                datePickerDateFinish.setEditable(false);
                datePickerDateStart.setEditable(false);
                comboBoxPreliminaryProject.setEditable(false);
                comboBoxSynodal.setEditable(false);
                paneDates.setVisible(true);
            } else {
                throw new IllegalArgumentException("Selecciona una columna con datos para poder modificar");
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private void enableInformation(String receptionalWorkName) {
        try {
            ReceptionalWorkDAO receptionalWorkDAO = new ReceptionalWorkDAO();
            ReceptionalWork receptionalWork = receptionalWorkDAO.getDataReceptionalWork(receptionalWorkDAO.getIdReceptionalWork(receptionalWorkName));
            if (receptionalWork.getEndDate() != null) {
                datePickerDateFinish.setValue(receptionalWork.getEndDate().toLocalDate());
                textFieldReceptionalWorkName.setEditable(false);
                comboBoxPreliminaryProject.setEditable(false);
                comboBoxSynodal.setEditable(false);
                datePickerDateStart.setEditable(false);
                datePickerDateFinish.setEditable(false);
                buttonModifyReceptionalWork.setDisable(true);
                DialogGenerator.getDialog("El trabajo recepcional seleccionado ya a concluido, "
                        + "por lo tanto no puedes modificarlo", Status.WARNING);
            } else {
                datePickerDateFinish.setValue(null);
                textFieldReceptionalWorkName.setEditable(true);
                comboBoxPreliminaryProject.setEditable(true);
                comboBoxSynodal.setEditable(true);
                datePickerDateStart.setEditable(true);
                datePickerDateFinish.setEditable(true);
                buttonModifyReceptionalWork.setDisable(false);
            }
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    private void modifyReceptionalWorkOnAction(ActionEvent event) {
        LocalDate startDate = datePickerDateStart.getValue();
        LocalDate deadline = datePickerDateFinish.getValue();
        LocalDate currentDate = LocalDate.now();
        try {
            ReceptionalWorkDAO receptionalWorkDAO = new ReceptionalWorkDAO();
            String receptionalWorkNameOriginal = tableViewReceptionalWork.getSelectionModel().getSelectedItem().getReceptionalWorkName();
            receptionalWorkName = textFieldReceptionalWorkName.getText();
            if (receptionalWorkName.equals(receptionalWorkNameOriginal)) {
                if (datePickerDateFinish.getValue() != null && datePickerDateStart.getValue() != null) {
                    if (startDate.isBefore(currentDate)) {
                        DialogGenerator.getDialog("La fecha de inicio debe ser posterior a la fecha actual",
                                Status.WARNING);
                    } else if (deadline.isBefore(startDate)) {
                        DialogGenerator.getDialog("La fecha de cierre debe ser posterior a la fecha de inicio",
                                Status.WARNING);
                    } else {
                        invokeUpdate(getDataFromForm());
                        buttonModifyReceptionalWork.setDisable(true);
                    }
                } else if (datePickerDateStart.getValue() != null && datePickerDateFinish.getValue() == null) {
                    if (startDate.isBefore(currentDate)) {
                        DialogGenerator.getDialog("La fecha de inicio debe ser posterior a la fecha actual",
                                Status.WARNING);
                    } else {
                        invokeUpdate(getDataFromForm());
                        buttonModifyReceptionalWork.setDisable(true);
                    }
                }
            } else {
                if (receptionalWorkDAO.isReceptionalWorkIsExisting(receptionalWorkName, directorNumber) == false) {
                    if (datePickerDateFinish.getValue() != null && datePickerDateStart.getValue() != null) {
                        if (startDate.isBefore(currentDate)) {
                            DialogGenerator.getDialog("La fecha de inicio debe ser posterior a la fecha actual",
                                    Status.WARNING);
                        } else if (deadline.isBefore(startDate)) {
                            DialogGenerator.getDialog("La fecha de cierre debe ser posterior a la fecha de inicio",
                                    Status.WARNING);
                        } else {
                            invokeUpdate(getDataFromForm());
                            buttonModifyReceptionalWork.setDisable(true);
                        }
                    } else if (datePickerDateStart.getValue() != null && datePickerDateFinish.getValue() == null) {
                        if (startDate.isBefore(currentDate)) {
                            DialogGenerator.getDialog("La fecha de inicio debe ser posterior a la fecha actual",
                                    Status.WARNING);
                        } else {
                            invokeUpdate(getDataFromForm());
                            buttonModifyReceptionalWork.setDisable(true);
                        }
                    }
                } else {
                    DialogGenerator.getDialog("No se puede duplicar nombres de trabajo recepcional", Status.WARNING);
                }
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private void invokeUpdate(ReceptionalWork receptionalWork) throws DAOException {
        String receptionalWorkNameOriginal = tableViewReceptionalWork.getSelectionModel().getSelectedItem().getReceptionalWorkName();
        Professor selectedSynodal = comboBoxSynodal.getSelectionModel().getSelectedItem();
        PreliminaryProject selectedPreliminaryProject = comboBoxPreliminaryProject.getSelectionModel().getSelectedItem();
        if (selectedSynodal == null) {
            throw new IllegalArgumentException("Debe seleccionar un Sinodal para el trabajo recepcional.");
        } else if (selectedPreliminaryProject == null) {
            throw new IllegalArgumentException("Debe seleccionar un anteproyecto para el trabajo recepcional.");
        } else if (receptionalWork.getStartDate() == null) {
            throw new IllegalArgumentException("Debe seleccionar la fecha en la cual iniciará el trabajo recepcional.");
        } else {
            String nameSynodal = selectedSynodal.toString();
            String namePreliminaryProject = selectedPreliminaryProject.toString();
            ReceptionalWorkDAO receptionalWorkDAO = new ReceptionalWorkDAO();
            PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
            ProfessorDAO professorDAO = new ProfessorDAO();
            int preliminaryProjectId = preliminaryProjectDAO.getIdPreliminaryProject(namePreliminaryProject);
            int synodalNumber = professorDAO.getProfessorNumber(nameSynodal);
            int receptionalWorkId = receptionalWorkDAO.getIdReceptionalWork(receptionalWorkNameOriginal);
            receptionalWorkDAO.updateReceptionalWork(receptionalWork, preliminaryProjectId, directorNumber, synodalNumber, receptionalWorkId);
            DialogGenerator.getConfirmationDialog("El Trabajo Recepcional fue actualizado exitosamente",
                    "El Trabajo Recepcional actualizado");
            tableViewReceptionalWork.setItems(getTableReceptionalWork());
        }
    }

    private ReceptionalWork getDataFromForm() {
        ReceptionalWork receptionalWork = new ReceptionalWork();
        String nameReceptionalWork = textFieldReceptionalWorkName.getText();
        if (nameReceptionalWork.length() > 255) {
            throw new IllegalArgumentException("El nombre no puede exceder los 255 caracteres. Se ingresaron "
                    + nameReceptionalWork.length() + " caracteres.");
        }
        LocalDate localDateStart = datePickerDateStart.getValue();
        if (localDateStart == null) {
            throw new IllegalArgumentException("Debe seleccionar la fecha en la cual iniciará el trabajo recepcional.");
        }
        java.sql.Date dateStart = java.sql.Date.valueOf(localDateStart);
        LocalDate localDateFinish = datePickerDateFinish.getValue();
        java.sql.Date dateFinish = (localDateFinish != null) ? java.sql.Date.valueOf(localDateFinish) : null;
        receptionalWork.setReceptionalWorkName(nameReceptionalWork);
        receptionalWork.setStartDate(dateStart);
        receptionalWork.setEndDate(dateFinish);
        return receptionalWork;
    }

    public void initComponents(String receptionalWorkName) {
        try {
            Professor director = new Professor();
            Professor synodal = new Professor();
            PreliminaryProject preliminaryProject = new PreliminaryProject();
            PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
            ReceptionalWorkDAO receptionalWorkDAO = new ReceptionalWorkDAO();
            List<Professor> listOfDirector = new ArrayList();
            List<Professor> listOfSynodal = new ArrayList();
            List<PreliminaryProject> listOfPreliminaryProject = new ArrayList();
            director.setName(receptionalWorkDAO.getDirector(receptionalWorkName));
            synodal.setName(receptionalWorkDAO.getSynodal(receptionalWorkName));
            preliminaryProject.setProjectName(preliminaryProjectDAO.getPreliminaryProjectNameWithReceptionalWork(
                    receptionalWorkName));
            listOfDirector.add(director);
            listOfSynodal.add(synodal);
            listOfPreliminaryProject.add(preliminaryProject);
            this.comboBoxSynodal.getSelectionModel().select(synodal);
            this.comboBoxPreliminaryProject.getSelectionModel().select(preliminaryProject);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private ObservableList<ReceptionalWork> getTableReceptionalWork() {
        ReceptionalWorkDAO receptionalWorkDAO = new ReceptionalWorkDAO();
        List<ReceptionalWork> listOfReceptionalWork = new ArrayList();
        try {
            listOfReceptionalWork = receptionalWorkDAO.showReceptionalWorkFromDirector(directorNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<ReceptionalWork> observableListReceptionalWork = FXCollections.observableArrayList(listOfReceptionalWork);
        return observableListReceptionalWork;
    }

    public void initComboBoxPreliminaryProject() {
        PreliminaryProjectDAO preliminaryProjectDAO = new PreliminaryProjectDAO();
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList();
        try {
            listOfPreliminaryProject = preliminaryProjectDAO.getPreliminaryProjectsFromDirector(directorNumber);
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<PreliminaryProject> observableListPreliminaryProject = FXCollections.observableArrayList(
                listOfPreliminaryProject);
        comboBoxPreliminaryProject.setItems(observableListPreliminaryProject);
    }

    public void initComboBoxSynodal() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        List<Professor> listOfSynodal = new ArrayList();
        try {
            listOfSynodal = professorDAO.getSynodal();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<Professor> observableListSynodal = FXCollections.observableArrayList(listOfSynodal);
        comboBoxSynodal.setItems(observableListSynodal);
    }
}
