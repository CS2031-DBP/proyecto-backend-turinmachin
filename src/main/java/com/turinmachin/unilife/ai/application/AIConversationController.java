package com.turinmachin.unilife.ai.application;

import com.turinmachin.unilife.ai.domain.AIConversationService;
import com.turinmachin.unilife.ai.domain.AIMessage;
import com.turinmachin.unilife.ai.dto.AIMessageResponseDto;
import com.turinmachin.unilife.ai.dto.UserPromptRequestDto;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIConversationController {

    private final AIConversationService conversationService;

    private final UserService userService;

    private final ModelMapper modelMapper;

    @GetMapping("/conversation")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<AIMessageResponseDto> getConversation(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        return conversationService.getConversation(user);
    }

    @PostMapping("/message")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public AIMessageResponseDto sendMessage(
            @Valid @RequestBody UserPromptRequestDto request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        AIMessage aiMessage = conversationService.sendMessage(user, request.getContent());
        return modelMapper.map(aiMessage, AIMessageResponseDto.class);
    }

    @DeleteMapping("/conversation")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetConversation(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.checkUserVerified(user);

        conversationService.resetConversation(user);
    }
}
