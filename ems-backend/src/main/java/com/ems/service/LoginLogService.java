package com.ems.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.LoginLog;

public interface LoginLogService extends IService<LoginLog> {

    Page<LoginLog> listPage(int page, int pageSize, String keyword);

    void recordLogin(Long userId, String username, String ip, String userAgent, int status, String message);
}
