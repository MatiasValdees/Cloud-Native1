package com.example.location_producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.location_producer.dto.LocationRecord;
import com.example.location_producer.service.KafkaProducerService;

@RestController
public class ProducerController {

	@Autowired
	private KafkaProducerService producerService;

	@PostMapping("/ubicaciones")
	public String registrarUbicacion(@RequestBody() LocationRecord request) {

		producerService.registrarUbicacion(request);

		return "Ubicación registrada: " + request.toString();
	}
}
