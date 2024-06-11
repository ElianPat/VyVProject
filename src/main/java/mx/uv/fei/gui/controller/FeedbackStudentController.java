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
import mx.uv.fei.logic.Student;

/**
 * FXML Controller class
 *
 * @author Palom
 */
public class FeedbackStudentController implements Initializable {

    @FXML
    private TextArea txtAreaReasons;
    @FXML
    private Button btnSend;
    
    private String emailStudent= ""; 

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
     public void initComponents(String enrollment) throws DAOException {
        Student student = new Student(); 
        String email = student.makeEmail(enrollment);
        emailStudent= email; 
    }
    
   @FXML
    private void sendOnAction(ActionEvent event) throws DAOException {
       
        Email email = new Email();
        String address = emailStudent; 
        String subject = "Comentarios a avance";
        String text = this.txtAreaReasons.getText();
        String content= email.formatMessageToStudent("Evidencia rechazada", text);
        email.sendEmail(address, subject, content);
        
       
        closeStage();
    }
    
    public void closeStage(){
        Stage stage = (Stage)this.btnSend.getScene().getWindow();
        stage.close();
    }

    
}
