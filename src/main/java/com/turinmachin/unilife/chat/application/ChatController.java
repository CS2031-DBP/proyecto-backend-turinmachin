package com.turinmachin.unilife.chat.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.turinmachin.unilife.chat.domain.ChatService;
import com.turinmachin.unilife.chat.dto.ChatMessageDto;
import com.turinmachin.unilife.chat.dto.ChatSubscriptionDto;
import com.turinmachin.unilife.chat.dto.WebhookRequestDto;
import com.turinmachin.unilife.common.exception.UnauthorizedException;
import com.turinmachin.unilife.user.domain.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    @Value("${chat.webhook-secret}")
    private String webhookSecret;

    private final ChatService chatService;

    @PostMapping("/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@Valid @RequestBody final ChatSubscriptionDto dto, final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        chatService.addSubscription(user, dto);
    }

    @PostMapping("/notify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendNotification(@RequestHeader(name = "X-Webhook-Secret", required = false) final String secret,
            @Valid @RequestBody final WebhookRequestDto dto) throws JsonProcessingException {
        if (!webhookSecret.equals(secret))
            throw new UnauthorizedException();

        chatService.notifyMessage(dto.getRecord());
    }

}
