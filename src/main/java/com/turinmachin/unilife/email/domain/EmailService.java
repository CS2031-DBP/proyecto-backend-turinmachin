package com.turinmachin.unilife.email.domain;

import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserToken;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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

    public void sendTemplatedEmail(String to, String subject, String templateName, Context context)
            throws MessagingException {
        String emailContent = templateEngine.process(templateName, context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(emailContent, true);

        mailSender.send(message);
    }

    public void sendVerificationEmail(User user) throws MessagingException {
        String verificationUrl = frontendUrl + "/verify?vid=" + user.getVerificationId();

        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("verification_url", verificationUrl);

        sendTemplatedEmail(user.getEmail(), "Tu verificación de UniLife", "verification", context);
    }

    public void sendWelcomeEmail(User user) throws MessagingException {
        Context context = new Context();
        context.setVariable("user_name", Optional.ofNullable(user.getDisplayName()).orElse(user.getUsername()));

        sendTemplatedEmail(user.getEmail(), "¡Bienvenido a UniLife!", "welcome", context);
    }

    public void sendResetPasswordEmail(User user, String token) throws MessagingException {
        String url = frontendUrl + "/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("user_name", Optional.ofNullable(user.getDisplayName()).orElse(user.getUsername()));
        context.setVariable("link", url);

        sendTemplatedEmail(user.getEmail(), "Restablece tu contraseña de UniLife", "reset_password", context);
    }

}
