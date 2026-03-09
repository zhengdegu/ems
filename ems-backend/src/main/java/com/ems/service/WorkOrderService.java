package com.ems.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.WorkOrder;

public interface WorkOrderService extends IService<WorkOrder> {

    Page<WorkOrder> listPage(int page, int pageSize, String keyword, String status, String type);

    WorkOrder detail(Long id);

    void createWorkOrder(WorkOrder workOrder);

    void updateWorkOrder(Long id, WorkOrder workOrder);

    void deleteWorkOrder(Long id);

    void updateStatus(Long id, String status);
}
