package com.smartcommunity.realtime;


import com.smartcommunity.channel.ChannelService;
import com.smartcommunity.message.MessageService;
import com.smartcommunity.message.dto.MessageRequest;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Controller
public class ChannelChatController {
    private final MessageService messages;
    private final ChannelService channels;
    private final SimpMessagingTemplate messaging;

    public ChannelChatController(MessageService messages, ChannelService channels,
                                 SimpMessagingTemplate messaging) {
        this.messages = messages;
        this.channels = channels;
        this.messaging = messaging;
    }

    @MessageMapping("/channels/{channelId}/send")
    public void send(@DestinationVariable UUID channelId, @Valid MessageRequest request,
                     Principal principal) {
        requirePrincipal(principal);
        messages.send(channelId, principal.getName(), request);
    }

    @MessageMapping("/channels/{channelId}/typing")
    public void typing(@DestinationVariable UUID channelId, Map<String, Object> payload,
                       Principal principal) {
        requirePrincipal(principal);
        channels.get(channelId, principal.getName());
        messaging.convertAndSend("/topic/channels/" + channelId, Map.of(
                "type", "TYPING",
                "channelId", channelId,
                "email", principal.getName(),
                "isTyping", Boolean.TRUE.equals(payload.get("isTyping"))
        ));
    }

    private void requirePrincipal(Principal principal) {
        if (principal == null) throw new AccessDeniedException("Authentication required");
    }
}

