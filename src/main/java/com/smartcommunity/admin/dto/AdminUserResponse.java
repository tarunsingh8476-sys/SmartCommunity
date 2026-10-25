package com.smartcommunity.admin.dto;

public record AdminUserResponse(
        java.util.UUID id,
        String name,
        String email,
        boolean emailVerified,
        String platformRole
) {
}
