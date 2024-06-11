/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Email;
import mx.uv.fei.logic.PreliminaryProjectDAO;

import mx.uv.fei.logic.ProfessorDAO;

/**
 * FXML Controller class
 * este controlador envia un correo cuando se rechaza un anteproyecto por
 * parde del cuerpo académico.
 * @author Palom
 */
public class SendEmailController implements Initializable {

    @FXML
    private TextArea textAreaReasons;
    @FXML
    private Button buttonSend;
    
    private String projectTitle;
    private String directorEmail;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void initComponents(String title) throws DAOException {
        ProfessorDAO professorDAO = new ProfessorDAO();
        directorEmail = professorDAO.getEmailDirector(title);
        projectTitle = title;
        textAreaReasons.setWrapText(true);
    }

    @FXML
    private void sendOnAction(ActionEvent event) throws DAOException {
        Email email = new Email();
        String address = this.directorEmail;
        String subject = "Comentarios a " + this.projectTitle;
        String text = this.textAreaReasons.getText();
        String content= email.formatMessageToDirector(this.projectTitle, text);
        email.sendEmail(address, subject, content);
        PreliminaryProjectDAO preliminaryProject = new PreliminaryProjectDAO();
        preliminaryProject.updateStatePreliminaryProject("Rechazado", this.projectTitle);
        DialogGenerator.getConfirmationDialog("El proyecto ha cambiado a estado rechazado y se han enviado los"
                + " comentarios al director", "Aviso");
        closeStage();
    }
    
    public void closeStage(){
        Stage stage = (Stage)this.buttonSend.getScene().getWindow();
        stage.close();
    }
}
