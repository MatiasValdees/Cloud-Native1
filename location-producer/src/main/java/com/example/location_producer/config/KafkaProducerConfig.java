package com.example.location_producer.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.example.location_producer.dto.LocationRecord;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	public static final String TOPIC = "ubicaciones_vehiculos";

	@Bean
	KafkaAdmin kafkaAdmin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put("bootstrap.servers", bootstrapServers);
		return new KafkaAdmin(configs);
	}

	@Bean
	NewTopic topicUbicaciones() {
		return new NewTopic(TOPIC, 3, (short) 2);
	}

	@Bean
	ProducerFactory<String, LocationRecord> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		configProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,
				"org.apache.kafka.clients.producer.RoundRobinPartitioner");
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	KafkaTemplate<String, LocationRecord> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}