package com.smartcommunity.moderation;

import com.fasterxml.jackson.databind.JsonNode;

public record ModerationResponse(JsonNode raw) {
    public boolean isFlagged() {
        return findBoolean(raw, "flagged", "is_flagged", "toxic", "is_toxic",
                "toxicity", "spam", "is_spam", "contains_toxicity", "contains_spam");
    }

    public String reason() {
        for (String key : new String[]{"reason", "message", "detail", "label", "prediction"}) {
            JsonNode node = raw == null ? null : raw.get(key);
            if (node != null && node.isValueNode() && !node.isNull()) {
                String value = node.asText();
                if (!value.isBlank()) return value;
            }
        }
        return isFlagged() ? "Message flagged by the moderation service." : "";
    }

    public boolean hasRecognizedDecision() {
        return containsAny(raw, "flagged", "is_flagged", "toxic", "is_toxic",
                "toxicity", "spam", "is_spam", "contains_toxicity", "contains_spam");
    }

    private static boolean findBoolean(JsonNode node, String... keys) {
        if (node == null || node.isNull()) return false;
        for (String key : keys) {
            JsonNode value = node.get(key);
            if (value != null && !value.isNull()) {
                if (value.isBoolean() && value.asBoolean()) return true;
                if (value.isNumber() && value.asDouble() >= 0.5) return true;
                if (value.isTextual()) {
                    String s = value.asText().trim().toLowerCase();
                    if (s.equals("true") || s.equals("toxic") || s.equals("spam") || s.equals("flagged")) return true;
                }
            }
        }
        return false;
    }

    private static boolean containsAny(JsonNode node, String... keys) {
        if (node == null || node.isNull()) return false;
        for (String key : keys) if (node.has(key)) return true;
        return false;
    }
}

