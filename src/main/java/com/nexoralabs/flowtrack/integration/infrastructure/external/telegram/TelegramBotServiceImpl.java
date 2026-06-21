package com.nexoralabs.flowtrack.integration.infrastructure.external.telegram;

import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.TelegramBotService;
import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.TelegramMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of the TelegramBotService interface.
 */
@Service
public class TelegramBotServiceImpl implements TelegramBotService {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotServiceImpl.class);
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String botToken;

    public TelegramBotServiceImpl(RestTemplate restTemplate,
                                  @Value("${integration.telegram.api-url}") String apiUrl,
                                  @Value("${integration.telegram.bot-token}") String botToken) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.botToken = botToken;
    }

    @Override
    public void sendAlert(TelegramMessage message) {
        log.info("Sending critical alert message via Telegram Bot (API URL: {})", apiUrl);
        log.info("Target Chat ID: {}", message.chatId());
        
        String fullUrl = String.format("%s%s/sendMessage", apiUrl, botToken);
        
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("chat_id", message.chatId());
            payload.put("text", message.text());
            payload.put("parse_mode", "Markdown");
            
            log.debug("Telegram API request payload: {}", payload);
            
            if ("mock_bot_token".equals(botToken)) {
                log.info("[MOCK] Sent Telegram Alert successfully: {}", message.text());
            } else {
                restTemplate.postForLocation(fullUrl, payload);
                log.info("Telegram Alert successfully sent.");
            }
        } catch (Exception e) {
            log.error("Failed to send Telegram alert", e);
            throw new RuntimeException("Telegram integration error: " + e.getMessage());
        }
    }
}
