package com.ems.event;

import com.ems.entity.WorkOrder;
import org.springframework.context.ApplicationEvent;

public class WorkOrderStatusChangedEvent extends ApplicationEvent {
    private final WorkOrder workOrder;
    private final String oldStatus;
    private final String newStatus;

    public WorkOrderStatusChangedEvent(Object source, WorkOrder workOrder, String oldStatus, String newStatus) {
        super(source);
        this.workOrder = workOrder;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }
}
