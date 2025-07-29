package com.turinmachin.unilife.user.event;

import com.turinmachin.unilife.email.domain.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendVerificationEmailListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleSendVerificationEmailEvent(final SendVerificationEmailEvent event) throws MessagingException {
        emailService.sendVerificationEmail(event.getUser());
    }

}
