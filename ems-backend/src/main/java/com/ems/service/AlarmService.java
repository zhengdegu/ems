package com.ems.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.Alarm;

public interface AlarmService extends IService<Alarm> {

    Page<Alarm> listPage(int page, int pageSize, String keyword, String level, String status);

    Alarm detail(Long id);

    void confirmAlarm(Long id);

    void handleAlarm(Long id, String handleNote);

    void deleteAlarm(Long id);
}
