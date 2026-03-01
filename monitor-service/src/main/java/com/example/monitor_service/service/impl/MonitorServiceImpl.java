package com.example.monitor_service.service.impl;

import com.example.monitor_service.entity.ConsolidatedSummary;
import com.example.monitor_service.repository.SummaryRepository;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
            log.info("Guardando Resumen de ubicacion: " + record);
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
    @Scheduled(fixedRate = 180000) // 3 minutos
    public void printSummary() {
        LocalDateTime lastTime = LocalDateTime.now().minusMinutes(3); // Solo traer los de los últimos 3 mins
        List<MonitorRecord> records = repository.findByTimestampAfter(lastTime);
        System.out.println("--- RESUMEN CONSOLIDADO (ÚLTIMOS 3 MINUTOS) ---");

        java.util.Map<String, java.util.List<MonitorRecord>> grouped = records.stream()
                .collect(java.util.stream.Collectors.groupingBy(MonitorRecord::getPatente));

        grouped.forEach((patente, vehicleRecords) -> {
            // Filtrar solo los registros de ubicación, y ordenarlos por timestamp de menor
            // a mayor
            List<MonitorRecord> locationRecords = vehicleRecords.stream()
                    .filter(r -> "UBICACION".equals(r.getTipo()))
                    .sorted(java.util.Comparator.comparing(MonitorRecord::getTimestamp))
                    .collect(java.util.stream.Collectors.toList());

            if (locationRecords.size() >= 2) {
                MonitorRecord start = locationRecords.get(0);
                MonitorRecord end = locationRecords.get(locationRecords.size() - 1);

                long seconds = java.time.Duration.between(start.getTimestamp(), end.getTimestamp()).getSeconds();

                String mensaje = String.format(
                        "PERSISTIDO - Vehículo: %s recorrió desde [%.4f, %.4f] hasta [%.4f, %.4f] en %d segundos",
                        patente, start.getLatitud(), start.getLongitud(), end.getLatitud(), end.getLongitud(), seconds);

                System.out.println(mensaje);

                // Guardar en tabla summary como veníamos haciendo (usando la última ubicación)
                ConsolidatedSummary summary = new ConsolidatedSummary();
                summary.setPatente(patente);
                summary.setLatitud(end.getLatitud());
                summary.setLongitud(end.getLongitud());
                // Buscamos si hay horario registrado en este lapso
                vehicleRecords.stream()
                        .filter(r -> "HORARIO".equals(r.getTipo()))
                        .map(MonitorRecord::getHorarioDeLlegada)
                        .reduce((first, second) -> second) // tomar el último
                        .ifPresent(summary::setHorarioDeLlegada);

                summaryRepository.save(summary);
            }
        });
        System.out.println("----------------------------------------------");
    }
}
