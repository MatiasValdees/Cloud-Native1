package com.example.location_producer.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.location_producer.dto.LocationRecord;
import com.example.location_producer.service.KafkaProducerService;
import java.util.Random;

@Component
public class MockLocationTask {

    private static final Logger logger = LoggerFactory.getLogger(MockLocationTask.class);

    @Autowired
    private KafkaProducerService producerService;

    private final Random random = new Random();
    private final String[] patentes = { "ABC-123", "XYZ-789", "JKL-456", "MNO-012" };

    @Scheduled(fixedRate = 60000, initialDelay = 5000)
    public void sendMockLocation() {
        String patente = patentes[random.nextInt(patentes.length)];
        double latitud = -33.4 + (random.nextDouble() * 0.1);
        double longitud = -70.6 + (random.nextDouble() * 0.1);

        LocationRecord location = new LocationRecord(patente, latitud, longitud);
        logger.info("Enviando mensaje al tópico para patente: {}", location.getPatente());
        producerService.registrarUbicacion(location);
    }
}
