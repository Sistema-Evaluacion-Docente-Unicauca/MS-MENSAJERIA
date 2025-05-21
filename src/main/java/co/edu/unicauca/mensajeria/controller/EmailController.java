package co.edu.unicauca.mensajeria.controller;

import co.edu.unicauca.mensajeria.dto.EmailRequest;
import co.edu.unicauca.mensajeria.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/mensajeria")
@RequiredArgsConstructor
@Tag(name = "Mensajería", description = "Servicio para envío de correos electrónicos con documentos adjuntos")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;

    @PostMapping("enviar-email")
    @Operation(
        summary = "Enviar correo electrónico con anexos",
        description = "Permite enviar un correo electrónico a múltiples destinatarios, incluyendo documentos adjuntos.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos del correo a enviar",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EmailRequest.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Correo enviado exitosamente",
                content = @Content(schema = @Schema(implementation = Boolean.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error al enviar el correo",
                content = @Content(schema = @Schema(implementation = Boolean.class))
            )
        }
    )
    public ResponseEntity<Boolean> enviarCorreoConAnexos(
            @org.springframework.web.bind.annotation.RequestBody EmailRequest request) {

        logger.info("Recibiendo solicitud para enviar correo a: {}", request.getCorreos());

        try {
            boolean exito = emailService.enviarCorreoConAnexos(
                    request.getCorreos(),
                    request.getAsunto(),
                    request.getMensaje(),
                    request.getDocumentos());

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
