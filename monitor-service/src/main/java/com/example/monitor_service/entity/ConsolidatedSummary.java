package com.example.monitor_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class ConsolidatedSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patente;
    private Double latitud;
    private Double longitud;
    private String horarioDeLlegada;
    private LocalDateTime timestamp = LocalDateTime.now();
}
