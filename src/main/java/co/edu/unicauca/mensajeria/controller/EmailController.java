package co.edu.unicauca.mensajeria.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import co.edu.unicauca.mensajeria.dto.EmailRequest;
import co.edu.unicauca.mensajeria.service.EmailService;

@RestController
@RequestMapping("/mensajeria")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private EmailService emailService;

    @PostMapping("enviarEmail")
    public ResponseEntity<Boolean> enviarCorreoConAnexos(@RequestBody EmailRequest request) {
        logger.info("Recibiendo solicitud para enviar correo a: {}", request.getCorreos());

        try {
            boolean exito = emailService.enviarCorreoConAnexos(
                request.getCorreos(),
                request.getAsunto(),
                request.getMensaje(),
                request.getDocumentos()
            );

            if (exito) {
                logger.info("Correo electrónico enviado correctamente a: {}", request.getCorreos());
                return ResponseEntity.ok(Boolean.TRUE);
            } else {
                logger.error("Error al enviar el correo a: {}", request.getCorreos());
                return ResponseEntity.internalServerError().body(Boolean.FALSE);
            }
        } catch (Exception e) {
            logger.error("Excepción al enviar el correo a: {}", request.getCorreos(), e);
            return ResponseEntity.internalServerError().body(Boolean.FALSE);
        }
    }
}
