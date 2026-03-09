package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ems.entity.WorkOrder;
import com.ems.mapper.WorkOrderMapper;
import com.ems.service.WorkOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {

    @Override
    public Page<WorkOrder> listPage(int page, int pageSize, String keyword, String status, String type) {
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(WorkOrder::getTitle, keyword)
                    .or().like(WorkOrder::getCode, keyword));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(WorkOrder::getStatus, status);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(WorkOrder::getType, type);
        }
        wrapper.orderByDesc(WorkOrder::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    public WorkOrder detail(Long id) {
        return getById(id);
    }

    @Override
    public void createWorkOrder(WorkOrder workOrder) {
        save(workOrder);
    }

    @Override
    public void updateWorkOrder(Long id, WorkOrder workOrder) {
        workOrder.setId(id);
        updateById(workOrder);
    }

    @Override
    public void deleteWorkOrder(Long id) {
        removeById(id);
    }

    @Override
    public void updateStatus(Long id, String status) {
        WorkOrder workOrder = getById(id);
        if (workOrder == null) {
            throw new RuntimeException("工单不存在");
        }
        workOrder.setStatus(status);
        updateById(workOrder);
    }
}
