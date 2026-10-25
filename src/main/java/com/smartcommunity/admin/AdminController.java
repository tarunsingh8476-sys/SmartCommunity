package com.smartcommunity.admin;

import com.smartcommunity.admin.dto.AdminDashboardResponse;
import com.smartcommunity.admin.dto.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Admin dashboard statistics
    @GetMapping("/dashboard")
    public AdminDashboardResponse dashboard() {
        return adminService.dashboard();
    }

    // Paginated list of users
    @GetMapping("/users")
    public Page<AdminUserResponse> users(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return adminService.users(page, size);
    }

    // Details of a specific user
    @GetMapping("/users/{id}")
    public AdminUserResponse user(@PathVariable UUID id) {
        return adminService.user(id);
    }
}
