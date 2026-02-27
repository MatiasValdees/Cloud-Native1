package com.example.monitor_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.monitor_service.entity.MonitorRecord;
import java.util.List;
import java.time.LocalDateTime;

public interface MonitorRepository extends JpaRepository<MonitorRecord, Long> {
    List<MonitorRecord> findByTimestampAfter(LocalDateTime timestamp);
}
