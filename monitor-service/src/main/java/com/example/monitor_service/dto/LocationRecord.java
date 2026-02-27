package com.example.monitor_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LocationRecord {
	private String patente;
	private Double latitud;
	private Double longitud;
}
