package com.nexoralabs.flowtrack.integration.application.internal.outboundservices;

import java.util.List;
import java.util.Map;

/**
 * Service contract for Google Sheets integration.
 */
public interface GoogleSheetsService {
    /**
     * Exports raw data list to a Google Sheet.
     *
     * @param sheetTitle Title of the sheet
     * @param headers    Column headers
     * @param rows       Map of data rows
     */
    void exportReport(String sheetTitle, List<String> headers, List<Map<String, Object>> rows);
}
