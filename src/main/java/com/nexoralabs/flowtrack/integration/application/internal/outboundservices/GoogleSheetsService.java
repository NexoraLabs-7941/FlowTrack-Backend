package com.nexoralabs.flowtrack.integration.application.internal.outboundservices;

import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.SheetReport;

/**
 * Service interface for Google Sheets API integration.
 */
public interface GoogleSheetsService {
    /**
     * Exports a report to a new or existing Google Sheet.
     * @param report The SheetReport containing metadata and rows
     * @return The URL or identifier of the exported sheet
     */
    String exportReport(SheetReport report);

    /**
     * Exports a report to a new Google Sheet, formats headers as bold, and shares it with the recipient email.
     * @param spreadsheetId Optional existing spreadsheet ID (bypasses quota limits)
     * @param titulo The title of the spreadsheet
     * @param cabeceras The header columns
     * @param datos The data rows
     * @param emailDestinatario The target Gmail/Google account email to share the spreadsheet with
     * @return The public URL of the created spreadsheet
     */
    String exportarReporte(String spreadsheetId, String titulo, java.util.List<String> cabeceras, java.util.List<java.util.List<Object>> datos, String emailDestinatario);

    /**
     * Resets the spreadsheet by deleting all sheets except a default 'Inicio' sheet.
     * @param spreadsheetId Optional target spreadsheet ID, defaults to the system default if null or empty
     */
    void resetSpreadsheet(String spreadsheetId);
}

