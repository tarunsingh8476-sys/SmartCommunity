package com.smartcommunity.moderation;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/moderation")
public class ModerationController {
    private final ModerationClient client;

    public ModerationController(ModerationClient client) {
        this.client = client;
    }

    /**
     * Authenticated diagnostic endpoint for the frontend to preview moderation.
     * Message creation is independently moderated in MessageService.
     */
    @PostMapping("/check")
    public Map<String, Object> check(
            Authentication authentication,
            @Valid @RequestBody ModerationCheckRequest request) {
        ModerationDecision decision = client.check(request.text());
        return Map.of(
                "flagged", decision.flagged(),
                "reason", decision.reason() == null ? "" : decision.reason(),
                "allowed", !decision.flagged()
        );
    }

    public record ModerationCheckRequest(
            @NotBlank @Size(max = 4000) String text) {
    }
}

