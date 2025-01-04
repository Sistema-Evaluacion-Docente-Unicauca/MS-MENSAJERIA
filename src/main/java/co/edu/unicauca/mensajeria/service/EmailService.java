package co.edu.unicauca.mensajeria.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${APLICACION_EXTENSION:application/pdf}")
    private String aplicacionExtension;

    @Value("${EXTENSION:.pdf}")
    private String extension;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public boolean enviarCorreoConAnexos(ArrayList<String> correos, String asunto, String mensaje, Map<String, String> documentos) {
        try {
            Map<String, Object> templateModel = new HashMap<>();

            for (String correo : correos) {
                long startTime = System.currentTimeMillis();
                logger.info("Iniciando creación del mensaje de correo...");
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                // Obtener saludo dinámico
                String saludo = obtenerSaludo();

                // Preparar plantilla Thymeleaf
                templateModel.put("mensaje_saludo", saludo);
                templateModel.put("cuerpoCorreoFormateado", mensaje);

                Context context = new Context();
                context.setVariables(templateModel);

                String html = templateEngine.process("emailTemplate", context);

                // Configurar correo
                helper.setTo(correo);
                helper.setSubject(asunto);
                helper.setText(html, true);

                long afterMessageCreation = System.currentTimeMillis();
                logger.info("Tiempo para crear el mensaje: {} ms", (afterMessageCreation - startTime));


                // Adjuntar documentos si existen
                if (documentos != null && !documentos.isEmpty()) {
                    for (Map.Entry<String, String> entry : documentos.entrySet()) {
                        String nombreDocumento = entry.getKey();
                        byte[] documentoBytes = Base64.getDecoder().decode(entry.getValue());
                        ByteArrayDataSource dataSource = new ByteArrayDataSource(documentoBytes, aplicacionExtension);
                        helper.addAttachment(nombreDocumento + extension, dataSource);
                    }
                }

                emailSender.send(message);
                long afterSend = System.currentTimeMillis();
                logger.info("Tiempo total para enviar el correo: {} ms", (afterSend - startTime));
            }

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String obtenerSaludo() {
        ZonedDateTime nowInColombia = ZonedDateTime.now(ZoneId.of("America/Bogota"));
        LocalTime currentTime = nowInColombia.toLocalTime();

        if (currentTime.isBefore(LocalTime.NOON)) {
            return "Buenos días";
        } else if (currentTime.isBefore(LocalTime.of(18, 0))) {
            return "Buenas tardes";
        } else {
            return "Buenas noches";
        }
    }
}
