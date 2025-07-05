package com.turinmachin.unilife.authentication.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.turinmachin.unilife.email.domain.EmailService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResetPasswordIssuedEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleResetPasswordIssuedEvent(ResetPasswordIssuedEvent ev) throws MessagingException {
        emailService.sendResetPasswordEmail(ev.getUser(), ev.getToken());
    }

}
