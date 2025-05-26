package com.turinmachin.unilife.user.event;

import com.turinmachin.unilife.email.domain.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SendWelcomeEmailEventListener {

    @Autowired
    private EmailService emailService;

    @Async
    @EventListener
    public void handleSendWelcomeEmailEvent(SendWelcomeEmailEvent event) throws MessagingException {
        emailService.sendWelcomeEmail(event.getUser());
    }

}
