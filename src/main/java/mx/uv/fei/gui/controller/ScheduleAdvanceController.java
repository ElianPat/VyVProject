/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mx.uv.fei.logic.Advance;
import mx.uv.fei.logic.AdvanceDAO;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.StudentDAO;
import mx.uv.fei.logic.ClassHolder;
import mx.uv.fei.logic.Student;

/**
 * FXML Controller class
 * este controlador permite programar avances para los estudiantes.
 * @author alexs
 */
public class ScheduleAdvanceController implements Initializable {

    @FXML
    private ImageView imageViewExit;
    @FXML
    private DatePicker datePickerDeadLineDatePick;
    @FXML
    private DatePicker datePickerStartDatePick;
    @FXML
    private TextArea textAreaDescription;
    @FXML
    private TextField textFieldTitle;
    @FXML
    private Label labelEducativeExperience;
    @FXML
    private Label labelName;

    private ClassHolder classHolder = ClassHolder.getInstance();
    private HomeStudentController homeStudent;
    private final String TIME_FORMAT = "23:59:00";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setStyleComponents();
        String email = classHolder.getUser().getEmail();
        initComponents(email);
    }

    public void setHomeStudent(HomeStudentController homeStudent) {
        this.homeStudent = homeStudent;
    }

    @FXML
    void scheduleAdvanceOnAction(ActionEvent event) {
        try {
            if (datePickerStartDatePick.getValue() == null || datePickerDeadLineDatePick.getValue() == null) {
                DialogGenerator.getDialog("Debe seleccionar una fecha", Status.WARNING);
            } else {
                LocalDate startDate = datePickerStartDatePick.getValue();
                LocalDate deadline = datePickerDeadLineDatePick.getValue();
                LocalDate currentDate = LocalDate.now();

                if (startDate.isBefore(currentDate)) {
                    DialogGenerator.getDialog("La fecha de inicio debe ser posterior a la fecha actual", 
                            Status.WARNING);
                } else if (deadline.isBefore(startDate)) {
                    DialogGenerator.getDialog("La fecha de cierre debe ser posterior a la fecha de inicio", 
                            Status.WARNING);
                } else {
                    invokeScheduleAdvance(getDataFromForm());
                    Stage stage = (Stage) this.imageViewExit.getScene().getWindow();
                    stage.close();
                }
            }
        } catch (DAOException | IllegalArgumentException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    @FXML
    void exitOnAction(MouseEvent event) {
        Stage stage = (Stage) this.imageViewExit.getScene().getWindow();
        stage.close();
    }

    public void setStyleComponents() {
        textFieldTitle.setStyle("-fx-control-inner-background: #0B5569;-fx-prompt-text-fill: white;");
        textAreaDescription.setStyle("-fx-control-inner-background: #0B5569;-fx-prompt-text-fill: white;");
    }

    private void invokeScheduleAdvance(Advance advance) throws DAOException {
        AdvanceDAO advanceDAO = new AdvanceDAO();
        StudentDAO studentDAO = new StudentDAO();
        String student = studentDAO.getEnrollmentStudent(classHolder.getUser().getEmail());
        if (advanceDAO.isAdvanceDuplication(advance.getTitle(), student) == false) {
            if (advanceDAO.scheduleAdvance(advance) > 0) {
                if (advanceDAO.studentAdvance(student, advanceDAO.getAdvanceNumber(
                        textFieldTitle.getText())) > 0) {
                    DialogGenerator.getDialog("Avance programado", Status.SUCCESS);
                    if (homeStudent != null) {
                        homeStudent.updateTable();
                    }
                }
            } else {
                DialogGenerator.getDialog("No fue posible programar el avance", Status.WARNING);
            }
        } else {
            DialogGenerator.getDialog("Avance previamente registrado en el sistema", Status.FATAL);
        }
    }

    public void initComponents(String email) {
        try {
            Student student = new Student();
            StudentDAO studentDAO = new StudentDAO();
            student = studentDAO.getInformation(email);
            this.labelName.setText(String.format("%s", student.getName().split(" ")));
            this.labelEducativeExperience.setText(String.format("%s", student.getEducativeExperience()));
            textAreaDescription.setWrapText(true);
            datePickerDeadLineDatePick.setEditable(false);
            datePickerStartDatePick.setEditable(false);
            validateDatePicker();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }

    private void validateDatePicker() {
        datePickerStartDatePick.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });
        datePickerStartDatePick.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBefore(LocalDate.now())) {
                datePickerStartDatePick.setValue(LocalDate.now());
            }
            if (datePickerDeadLineDatePick.getValue() != null && datePickerDeadLineDatePick.getValue().isBefore(newValue)) {
                datePickerDeadLineDatePick.setValue(null);
            }
        });

        datePickerStartDatePick.setOnAction(event -> {
            LocalDate selectedDate = datePickerStartDatePick.getValue();
            datePickerDeadLineDatePick.setDayCellFactory(datePicker -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setDisable(empty || item.isBefore(selectedDate.plusDays(1)));
                }
            });
        });
    }

    private Advance getDataFromForm() {
        Advance advance = new Advance();

        advance.setTitle(textFieldTitle.getText());
        advance.setDescription(textAreaDescription.getText());

        LocalDate localStartDate = datePickerStartDatePick.getValue();
        if (localStartDate != null) {
            java.sql.Date startDate = java.sql.Date.valueOf(localStartDate);
            advance.setStartDate(startDate);
        }

        LocalDate localDeadLine = datePickerDeadLineDatePick.getValue();
        if (localDeadLine != null) {
            java.sql.Date deadLineDate = java.sql.Date.valueOf(localDeadLine);
            advance.setEndDate(deadLineDate);
        }
        advance.setEndTime(TIME_FORMAT);
        return advance;
    }
}
