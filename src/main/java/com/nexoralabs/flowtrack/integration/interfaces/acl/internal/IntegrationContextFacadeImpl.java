package com.nexoralabs.flowtrack.integration.interfaces.acl.internal;

import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.FirebaseNotificationService;
import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.GoogleSheetsService;
import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.NotificationDetails;
import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.SheetReport;
import com.nexoralabs.flowtrack.integration.interfaces.acl.IntegrationContextFacade;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of the IntegrationContextFacade.
 * Orchestrates calls to the internal application outbound services.
 */
@Component
public class IntegrationContextFacadeImpl implements IntegrationContextFacade {

    private final GoogleSheetsService googleSheetsService;
    private final FirebaseNotificationService firebaseNotificationService;

    public IntegrationContextFacadeImpl(GoogleSheetsService googleSheetsService,
                                         FirebaseNotificationService firebaseNotificationService) {
        this.googleSheetsService = googleSheetsService;
        this.firebaseNotificationService = firebaseNotificationService;
    }

    @Override
    public String exportReport(SheetReport report) {
        return googleSheetsService.exportReport(report);
    }

    @Override
    public void sendPushNotification(NotificationDetails notification) {
        firebaseNotificationService.sendPushNotification(notification);
    }
}
