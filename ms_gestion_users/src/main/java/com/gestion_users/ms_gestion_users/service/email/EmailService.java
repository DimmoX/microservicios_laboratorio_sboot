package com.gestion_users.ms_gestion_users.service.email;

public interface EmailService {
    /**
     * Envía un email con la contraseña temporal al usuario
     * @param toEmail Email del destinatario
     * @param temporaryPassword Contraseña temporal generada
     */
    void sendTemporaryPasswordEmail(String toEmail, String temporaryPassword);
}
