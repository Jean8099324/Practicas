package com.ufide.practicasemanal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String remitente;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String remitente) {

        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    /**
     * Envía el enlace para restablecer la contraseña.
     *
     * @param destinatario correo del usuario
     * @param enlace       enlace con el token de recuperación
     */
    public void enviarLinkRecuperacion(
            String destinatario,
            String enlace) {

        validarDatos(destinatario, enlace);

        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setFrom(remitente);

        mensaje.setTo(destinatario);

        mensaje.setSubject(
                "SC-403 - Recuperar contraseña");

        mensaje.setText(
                "Recibimos una solicitud para restablecer "
                        + "tu contraseña.\n\n"
                        + "Haz clic en el siguiente enlace "
                        + "(válido por 30 minutos):\n\n"
                        + enlace
                        + "\n\n"
                        + "Si no solicitaste este cambio, "
                        + "ignora este correo. "
                        + "Tu contraseña actual seguirá "
                        + "funcionando normalmente.\n\n"
                        + "SC-403 - Sistema de Cursos");

        try {

            mailSender.send(mensaje);

        } catch (MailException exception) {

            throw new IllegalStateException(
                    "No fue posible enviar el correo de recuperación.",
                    exception);
        }
    }

    private void validarDatos(
            String destinatario,
            String enlace) {

        if (destinatario == null
                || destinatario.isBlank()) {

            throw new IllegalArgumentException(
                    "El correo destinatario es obligatorio.");
        }

        if (enlace == null
                || enlace.isBlank()) {

            throw new IllegalArgumentException(
                    "El enlace de recuperación es obligatorio.");
        }
    }
}