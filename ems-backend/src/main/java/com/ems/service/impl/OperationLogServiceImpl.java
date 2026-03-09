package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.OperationLog;
import com.ems.mapper.OperationLogMapper;
import com.ems.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public Page<OperationLog> listPage(int page, int pageSize, String keyword, String module) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(OperationLog::getUsername, keyword)
                    .or().like(OperationLog::getAction, keyword));
        }
        if (StringUtils.hasText(module)) {
            wrapper.eq(OperationLog::getModule, module);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public void record(Long userId, String username, String module, String action, String target, String detail, String ip) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setModule(module);
        log.setAction(action);
        log.setTarget(target);
        log.setDetail(detail);
        log.setIp(ip);
        log.setCreateTime(LocalDateTime.now());
        save(log);
    }
}
