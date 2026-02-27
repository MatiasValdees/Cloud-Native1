package com.example.schedule_processor.service;

import org.springframework.kafka.support.Acknowledgment;

import com.example.schedule_processor.dto.LocationRecord;

public interface KafkaConsumerService {

	void consumirUbicacion(LocationRecord notificacion, Acknowledgment ack);
}
