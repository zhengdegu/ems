package com.ems.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.Notification;

public interface NotificationService extends IService<Notification> {

    Page<Notification> listPage(int page, int pageSize, Long userId, Integer isRead);

    long unreadCount(Long userId);

    void markRead(Long id);

    void markAllRead(Long userId);

    void deleteNotification(Long id);

    void createNotification(Notification notification);
}
