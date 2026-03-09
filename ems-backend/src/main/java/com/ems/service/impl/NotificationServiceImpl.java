package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.Notification;
import com.ems.mapper.NotificationMapper;
import com.ems.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public Page<Notification> listPage(int page, int pageSize, Long userId, Integer isRead) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Notification::getUserId, userId);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }
        wrapper.orderByDesc(Notification::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public long unreadCount(Long userId) {
        return lambdaQuery().eq(Notification::getUserId, userId).eq(Notification::getIsRead, 0).count();
    }

    @Override
    public void markRead(Long id) {
        Notification notification = getById(id);
        if (notification == null) {
            throw new RuntimeException("通知不存在");
        }
        notification.setIsRead(1);
        updateById(notification);
    }

    @Override
    public void markAllRead(Long userId) {
        lambdaUpdate().eq(Notification::getUserId, userId).eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1).update();
    }

    @Override
    public void deleteNotification(Long id) {
        removeById(id);
    }

    @Override
    public void createNotification(Notification notification) {
        save(notification);
    }
}
