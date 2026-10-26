package com.smartcommunity.moderation;

public class ModerationRejectedException extends RuntimeException {
    public ModerationRejectedException(String message) {
        super(message);
    }
}
