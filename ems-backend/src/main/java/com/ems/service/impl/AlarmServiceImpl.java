package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.Alarm;
import com.ems.mapper.AlarmMapper;
import com.ems.service.AlarmService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class AlarmServiceImpl extends ServiceImpl<AlarmMapper, Alarm> implements AlarmService {

    @Override
    public Page<Alarm> listPage(int page, int pageSize, String keyword, String level, String status) {
        LambdaQueryWrapper<Alarm> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Alarm::getMessage, keyword);
        }
        if (StringUtils.hasText(level)) {
            wrapper.eq(Alarm::getLevel, level);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Alarm::getStatus, status);
        }
        wrapper.orderByDesc(Alarm::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public Alarm detail(Long id) {
        return getById(id);
    }

    @Override
    public void confirmAlarm(Long id) {
        Alarm alarm = getById(id);
        if (alarm == null) {
            throw new RuntimeException("告警不存在");
        }
        alarm.setStatus("confirmed");
        alarm.setConfirmTime(LocalDateTime.now());
        updateById(alarm);
    }

    @Override
    public void handleAlarm(Long id, String handleNote) {
        Alarm alarm = getById(id);
        if (alarm == null) {
            throw new RuntimeException("告警不存在");
        }
        alarm.setStatus("handled");
        alarm.setHandleNote(handleNote);
        alarm.setHandleTime(LocalDateTime.now());
        updateById(alarm);
    }

    @Override
    public void deleteAlarm(Long id) {
        removeById(id);
    }
}
