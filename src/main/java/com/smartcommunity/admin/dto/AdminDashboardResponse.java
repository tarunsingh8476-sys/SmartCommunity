package com.smartcommunity.admin.dto;

public record AdminDashboardResponse(
        long totalUsers,
        long verifiedUsers,
        long unverifiedUsers,
        long totalCommunities,
        long totalMessages,
        long totalNotifications
) {
}
