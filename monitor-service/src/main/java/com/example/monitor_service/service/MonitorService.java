package com.example.monitor_service.service;

import org.springframework.kafka.support.Acknowledgment;
import com.example.monitor_service.dto.LocationRecord;
import com.example.monitor_service.dto.ArrivalSchedule;

public interface MonitorService {
    void consumeLocation(LocationRecord location, Acknowledgment ack);

    void consumeSchedule(ArrivalSchedule schedule, Acknowledgment ack);

    void printSummary();
}
