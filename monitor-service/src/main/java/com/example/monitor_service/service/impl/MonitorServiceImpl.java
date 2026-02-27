package com.example.monitor_service.service.impl;

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
    @Scheduled(fixedRate = 120000)
    public void printSummary() {
        LocalDateTime lastTime = LocalDateTime.now().minusMinutes(2);
        List<MonitorRecord> records = repository.findByTimestampAfter(lastTime);
        System.out.println("--- RESUMEN DE ÚLTIMOS 2 MINUTOS ---");
        records.forEach(r -> {
            if ("UBICACION".equals(r.getTipo())) {
                System.out.println("Location: " + r.getPatente() + " [" + r.getLatitud() + "," + r.getLongitud() + "]");
            } else {
                System.out.println("Schedule: " + r.getPatente() + " arrival at " + r.getHorarioDeLlegada());
            }
        });
        System.out.println("--------------------------------");
    }
}
