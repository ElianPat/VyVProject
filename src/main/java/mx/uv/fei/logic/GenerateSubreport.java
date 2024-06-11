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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import mx.uv.fei.dataaccess.DataBaseManager;
import mx.uv.fei.gui.controller.App;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 *
 * @author alexs
 */
public class GenerateSubreport {

    private String pathMainReport;
    private List<String> subreports;
    private List<String> subreportParameters;
    private Map<String, Object> parameters;

    /**
     *
     * @param pathMainReport
     */
    public GenerateSubreport(String pathMainReport) {
        this.pathMainReport = pathMainReport;
        this.subreports = new ArrayList<>();
        this.subreportParameters = new ArrayList<>();
        this.parameters = new HashMap<>();
    }

    /**
     *
     * @param subreportParameter
     * @param subreport
     * @return
     */
    public GenerateSubreport addSubreport(String subreportParameter, String subreport) {
        subreportParameters.add(subreportParameter);
        subreports.add(subreport);
        return this;
    }

    /**
     *
     * @param parameters
     * @return
     */
    public GenerateSubreport addParameters(Map<String, Object> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    /**
     *
     * @return
     * @throws JRException
     */
    public JasperPrint printSubReports() throws JRException {
        JasperPrint jasperPrint = null;
        try {
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();

            JasperReport jasperMainReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(pathMainReport));

            for (int i = 0; i < subreports.size(); i++) {
                String subreport = subreports.get(i);
                String subreportParameter = subreportParameters.get(i);

                JasperDesign jasperDesignSubReport = JRXmlLoader.load(getClass().getResourceAsStream(subreport));
                JasperReport compiledSubReport = JasperCompileManager.compileReport(jasperDesignSubReport);

                parameters.put(subreportParameter, compiledSubReport);
            }
            jasperPrint = JasperFillManager.fillReport(jasperMainReport, parameters, connection);
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new JRException("No fue posible obtener los datos para generar el reporte");
        }
        return jasperPrint;
    }

    /**
     *
     * @return
     * @throws JRException
     * @throws IOException
     */
    public Image getPreviewSubReports() throws JRException, IOException {
        JasperPrint jasperPrint = printSubReports();
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
