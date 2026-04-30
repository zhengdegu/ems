package com.ems.event;

import com.ems.entity.WorkOrder;
import org.springframework.context.ApplicationEvent;

public class WorkOrderCreatedEvent extends ApplicationEvent {
    private final WorkOrder workOrder;

    public WorkOrderCreatedEvent(Object source, WorkOrder workOrder) {
        super(source);
        this.workOrder = workOrder;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }
}
