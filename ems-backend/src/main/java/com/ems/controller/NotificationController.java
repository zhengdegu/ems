package com.ems.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ems.dto.R;
import com.ems.entity.Notification;
import com.ems.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public R list(@RequestParam(defaultValue = "1") int page,
                  @RequestParam(defaultValue = "10") int pageSize,
                  @RequestParam(required = false) Long userId,
                  @RequestParam(required = false) Integer isRead) {
        Page<Notification> result = notificationService.listPage(page, pageSize, userId, isRead);
        return R.ok(result);
    }

    @GetMapping("/unread-count")
    public R unreadCount(@RequestParam Long userId) {
        long count = notificationService.unreadCount(userId);
        return R.ok(count);
    }

    @PutMapping("/{id}/read")
    public R markRead(@PathVariable Long id) {
        try {
            notificationService.markRead(id);
            return R.ok();
        } catch (RuntimeException e) {
            return R.fail(e.getMessage());
        }
    }

    @PutMapping("/read-all")
    public R markAllRead(@RequestParam Long userId) {
        notificationService.markAllRead(userId);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return R.ok();
    }
}
