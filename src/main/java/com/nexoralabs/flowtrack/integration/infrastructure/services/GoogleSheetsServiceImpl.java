package com.nexoralabs.flowtrack.integration.infrastructure.services;

import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.GoogleSheetsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Stub implementation of the GoogleSheetsService.
 */
@Service
public class GoogleSheetsServiceImpl implements GoogleSheetsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheetsServiceImpl.class);

    @Override
    public void exportReport(String sheetTitle, List<String> headers, List<Map<String, Object>> rows) {
        LOGGER.info("Stub: Exporting report to Google Sheets with title: '{}'. Header count: {}, Row count: {}",
                sheetTitle, headers.size(), rows.size());
        // TODO: Implement actual connection to Google Sheets API
    }
}
