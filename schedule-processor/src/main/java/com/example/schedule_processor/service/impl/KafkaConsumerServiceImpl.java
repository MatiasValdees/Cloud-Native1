package com.example.schedule_processor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.example.schedule_processor.config.KafkaConsumerConfig;
import com.example.schedule_processor.config.KafkaProducerConfig;
import com.example.schedule_processor.dto.ArrivalSchedule;
import com.example.schedule_processor.dto.LocationRecord;
import com.example.schedule_processor.service.KafkaConsumerService;

@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

	@Autowired
	private KafkaTemplate<String, ArrivalSchedule> kafkaTemplate;

	@Override
	@KafkaListener(id = "locationListener", topics = KafkaConsumerConfig.TOPIC, groupId = KafkaConsumerConfig.CONSUMER_GROUP_ID)
	public void consumirUbicacion(LocationRecord ubicacion, Acknowledgment ack) {

		try {
			System.out.println("Ubicación recibida: " + ubicacion.toString());

			String horarioLlegada = java.time.LocalDateTime.now().plusHours(1).toString();
			ArrivalSchedule schedule = new ArrivalSchedule(ubicacion.getPatente(), horarioLlegada);

			kafkaTemplate.send(KafkaProducerConfig.TOPIC_SCHEDULES, schedule);
			System.out.println("Horario publicado: " + schedule.toString());

			ack.acknowledge();
		} catch (Exception e) {
			System.out.println("Error procesando ubicación");
			e.printStackTrace();
		}
	}
}
