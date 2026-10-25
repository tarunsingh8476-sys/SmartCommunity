package com.smartcommunity.admin;

import com.smartcommunity.admin.dto.AdminDashboardResponse;
import com.smartcommunity.admin.dto.AdminUserResponse;
import com.smartcommunity.message.MessageRepository;
import com.smartcommunity.notification.NotificationRepository;
import com.smartcommunity.server.ServerRepository;
import com.smartcommunity.user.User;
import com.smartcommunity.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminService {

    private final UserRepository users;
    private final ServerRepository servers;
    private final MessageRepository messages;
    private final NotificationRepository notifications;

    public AdminService(
            UserRepository users,
            ServerRepository servers,
            MessageRepository messages,
            NotificationRepository notifications) {
        this.users = users;
        this.servers = servers;
        this.messages = messages;
        this.notifications = notifications;
    }

    public AdminDashboardResponse dashboard() {
        long totalUsers = users.count();
        long verifiedUsers = users.countByEmailVerifiedTrue();
        long totalCommunities = servers.count();
        long totalMessages = messages.count();
        long totalNotifications = notifications.count();

        return new AdminDashboardResponse(
                totalUsers,
                verifiedUsers,
                totalUsers - verifiedUsers,
                totalCommunities,
                totalMessages,
                totalNotifications
        );
    }

    public Page<AdminUserResponse> users(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));

        Pageable pageable = PageRequest.of(safePage, safeSize);

        return users.findAll(pageable)
                .map(this::toResponse);
    }

    public AdminUserResponse user(UUID id) {
        User user = users.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found: " + id));

        return toResponse(user);
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getPlatformRole().name()
        );
    }
}
