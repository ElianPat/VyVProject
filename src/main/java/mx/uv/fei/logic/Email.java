/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import mx.uv.fei.gui.controller.App;
import mx.uv.fei.gui.controller.DialogGenerator;

/**
 *
 * @author Palom
 */
public class Email {

    public void sendEmail(String address, String subject, String text) {
        String sender = "SGPGyER@gmail.com";
        String emailKey = "ifjujzdqnpdfjjyi";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.user", sender);
        properties.put("mail.smtp.clave", emailKey);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(address, true));
            message.setSubject(subject);
            message.setContent(text, "text/html; charset=utf-8");
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", sender, emailKey);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog("Error de conexión, no se pudo enviar el correo", Status.WARNING);
        }
    }

    public String formatMessageToDirector(String title, String text) {

        String content = "<html>\n"
                + "<head>\n"
                + "    <title>Sistema</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "    <div style=\"text-align: center;\">\n"
                + "        <img src=\"https://i.ibb.co/cXs5Qjs/sgpgyer-blue.jpg\" alt=\"sgpgyer-blue\" border=\"0\">\n"
                + "    </div>\n"
                + "    <h1><center>" + title + "</h1>\n"
                + "    <p><center>Estimado usuario, su propuesta de proyecto recibio los siguientes comentarios: </p>\n"
                + "    <p><center>" + text + "</p>\n"
                + "    <div style=\"text-align: center;\">\n"
                + "        <img src=\"https://i.ibb.co/JBWh5zh/sgpgyer-barra.jpg\" alt=\"sgpgyer-barra\" border=\"0\">\n"
                + "    </div>\n"
                + "    <p><center>Correo de caracter informativo, no admite respuestas</p>\n"
                + "</body>\n"
                + "</html>";
        return content;
    }

    public String formatMessageToUser(String user, String password) {

        String content = "<html>\n"
                + "<head>\n"
                + "    <title>Sistema</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "    <div style=\"text-align: center;\">\n"
                + "        <img src=\"https://i.ibb.co/cXs5Qjs/sgpgyer-blue.jpg\" alt=\"sgpgyer-blue\" border=\"0\">\n"
                + "    </div>\n"
                + "    <h1><center>Le damos la bienvenida a SGPGyER</h1>\n"
                + "    <p><center>Estimado usuario, le compartimos su usuario y contraseña para ingresar al sistema </p>\n"
                + "    <p><center>Usuario: " + user + "</p>\n"
                + "    <p><center>Contraseña: " + password + "</p>\n"
                + "    <p><center>Sus datos son confidenciales, no los comparta.  </p>\n"
                + "    <div style=\"text-align: center;\">\n"
                + "        <img src=\"https://i.ibb.co/JBWh5zh/sgpgyer-barra.jpg\" alt=\"sgpgyer-barra\" border=\"0\">\n"
                + "    </div>\n"
                + "    <p><center>Correo de caracter informativo, no admite respuestas</p>\n"
                + "</body>\n"
                + "</html>";
        return content;
    }

    public String formatMessageToStudent(String title, String text) {

        String content = "<html>\n"
                + "<head>\n"
                + "    <title>Sistema</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "    <div style=\"text-align: center;\">\n"
                + "        <img src=\"https://i.ibb.co/cXs5Qjs/sgpgyer-blue.jpg\" alt=\"sgpgyer-blue\" border=\"0\">\n"
                + "    </div>\n"
                + "    <h1><center>" + title + "</h1>\n"
                + "    <p><center>Estimado usuario, su avance recientemente entregado recibio los siguientes comentarios: </p>\n"
                + "    <p><center>" + text + "</p>\n"
                + "    <div style=\"text-align: center;\">\n"
                + "        <img src=\"https://i.ibb.co/JBWh5zh/sgpgyer-barra.jpg\" alt=\"sgpgyer-barra\" border=\"0\">\n"
                + "    </div>\n"
                + "    <p><center>Correo de caracter informativo, no admite respuestas</p>\n"
                + "</body>\n"
                + "</html>";
        return content;
    }
}
