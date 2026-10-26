package com.smartcommunity.moderation;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ModerationService {
    private final ModerationClient client;

    public ModerationService(ModerationClient client) {
        this.client = client;
    }

    /** Fail closed: messages are not saved/broadcast if the moderation API is unavailable. */
    public void requireAllowed(String content) {
        ModerationDecision decision = client.check(content);
        if (decision.flagged()) {
            String reason = StringUtils.hasText(decision.reason())
                    ? decision.reason()
                    : "Your message was flagged by the moderation service.";
            throw new ModerationRejectedException(reason);
        }
    }
}

