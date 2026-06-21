package com.nexoralabs.flowtrack.integration.interfaces.acl;

import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.NotificationDetails;
import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.TelegramMessage;
import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.SheetReport;

/**
 * ACL Facade interface for interacting with the Integration Bounded Context.
 */
public interface IntegrationContextFacade {
    /**
     * Export report data to Google Sheets.
     * @param report The SheetReport containing metadata and rows
     * @return The URL of the generated Google Sheet
     */
    String exportReport(SheetReport report);

    /**
     * Send a critical alert message via Telegram Bot.
     * @param message The TelegramMessage containing chatId and content
     */
    void sendTelegramAlert(TelegramMessage message);

    /**
     * Send a push notification using Firebase Cloud Messaging.
     * @param notification The NotificationDetails containing token, title, body, and payload data
     */
    void sendPushNotification(NotificationDetails notification);
}
