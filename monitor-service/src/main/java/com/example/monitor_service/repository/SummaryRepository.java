package com.example.monitor_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.monitor_service.entity.ConsolidatedSummary;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryRepository extends JpaRepository<ConsolidatedSummary, Long> {
}
