/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import mx.uv.fei.logic.Status;

/**
 * Esta clase se encarga de generar las ventanas emergentes. 
 * @author alexs
 */
public class DialogGenerator {

    public static final ButtonType BUTTON_YES = new ButtonType("Sí", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType BUTTON_NO = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

    public static final String SUCCESS = "100";
    public static final String WARNING = "200";
    public static final String ERROR = "300";
    public static final String FATAL = "400";

    public static Optional<ButtonType> getDialog(String message, Status status) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (null != status.getCode()) {
            switch (status.getCode()) {
                case SUCCESS:
                    break;
                case WARNING:
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Advertencia");
                    break;
                case ERROR:
                case FATAL:
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error");
                    break;
                default:
                    throw new IllegalArgumentException("El tipo de mensaje no es admitido en el objeto Alert");
            }
        }
        alert.setTitle("Cuadro de diálogo");
        alert.setContentText(message);
        return alert.showAndWait();
    }

    public static Optional<ButtonType> getConfirmationDialog(String message, String title) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                message,
                BUTTON_YES);
        alert.setTitle(title);
        alert.setHeaderText("Da clic en el botón para continuar");
        return alert.showAndWait();
    }
}
