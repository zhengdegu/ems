package com.ems.event;

import com.ems.entity.Equipment;
import org.springframework.context.ApplicationEvent;

public class EquipmentStatusChangedEvent extends ApplicationEvent {
    private final Equipment equipment;
    private final String oldStatus;
    private final String newStatus;

    public EquipmentStatusChangedEvent(Object source, Equipment equipment, String oldStatus, String newStatus) {
        super(source);
        this.equipment = equipment;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }
}
