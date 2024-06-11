/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.gui.controller;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import mx.uv.fei.logic.Document;

public class DocumentCell extends ListCell<Document> {

    /**
     * Se encarga de actualizar las filas de una tabla.
     * 
     * @param document
     * @param empty 
     */
    
    @Override
    protected void updateItem(Document document, boolean empty) {
        super.updateItem(document, empty);

        if (empty || document == null) {
            setText(null);
            setGraphic(null);
        } else {
            ImageView imageView = new ImageView(document.getImage());
            imageView.setFitWidth(50); 
            imageView.setFitHeight(50);
            setText(document.getFileName());
            setGraphic(imageView);
        }
    }
}
