package com.smartcommunity.message;
import com.smartcommunity.channel.*;import com.smartcommunity.message.dto.MessageRequest;import com.smartcommunity.user.*;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;import java.util.*;

@Service
public class MessageService {

    private final MessageRepository repo;
    private final ChannelService channels;
    private final UserService users;
    private final SimpMessagingTemplate messaging;

    public MessageService(
            MessageRepository r,
            ChannelService c,
            UserService u,
            SimpMessagingTemplate messaging) {
        this.repo = r;
        this.channels = c;
        this.users = u;
        this.messaging = messaging;
    }

    public Map<String, Object> send(
            UUID channelId, String email, MessageRequest r) {

        Channel c = channels.getChannelById(channelId);

        Message m = new Message();
        m.setChannel(c);
        m.setSender(users.byEmail(email));
        m.setContent(r.content().trim());

        Map<String, Object> dto = view(repo.save(m));
        publish(channelId, "MESSAGE_CREATED", dto);

        return dto;
    }

    public Map<String, Object> list(
            UUID id, String email, int page, int size) {

        Page<Message> p = repo.findByChannelIdOrderByCreatedAtDesc(
                id,
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(Math.max(size, 1), 100)
                )
        );

        return Map.of(
                "items", p.getContent().stream().map(this::view).toList(),
                "page", p.getNumber(),
                "size", p.getSize(),
                "totalElements", p.getTotalElements(),
                "totalPages", p.getTotalPages()
        );
    }

    public Map<String, Object> edit(
            UUID id, String email, MessageRequest r) {

        Message m = repo.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Message not found"));

        if (!m.getSender().getEmail().equalsIgnoreCase(email)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Own messages only");
        }

        m.setContent(r.content().trim());

        Map<String, Object> dto = view(repo.save(m));
        publish(m.getChannel().getId(), "MESSAGE_UPDATED", dto);

        return dto;
    }

    public void delete(UUID id, String email) {

        Message m = repo.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Message not found"));

        if (!m.getSender().getEmail().equalsIgnoreCase(email)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Own messages only");
        }

        UUID channelId = m.getChannel().getId();
        Map<String, Object> dto = view(m);

        repo.delete(m);
        publish(channelId, "MESSAGE_DELETED", dto);
    }

    private void publish(
            UUID channelId, String type, Map<String, Object> message) {

        messaging.convertAndSend(
                "/topic/channels/" + channelId,
                Map.of(
                        "type", type,
                        "channelId", channelId,
                        "message", message
                )
        );
    }

    public Map<String, Object> view(Message m) {
        return Map.of(
                "id", m.getId(),
                "content", m.getContent(),
                "senderId", m.getSender().getId(),
                "senderName", m.getSender().getName(),
                "channelId", m.getChannel().getId(),
                "createdAt", m.getCreatedAt(),
                "updatedAt", m.getUpdatedAt()
        );
    }
}
