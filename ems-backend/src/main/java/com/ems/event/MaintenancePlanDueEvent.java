package com.ems.event;

import com.ems.entity.MaintenancePlan;
import org.springframework.context.ApplicationEvent;

public class MaintenancePlanDueEvent extends ApplicationEvent {
    private final MaintenancePlan plan;

    public MaintenancePlanDueEvent(Object source, MaintenancePlan plan) {
        super(source);
        this.plan = plan;
    }

    public MaintenancePlan getPlan() {
        return plan;
    }
}
