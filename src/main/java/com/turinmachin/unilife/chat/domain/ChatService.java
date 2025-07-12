package com.turinmachin.unilife.chat.domain;

import java.util.List;
import java.util.Optional;

import org.jose4j.lang.JoseException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turinmachin.unilife.chat.dto.ChatMessageDto;
import com.turinmachin.unilife.chat.dto.ChatNotificationPayloadDto;
import com.turinmachin.unilife.chat.dto.ChatSubscriptionDto;
import com.turinmachin.unilife.chat.infrastructure.ChatSubscriptionRepository;
import com.turinmachin.unilife.common.exception.ConflictException;
import com.turinmachin.unilife.fileinfo.domain.FileInfo;
import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushAsyncService;
import nl.martijndwars.webpush.PushService;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final PushAsyncService pushService;
    private final UserService userService;
    private final ChatSubscriptionRepository chatSubscriptionRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public ChatSubscription addSubscription(final User user, final ChatSubscriptionDto dto) {
        if (chatSubscriptionRepository.existsByEndpoint(dto.getEndpoint()))
            throw new ConflictException();

        final ChatSubscription subscription = modelMapper.map(dto, ChatSubscription.class);

        final User attachedUser = userService.getUserById(user.getId()).orElseThrow();
        subscription.setUser(attachedUser);
        return chatSubscriptionRepository.save(subscription);
    }

    public void notifyMessage(final ChatMessageDto dto) throws JsonProcessingException {
        final List<ChatSubscription> subscriptions = chatSubscriptionRepository.findByUserId(dto.getToId());
        final User author = userService.getUserById(dto.getFromId()).orElseThrow(UserNotFoundException::new);

        final ChatNotificationPayloadDto payload = new ChatNotificationPayloadDto();
        payload.setTitle(Optional.ofNullable(author.getDisplayName()).orElse(author.getUsername()));
        payload.setBody(dto.getContent()); // TODO: crop content
        payload.setIcon(Optional.ofNullable(author.getProfilePicture()).map(FileInfo::getUrl).orElse(null));

        final byte[] payloadBytes = objectMapper.writeValueAsBytes(payload);

        for (final ChatSubscription sub : subscriptions) {
            try {
                final Notification notification = new Notification(
                        sub.getEndpoint(),
                        sub.getUserPublicKey(),
                        sub.getAuthAsBytes(),
                        payloadBytes);

                pushService.send(notification);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
