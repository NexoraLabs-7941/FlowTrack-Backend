package com.nexoralabs.flowtrack.integration.domain.model.valueobjects;

import java.util.List;

/**
 * Value object representing report export details for Google Sheets.
 */
public record SheetReport(String spreadsheetName, List<List<Object>> rows) {
    public SheetReport {
        if (spreadsheetName == null || spreadsheetName.isBlank()) {
            throw new IllegalArgumentException("Spreadsheet name must not be null or blank");
        }
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("Rows must not be null or empty");
        }
    }
}
