package com.gestion_users.ms_gestion_users.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendTemporaryPasswordEmail(String toEmail, String temporaryPassword) {
        if (!emailEnabled) {
            logger.info("Email deshabilitado. Contraseña temporal para {}: {}", toEmail, temporaryPassword);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Recuperación de Contraseña - Laboratorios Clínicos");
            message.setText(buildEmailBody(temporaryPassword));
            
            mailSender.send(message);
            logger.info("Email de contraseña temporal enviado exitosamente a: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Error al enviar email a {}: {}", toEmail, e.getMessage());
            // No lanzamos excepción para no interrumpir el flujo
            // La contraseña ya fue guardada en BD y se muestra en pantalla
        }
    }

    private String buildEmailBody(String temporaryPassword) {
        return """
                Hola,
                
                Has solicitado recuperar tu contraseña en el sistema de Laboratorios Clínicos.
                
                Tu contraseña temporal es: %s
                
                Por razones de seguridad, deberás cambiar esta contraseña en tu primer inicio de sesión.
                
                Si no solicitaste este cambio, por favor contacta al administrador del sistema.
                
                Saludos,
                Equipo de Laboratorios Clínicos
                """.formatted(temporaryPassword);
    }
}
