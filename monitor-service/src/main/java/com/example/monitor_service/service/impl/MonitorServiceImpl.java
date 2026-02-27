package com.example.monitor_service.service.impl;

import com.example.monitor_service.entity.ConsolidatedSummary;
import com.example.monitor_service.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.monitor_service.dto.LocationRecord;
import com.example.monitor_service.dto.ArrivalSchedule;
import com.example.monitor_service.entity.MonitorRecord;
import com.example.monitor_service.repository.MonitorRepository;
import com.example.monitor_service.service.MonitorService;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private MonitorRepository repository;

    @Autowired
    private SummaryRepository summaryRepository;

    @Override
    @KafkaListener(topics = "ubicaciones_vehiculos", groupId = "monitor-group")
    public void consumeLocation(LocationRecord location, Acknowledgment ack) {
        try {
            MonitorRecord record = new MonitorRecord();
            record.setPatente(location.getPatente());
            record.setLatitud(location.getLatitud());
            record.setLongitud(location.getLongitud());
            record.setTipo("UBICACION");
            repository.save(record);
            ack.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @KafkaListener(topics = "horarios", groupId = "monitor-group")
    public void consumeSchedule(ArrivalSchedule schedule, Acknowledgment ack) {
        try {
            MonitorRecord record = new MonitorRecord();
            record.setPatente(schedule.getPatente());
            record.setHorarioDeLlegada(schedule.getHorarioDeLlegada());
            record.setTipo("HORARIO");
            repository.save(record);
            ack.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Scheduled(fixedRate = 300000)
    public void printSummary() {
        LocalDateTime lastTime = LocalDateTime.now().minusMinutes(5);
        List<MonitorRecord> records = repository.findByTimestampAfter(lastTime);
        System.out.println("--- RESUMEN CONSOLIDADO (ÚLTIMOS 5 MINUTOS) ---");

        java.util.Map<String, java.util.List<MonitorRecord>> grouped = records.stream()
                .collect(java.util.stream.Collectors.groupingBy(MonitorRecord::getPatente));

        grouped.forEach((patente, vehicleRecords) -> {
            ConsolidatedSummary summary = new ConsolidatedSummary();
            summary.setPatente(patente);

            for (MonitorRecord r : vehicleRecords) {
                if ("UBICACION".equals(r.getTipo())) {
                    summary.setLatitud(r.getLatitud());
                    summary.setLongitud(r.getLongitud());
                } else if ("HORARIO".equals(r.getTipo())) {
                    summary.setHorarioDeLlegada(r.getHorarioDeLlegada());
                }
            }

            summaryRepository.save(summary);
            System.out.println("PERSISTIDO - Vehículo: " + patente +
                    " | Llegada: " + (summary.getHorarioDeLlegada() != null ? summary.getHorarioDeLlegada() : "N/A") +
                    " | Ubicación: [" + summary.getLatitud() + ", " + summary.getLongitud() + "]");
        });
        System.out.println("----------------------------------------------");
    }
}
