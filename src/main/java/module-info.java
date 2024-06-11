module com.mycompany.sgpgyer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires java.mail;
    requires java.base;
    requires com.google.gson;
    requires jasperreports;
    requires jasperreports.fonts;
    requires com.google.protobuf;
    requires mysql.connector.j;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires itextpdf;
    requires javafx.graphics;
    requires itext;
    requires jbcrypt;

    opens mx.uv.fei.gui.controller to javafx.fxml;  
    opens mx.uv.fei.logic to javafx.base;
    exports mx.uv.fei.gui.controller;
}
