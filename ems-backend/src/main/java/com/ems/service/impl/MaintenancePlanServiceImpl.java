package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.MaintenancePlan;
import com.ems.mapper.MaintenancePlanMapper;
import com.ems.service.MaintenancePlanService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MaintenancePlanServiceImpl extends ServiceImpl<MaintenancePlanMapper, MaintenancePlan> implements MaintenancePlanService {

    @Override
    public Page<MaintenancePlan> listPage(int page, int pageSize, String keyword, String status, String type) {
        LambdaQueryWrapper<MaintenancePlan> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(MaintenancePlan::getName, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(MaintenancePlan::getStatus, status);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(MaintenancePlan::getType, type);
        }
        wrapper.orderByDesc(MaintenancePlan::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public MaintenancePlan detail(Long id) {
        return getById(id);
    }

    @Override
    public void createPlan(MaintenancePlan plan) {
        save(plan);
    }

    @Override
    public void updatePlan(Long id, MaintenancePlan plan) {
        plan.setId(id);
        updateById(plan);
    }

    @Override
    public void deletePlan(Long id) {
        removeById(id);
    }

    @Override
    public void toggleEnabled(Long id) {
        MaintenancePlan plan = getById(id);
        if (plan == null) {
            throw new RuntimeException("维护计划不存在");
        }
        plan.setStatus("enabled".equals(plan.getStatus()) ? "disabled" : "enabled");
        updateById(plan);
    }
}
