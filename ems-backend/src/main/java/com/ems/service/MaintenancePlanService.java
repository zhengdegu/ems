package com.ems.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.MaintenancePlan;

public interface MaintenancePlanService extends IService<MaintenancePlan> {

    Page<MaintenancePlan> listPage(int page, int pageSize, String keyword, String status, String type);

    MaintenancePlan detail(Long id);

    void createPlan(MaintenancePlan plan);

    void updatePlan(Long id, MaintenancePlan plan);

    void deletePlan(Long id);

    void toggleEnabled(Long id);
}
