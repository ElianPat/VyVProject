/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import mx.uv.fei.dataaccess.DataBaseManager;
import mx.uv.fei.gui.controller.App;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 *
 * @author alexs
 */
public class GenerateReport {

    public JasperPrint getReport(String pathFile, Map parameters) throws JRException {
        JasperPrint jasperPrint = null;
        try {
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            InputStream inputStream = getClass().getResourceAsStream(pathFile);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new JRException("No fue posible obtener los datos para generar el reporte");
        }
        return jasperPrint;
    }

    public Image getPreviewReport(String pathFile, Map parameter) throws JRException, IOException {
        JasperPrint jasperPrint = getReport(pathFile, parameter);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        BufferedImage bufferedImage = convertPDFToImage(inputStream);
        Image image = convertBufferedImageToImage(bufferedImage);
        return image;
    }

    private BufferedImage convertPDFToImage(InputStream inputStream) throws IOException {
        BufferedImage bufferedImage;
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
        }
        return bufferedImage;
    }

    private Image convertBufferedImageToImage(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return new Image(inputStream);
    }
}
