package com.example.schedule_processor.service;

public interface KafkaAdminListenerService {

	void pausarListener(String id);

	void reanudarListener(String id);

	boolean obtenerEstadoListener(String id);
}
