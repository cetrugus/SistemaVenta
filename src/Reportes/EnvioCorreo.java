package Reportes;

import Modelo.Conexion;
import Modelo.Empresa;
import Modelo.EmpresaDAO;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;

public class EnvioCorreo {

    public static boolean enviarFactura(String correoDestino,
            String nombreCliente,
            String rutaPdf) {
        try {
            EmpresaDAO empresaDAO = new EmpresaDAO();
            Empresa empresa = empresaDAO.ObtenerEmpresa();

            // Validar que tenga configuración SMTP
            if (empresa == null || empresa.getSmtpPass() == null
                    || empresa.getSmtpPass().isEmpty()) {
                System.out.println("SMTP no configurado");
                return false;
            }

            // ← Datos desde BD, no hardcodeados
            final String smtpHost = empresa.getSmtpHost();
            final String smtpPuerto = String.valueOf(empresa.getSmtpPort());
            final String smtpUsuario = empresa.getSmtpUsuario();
            final String smtpPass = empresa.getSmtpPass();
            final String nombreEmpresa = empresa.getNombre();

            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPuerto);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", smtpHost);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsuario, smtpPass);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpUsuario, nombreEmpresa));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(correoDestino));
            message.setSubject("Factura de compra - " + nombreEmpresa);

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(
                    "<html><body>"
                    + "<h2>Estimado/a " + nombreCliente + "</h2>"
                    + "<p>Adjunto encontrará su factura de compra.</p>"
                    + "<p>Gracias por preferirnos.</p><br>"
                    + "<p><b>" + nombreEmpresa + "</b></p>"
                    + "<p>" + empresa.getTelefono() + "</p>"
                    + "<p>" + empresa.getDireccion() + "</p>"
                    + "</body></html>",
                    "text/html; charset=utf-8"
            );

            MimeBodyPart attachPart = new MimeBodyPart();
            attachPart.attachFile(new File(rutaPdf));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachPart);
            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Correo enviado a: " + correoDestino);
            return true;

        } catch (Exception e) {
            System.out.println("Error enviando correo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
