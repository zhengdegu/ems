package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.AlarmRule;
import com.ems.mapper.AlarmRuleMapper;
import com.ems.service.AlarmRuleService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AlarmRuleServiceImpl extends ServiceImpl<AlarmRuleMapper, AlarmRule> implements AlarmRuleService {

    @Override
    public Page<AlarmRule> listPage(int page, int pageSize, String keyword) {
        LambdaQueryWrapper<AlarmRule> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(AlarmRule::getName, keyword);
        }
        wrapper.orderByDesc(AlarmRule::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public void createRule(AlarmRule rule) {
        save(rule);
    }

    @Override
    public void updateRule(Long id, AlarmRule rule) {
        rule.setId(id);
        updateById(rule);
    }

    @Override
    public void deleteRule(Long id) {
        removeById(id);
    }

    @Override
    public void toggleEnabled(Long id) {
        AlarmRule rule = getById(id);
        if (rule == null) {
            throw new RuntimeException("告警规则不存在");
        }
        rule.setEnabled(rule.getEnabled() == 1 ? 0 : 1);
        updateById(rule);
    }
}
