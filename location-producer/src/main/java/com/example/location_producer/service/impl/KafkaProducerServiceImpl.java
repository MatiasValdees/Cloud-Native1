package com.example.location_producer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.location_producer.config.KafkaProducerConfig;
import com.example.location_producer.dto.LocationRecord;
import com.example.location_producer.service.KafkaProducerService;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

	@Autowired
	private KafkaTemplate<String, LocationRecord> kafkaTemplate;

	@Override
	public void registrarUbicacion(LocationRecord ubicacion) {
		kafkaTemplate.send(KafkaProducerConfig.TOPIC, ubicacion);
	}
}
