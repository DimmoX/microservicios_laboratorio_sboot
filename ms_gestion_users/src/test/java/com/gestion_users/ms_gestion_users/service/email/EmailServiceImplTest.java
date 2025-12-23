package com.gestion_users.ms_gestion_users.service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private String fromEmail = "noreply@laboratorios.com";
    private String toEmail = "test@example.com";
    private String temporaryPassword = "Temp123!";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", fromEmail);
    }

    @Test
    void testSendTemporaryPasswordEmail_EmailEnabled_Success() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        emailService.sendTemporaryPasswordEmail(toEmail, temporaryPassword);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        
        assertNotNull(sentMessage);
        assertEquals(fromEmail, sentMessage.getFrom());
        assertArrayEquals(new String[]{toEmail}, sentMessage.getTo());
        assertEquals("Recuperación de Contraseña - Laboratorios Clínicos", sentMessage.getSubject());
        assertNotNull(sentMessage.getText());
        assertTrue(sentMessage.getText().contains(temporaryPassword));
        assertTrue(sentMessage.getText().contains("Tu contraseña temporal es: " + temporaryPassword));
    }

    @Test
    void testSendTemporaryPasswordEmail_EmailDisabled_NoEmailSent() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailEnabled", false);

        // Act
        emailService.sendTemporaryPasswordEmail(toEmail, temporaryPassword);

        // Assert
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendTemporaryPasswordEmail_EmailEnabled_ExceptionThrown_NoExceptionPropagated() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert - No debería lanzar excepción
        assertDoesNotThrow(() -> {
            emailService.sendTemporaryPasswordEmail(toEmail, temporaryPassword);
        });

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendTemporaryPasswordEmail_EmailBodyContainsAllExpectedText() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        emailService.sendTemporaryPasswordEmail(toEmail, temporaryPassword);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        String emailBody = messageCaptor.getValue().getText();
        
        assertTrue(emailBody.contains("Hola,"));
        assertTrue(emailBody.contains("Has solicitado recuperar tu contraseña"));
        assertTrue(emailBody.contains("Tu contraseña temporal es: " + temporaryPassword));
        assertTrue(emailBody.contains("deberás cambiar esta contraseña"));
        assertTrue(emailBody.contains("Saludos,"));
        assertTrue(emailBody.contains("Equipo de Laboratorios Clínicos"));
    }

    @Test
    void testSendTemporaryPasswordEmail_DifferentPasswords() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
        String password1 = "Pass123!";
        String password2 = "SecureP@ss456";
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        emailService.sendTemporaryPasswordEmail(toEmail, password1);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        String emailBody1 = messageCaptor.getValue().getText();
        assertTrue(emailBody1.contains(password1));
        assertFalse(emailBody1.contains(password2));
    }

    @Test
    void testSendTemporaryPasswordEmail_MultipleRecipients() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        emailService.sendTemporaryPasswordEmail(email1, temporaryPassword);
        emailService.sendTemporaryPasswordEmail(email2, temporaryPassword);

        // Assert
        verify(mailSender, times(2)).send(messageCaptor.capture());
        var messages = messageCaptor.getAllValues();
        assertArrayEquals(new String[]{email1}, messages.get(0).getTo());
        assertArrayEquals(new String[]{email2}, messages.get(1).getTo());
    }

    @Test
    void testSendTemporaryPasswordEmail_WithSpecialCharactersInPassword() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
        String specialPassword = "P@ss!#$%&*()_+-=[]{}|;:',.<>?/`~";
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Act
        emailService.sendTemporaryPasswordEmail(toEmail, specialPassword);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        String emailBody = messageCaptor.getValue().getText();
        assertTrue(emailBody.contains(specialPassword));
    }
}
