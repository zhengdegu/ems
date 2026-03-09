package com.ems.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.AlarmRule;

public interface AlarmRuleService extends IService<AlarmRule> {

    Page<AlarmRule> listPage(int page, int pageSize, String keyword);

    void createRule(AlarmRule rule);

    void updateRule(Long id, AlarmRule rule);

    void deleteRule(Long id);

    void toggleEnabled(Long id);
}
