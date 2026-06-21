package com.nexoralabs.flowtrack.integration.interfaces.rest.resources;

import java.util.List;

/**
 * Resource representing a request to export data to Google Sheets.
 */
public record GoogleSheetsReportResource(
        String spreadsheetName,
        List<List<Object>> rows
) {
    public GoogleSheetsReportResource {
        if (spreadsheetName == null || spreadsheetName.isBlank()) {
            throw new IllegalArgumentException("Spreadsheet name must not be null or blank");
        }
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("Rows must not be null or empty");
        }
    }
}
