package com.ems.event;

import com.ems.entity.Alarm;
import org.springframework.context.ApplicationEvent;

public class AlarmCreatedEvent extends ApplicationEvent {
    private final Alarm alarm;

    public AlarmCreatedEvent(Object source, Alarm alarm) {
        super(source);
        this.alarm = alarm;
    }

    public Alarm getAlarm() {
        return alarm;
    }
}
