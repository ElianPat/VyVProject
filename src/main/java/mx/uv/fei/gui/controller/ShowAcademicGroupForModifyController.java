/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.fei.logic.AcademicGroup;
import mx.uv.fei.logic.AcademicGroupDAO;
import mx.uv.fei.logic.ClassHolder;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Status;

/**
 * FXML Controller class
 * este controlador muestra la información de todos los cuerpos académicos registrados
 * para poder seleccionar uno y así modificar el que se haya seleccionado.
 * @author aresj
 */
public class ShowAcademicGroupForModifyController implements Initializable {

    @FXML
    private TableView<AcademicGroup> tableViewAcademicGroup;
    @FXML
    private Button buttonModifyAcademicGroup;
    @FXML
    private TableColumn<AcademicGroup, String> columnAcademicGroupKey;
    @FXML
    private TableColumn<AcademicGroup, String> columnAcademicGroupName;

    private final ClassHolder classHolder = ClassHolder.getInstance();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnAcademicGroupKey.setCellValueFactory(new PropertyValueFactory<>("key"));
        columnAcademicGroupName.setCellValueFactory(new PropertyValueFactory<>("nameAcademicGroup"));
        tableViewAcademicGroup.setItems(getTableAcademicGroup());
    }

    private ObservableList<AcademicGroup> getTableAcademicGroup() {
        AcademicGroupDAO academicGrouoDAO = new AcademicGroupDAO();
        List<AcademicGroup> listOfAcademicGroup = new ArrayList();
        try {
            listOfAcademicGroup = academicGrouoDAO.getAllAcademicGroup();
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
        ObservableList<AcademicGroup> observableListAcademicGroup = FXCollections.observableArrayList(listOfAcademicGroup);
        return observableListAcademicGroup;
    }

    @FXML
    private void modifyAcademicGroupOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifyAcademicGroup.fxml"));
            Parent root = loader.load();
            ModifyAcademicGroupController controller = loader.getController();
            Scene secene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(secene);
            stage.showAndWait();
            buttonModifyAcademicGroup.setDisable(true);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void selectedRow(MouseEvent event) {
        AcademicGroup academicGroup = new AcademicGroup();
        academicGroup = this.tableViewAcademicGroup.getSelectionModel().getSelectedItem();
        if (academicGroup != null) {
            classHolder.setAcademicGroup(academicGroup);
            buttonModifyAcademicGroup.setDisable(false);
        }
    }
}
