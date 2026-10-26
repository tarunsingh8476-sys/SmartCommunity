package com.smartcommunity.moderation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModerationRequest(
        @NotBlank @Size(max = 4000) String text
) {
}

