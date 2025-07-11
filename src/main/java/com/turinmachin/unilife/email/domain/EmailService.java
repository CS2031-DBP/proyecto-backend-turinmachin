package com.turinmachin.unilife.email.domain;

import com.turinmachin.unilife.user.domain.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    @Value("${deployment.frontend.url}")
    private String frontendUrl;

    public void sendTemplatedEmail(final String to, final String subject, final String templateName,
            final Context context)
            throws MessagingException {
        final String emailContent = templateEngine.process(templateName, context);

        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(emailContent, true);

        mailSender.send(message);
    }

    public void sendVerificationEmail(final User user) throws MessagingException {
        final String verificationUrl = frontendUrl + "/verify?vid=" + user.getVerificationId();

        final Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("verification_url", verificationUrl);

        sendTemplatedEmail(user.getEmail(), "Tu verificación de UniLife", "verification", context);
    }

    public void sendWelcomeEmail(final User user) throws MessagingException {
        final Context context = new Context();
        context.setVariable("user_name", Optional.ofNullable(user.getDisplayName()).orElse(user.getUsername()));

        sendTemplatedEmail(user.getEmail(), "¡Bienvenido a UniLife!", "welcome", context);
    }

    public void sendResetPasswordEmail(final User user, final String token) throws MessagingException {
        final String url = frontendUrl + "/reset-password?token=" + token;

        final Context context = new Context();
        context.setVariable("user_name", Optional.ofNullable(user.getDisplayName()).orElse(user.getUsername()));
        context.setVariable("link", url);

        sendTemplatedEmail(user.getEmail(), "Restablece tu contraseña de UniLife", "reset_password", context);
    }

}
