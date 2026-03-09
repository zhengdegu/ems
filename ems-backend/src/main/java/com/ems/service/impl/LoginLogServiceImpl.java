package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.LoginLog;
import com.ems.mapper.LoginLogMapper;
import com.ems.service.LoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public Page<LoginLog> listPage(int page, int pageSize, String keyword) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(LoginLog::getUsername, keyword);
        }
        wrapper.orderByDesc(LoginLog::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public void recordLogin(Long userId, String username, String ip, String userAgent, int status, String message) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setStatus(status);
        log.setMessage(message);
        log.setCreateTime(LocalDateTime.now());
        save(log);
    }
}
