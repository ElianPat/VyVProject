/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.sql.Blob;

/**
 *
 * @author aresj
 */
import javafx.scene.image.Image;

/**
 *
 * @author aresj
 */
public class Document {
    private Image image;
    private String fileName;
    private Blob blob;

    public Document(Image image, String fileName) {
        this.image = image;
        this.fileName = fileName;
    }

    public Image getImage() {
        return image;
    }
    
    public void setImage(Image image){
        this.image = image;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public Blob getBlob() {
        return blob;
    }

    public void setBlob(Blob blob) {
        this.blob = blob;
    }
}
