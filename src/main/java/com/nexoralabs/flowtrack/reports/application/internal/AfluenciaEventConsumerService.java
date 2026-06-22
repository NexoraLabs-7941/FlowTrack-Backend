package com.nexoralabs.flowtrack.reports.application.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoralabs.flowtrack.reports.domain.model.entities.RegistroAfluencia;
import com.nexoralabs.flowtrack.reports.infrastructure.dto.AfluenciaKafkaEventDto;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories.RegistroAfluenciaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AfluenciaEventConsumerService {

    private final ObjectMapper objectMapper;
    private final RegistroAfluenciaRepository registroAfluenciaRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public AfluenciaEventConsumerService(
            ObjectMapper objectMapper,
            RegistroAfluenciaRepository registroAfluenciaRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.objectMapper = objectMapper;
        this.registroAfluenciaRepository = registroAfluenciaRepository;
        this.messagingTemplate = messagingTemplate;
    }

//    @Transactional
//    @KafkaListener(topics = "afluencia_topic", groupId = "${spring.kafka.consumer.group-id:flowtrack-group}")
    public void consumirEventoAfluencia(String payload) throws JsonProcessingException {
        AfluenciaKafkaEventDto evento = objectMapper.readValue(payload, AfluenciaKafkaEventDto.class);

        RegistroAfluencia registro = new RegistroAfluencia(
                evento.timestamp(),
                evento.cantidad(),
                evento.evento(),
                evento.camaraId()
        );

        registroAfluenciaRepository.save(registro);

        // Publica el mismo contrato recibido desde Python para que Angular lo consuma en tiempo real por STOMP.
        messagingTemplate.convertAndSend("/topic/envivo", evento);
    }
}
