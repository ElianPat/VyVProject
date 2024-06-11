/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Esta clase inicializa el login, la primer clase.
 * @author alexs
 */
public class App extends Application {

    private static final String KEY = "mail.smtp.starttls.enable";
    private static final String VALUE = "true";
    private static final Logger LOGGER = LogManager.getLogger(App.class);
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        App.primaryStage = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.setProperty(KEY, VALUE);
        launch(args);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void openNewWindow(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlFile));
        Parent root = loader.load();

        Scene newScene = new Scene(root);
        primaryStage.setScene(newScene);

        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.show();

        newScene.getWindow().setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void configureStage(Stage stage, double width, double height) {
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
    }

    public static void newView(Parent root) {
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
