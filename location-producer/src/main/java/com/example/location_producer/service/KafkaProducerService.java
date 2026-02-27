package com.example.location_producer.service;

import com.example.location_producer.dto.LocationRecord;

public interface KafkaProducerService {

	void registrarUbicacion(LocationRecord ubicacion);
}
