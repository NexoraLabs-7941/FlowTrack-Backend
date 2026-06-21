package com.nexoralabs.flowtrack.integration.infrastructure.external.googlesheets;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.nexoralabs.flowtrack.integration.application.internal.outboundservices.GoogleSheetsService;
import com.nexoralabs.flowtrack.integration.domain.model.valueobjects.SheetReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete implementation of the GoogleSheetsService interface.
 * Uses the official Google Sheets API v4 and Google Drive API v3.
 */
@Service
public class GoogleSheetsServiceImpl implements GoogleSheetsService {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetsServiceImpl.class);

    private final Sheets sheetsService;
    private final Drive driveService;
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String defaultSpreadsheetId;

    public GoogleSheetsServiceImpl(
            Sheets sheetsService,
            Drive driveService,
            RestTemplate restTemplate,
            @Value("${integration.google-sheets.api-url}") String apiUrl,
            @Value("${app.google-sheets.default-spreadsheet-id}") String defaultSpreadsheetId) {
        this.sheetsService = sheetsService;
        this.driveService = driveService;
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.defaultSpreadsheetId = defaultSpreadsheetId;
    }

    private void checkServicesInitialized() {
        if (sheetsService == null || driveService == null) {
            throw new IllegalStateException("Google API services (Sheets/Drive) are not initialized. Check credentials and configuration logs.");
        }
    }

    @Override
    public String exportReport(SheetReport report) {
        log.info("Delegating exportReport to exportarReporte for '{}'", report.spreadsheetName());
        List<List<Object>> rows = report.rows();
        List<String> headers = new ArrayList<>();
        List<List<Object>> data = new ArrayList<>();
        
        if (rows != null && !rows.isEmpty()) {
            List<Object> firstRow = rows.get(0);
            for (Object obj : firstRow) {
                headers.add(obj != null ? obj.toString() : "");
            }
            for (int i = 1; i < rows.size(); i++) {
                data.add(rows.get(i));
            }
        }
        
        return exportarReporte(null, report.spreadsheetName(), headers, data, null);
    }

    @Override
    public String exportarReporte(String spreadsheetId, String titulo, List<String> cabeceras, List<List<Object>> datos, String emailDestinatario) {
        checkServicesInitialized();
        
        try {
            String targetSpreadsheetId = spreadsheetId;
            if (targetSpreadsheetId == null || targetSpreadsheetId.isBlank()) {
                targetSpreadsheetId = defaultSpreadsheetId;
            }

            String spreadsheetUrl;

            // 1. Create a new sheet if no existing sheetId is supplied and no default sheet ID is configured
            if (targetSpreadsheetId == null || targetSpreadsheetId.isBlank()) {
                log.info("No spreadsheetId or defaultSpreadsheetId provided. Attempting to create a new Google Sheet: '{}'", titulo);
                try {
                    Spreadsheet spreadsheet = new Spreadsheet()
                            .setProperties(new SpreadsheetProperties().setTitle(titulo));
                    
                    Spreadsheet response = sheetsService.spreadsheets().create(spreadsheet)
                            .execute();
                    
                    targetSpreadsheetId = response.getSpreadsheetId();
                    spreadsheetUrl = response.getSpreadsheetUrl();
                    log.info("Created spreadsheet with ID: {}", targetSpreadsheetId);
                } catch (IOException e) {
                    log.error("Failed to create new spreadsheet due to Google API storage quota limits", e);
                    throw new RuntimeException("No se pudo crear una nueva hoja de cálculo. Google limita la creación directa a cuentas con cuota de almacenamiento. SOLUCIÓN: Crea una hoja de cálculo en blanco en tu propio Drive personal, compártela con el correo '" + 
                            "firebase-adminsdk-fbsvc@flowtracknotis.iam.gserviceaccount.com" + 
                            "' como EDITOR y envía el ID de ese documento en el parámetro 'spreadsheetId' de la petición. Detalle: " + e.getMessage(), e);
                }
            } else {
                log.info("Using spreadsheetId: {}", targetSpreadsheetId);
                spreadsheetUrl = "https://docs.google.com/spreadsheets/d/" + targetSpreadsheetId;
            }

            // Ensure sheet/tab exists dynamically in the spreadsheet
            Spreadsheet spreadsheetInfo = sheetsService.spreadsheets().get(targetSpreadsheetId).execute();
            List<Sheet> sheets = spreadsheetInfo.getSheets();
            
            Integer sheetId = null;
            boolean tabExists = false;
            String searchTitle = (titulo != null && !titulo.isBlank()) ? titulo.trim() : "Reporte";
            for (Sheet sheet : sheets) {
                if (sheet.getProperties().getTitle().equalsIgnoreCase(searchTitle)) {
                    sheetId = sheet.getProperties().getSheetId();
                    tabExists = true;
                    break;
                }
            }

            if (!tabExists) {
                log.info("Sheet tab '{}' does not exist. Creating it...", searchTitle);
                AddSheetRequest addSheetRequest = new AddSheetRequest()
                        .setProperties(new SheetProperties().setTitle(searchTitle));
                
                Request request = new Request().setAddSheet(addSheetRequest);
                BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest()
                        .setRequests(Collections.singletonList(request));
                
                BatchUpdateSpreadsheetResponse batchResponse = sheetsService.spreadsheets()
                        .batchUpdate(targetSpreadsheetId, batchRequest)
                        .execute();
                
                SheetProperties createdProperties = batchResponse.getReplies().get(0)
                        .getAddSheet().getProperties();
                sheetId = createdProperties.getSheetId();
                log.info("Created sheet tab '{}' with sheetId {}", searchTitle, sheetId);
            }

            // Clear the existing values in the specific tab (range A1:Z1000) to overwrite cleanly
            String safeTitle = searchTitle.replace("'", "''");
            String clearRange = "'" + safeTitle + "'!A1:Z1000";
            try {
                sheetsService.spreadsheets().values()
                        .clear(targetSpreadsheetId, clearRange, new ClearValuesRequest())
                        .execute();
                log.info("Cleared range '{}' in spreadsheet {}", clearRange, targetSpreadsheetId);
            } catch (IOException e) {
                log.warn("Could not clear range '{}' in spreadsheet ID: {}. Error: {}", clearRange, targetSpreadsheetId, e.getMessage());
            }

            // 2. Prepare data payload (Headers + Data)
            List<List<Object>> values = new ArrayList<>();
            if (cabeceras != null && !cabeceras.isEmpty()) {
                values.add(new ArrayList<>(cabeceras));
            }
            if (datos != null && !datos.isEmpty()) {
                values.addAll(datos);
            }

            // 3. Write data to the spreadsheet (using cell range SafeTitle!A1)
            if (!values.isEmpty()) {
                ValueRange body = new ValueRange().setValues(values);
                String writeRange = "'" + safeTitle + "'!A1";
                try {
                    sheetsService.spreadsheets().values()
                            .update(targetSpreadsheetId, writeRange, body)
                            .setValueInputOption("RAW")
                            .execute();
                    log.info("Wrote {} rows of data (including headers) to range {} in spreadsheet {}", values.size(), writeRange, targetSpreadsheetId);
                } catch (IOException e) {
                    log.error("Failed to write values to range {} in spreadsheet ID: {}", writeRange, targetSpreadsheetId, e);
                    throw new RuntimeException("Error escribiendo en la hoja de cálculo. Asegúrate de compartir tu documento con el correo '" +
                            "firebase-adminsdk-fbsvc@flowtracknotis.iam.gserviceaccount.com" +
                            "' dándole permisos de EDITOR. Detalle: " + e.getMessage(), e);
                }
            }

            // 4. Style headers to BOLD in the specific tab
            if (cabeceras != null && !cabeceras.isEmpty() && sheetId != null) {
                try {
                    CellFormat boldFormat = new CellFormat()
                            .setTextFormat(new TextFormat().setBold(true));
                    
                    GridRange gridRange = new GridRange()
                            .setSheetId(sheetId)
                            .setStartRowIndex(0)
                            .setEndRowIndex(1)
                            .setStartColumnIndex(0)
                            .setEndColumnIndex(cabeceras.size());

                    RepeatCellRequest repeatCellRequest = new RepeatCellRequest()
                            .setRange(gridRange)
                            .setCell(new CellData().setUserEnteredFormat(boldFormat))
                            .setFields("userEnteredFormat.textFormat.bold");

                    Request request = new Request().setRepeatCell(repeatCellRequest);
                    BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest()
                            .setRequests(Collections.singletonList(request));

                    sheetsService.spreadsheets().batchUpdate(targetSpreadsheetId, batchRequest).execute();
                    log.info("Formatted header row as bold on sheetId {}", sheetId);
                } catch (Exception e) {
                    log.warn("Could not apply bold formatting to headers on sheetId {} (continuing execution)", sheetId, e);
                }
            }

            // 5. Share with recipient email using Drive API
            if (emailDestinatario != null && !emailDestinatario.isBlank()) {
                shareSpreadsheet(targetSpreadsheetId, emailDestinatario);
            }

            return spreadsheetUrl;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Google Sheets integration error: " + e.getMessage(), e);
        }
    }

    private void shareSpreadsheet(String fileId, String emailAddress) {
        log.info("Sharing file ID {} with {}", fileId, emailAddress);
        try {
            Permission permission = new Permission()
                    .setType("user")
                    .setRole("writer")
                    .setEmailAddress(emailAddress);
            
            driveService.permissions().create(fileId, permission)
                    .setSendNotificationEmail(true)
                    .execute();
            log.info("Successfully shared file with notification to {}", emailAddress);
        } catch (Exception e) {
            log.warn("Failed to share file with notification. Retrying without notification email...", e);
            try {
                Permission permission = new Permission()
                        .setType("user")
                        .setRole("writer")
                        .setEmailAddress(emailAddress);
                
                driveService.permissions().create(fileId, permission)
                        .setSendNotificationEmail(false)
                        .execute();
                log.info("Successfully shared file without notification to {}", emailAddress);
            } catch (Exception ex) {
                log.error("Failed to share file even without notification email for {}", emailAddress, ex);
            }
        }
    }

    @Override
    public void resetSpreadsheet(String spreadsheetId) {
        checkServicesInitialized();
        try {
            String targetSpreadsheetId = (spreadsheetId != null && !spreadsheetId.isBlank()) 
                    ? spreadsheetId 
                    : defaultSpreadsheetId;

            log.info("Attempting to reset spreadsheet ID: {}", targetSpreadsheetId);
            Spreadsheet spreadsheetInfo = sheetsService.spreadsheets().get(targetSpreadsheetId).execute();
            List<Sheet> sheets = spreadsheetInfo.getSheets();

            // We must always keep at least one sheet tab.
            // Check if there is a tab called "Inicio". If not, create it.
            Integer inicioSheetId = null;
            for (Sheet sheet : sheets) {
                if (sheet.getProperties().getTitle().equalsIgnoreCase("Inicio")) {
                    inicioSheetId = sheet.getProperties().getSheetId();
                    break;
                }
            }

            List<Request> requests = new ArrayList<>();

            if (inicioSheetId == null) {
                // Create "Inicio" tab first
                log.info("Creating 'Inicio' tab as placeholder for reset in spreadsheet {}", targetSpreadsheetId);
                AddSheetRequest addSheetRequest = new AddSheetRequest()
                        .setProperties(new SheetProperties().setTitle("Inicio"));
                Request request = new Request().setAddSheet(addSheetRequest);
                BatchUpdateSpreadsheetResponse addResponse = sheetsService.spreadsheets()
                        .batchUpdate(targetSpreadsheetId, new BatchUpdateSpreadsheetRequest().setRequests(Collections.singletonList(request)))
                        .execute();
                inicioSheetId = addResponse.getReplies().get(0).getAddSheet().getProperties().getSheetId();
                
                // Refresh sheet metadata to get latest sheets list
                spreadsheetInfo = sheetsService.spreadsheets().get(targetSpreadsheetId).execute();
                sheets = spreadsheetInfo.getSheets();
            }

            // Delete all sheets other than "Inicio"
            for (Sheet sheet : sheets) {
                Integer currentId = sheet.getProperties().getSheetId();
                if (!currentId.equals(inicioSheetId)) {
                    requests.add(new Request().setDeleteSheet(new DeleteSheetRequest().setSheetId(currentId)));
                }
            }

            if (!requests.isEmpty()) {
                BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                sheetsService.spreadsheets().batchUpdate(targetSpreadsheetId, batchRequest).execute();
                log.info("Reset spreadsheet ID {}: deleted all tabs except 'Inicio'", targetSpreadsheetId);
            } else {
                log.info("Spreadsheet ID {} was already clean (only 'Inicio' tab existed)", targetSpreadsheetId);
            }
        } catch (Exception e) {
            log.error("Failed to reset Google Sheet", e);
            throw new RuntimeException("Error al reiniciar la hoja de cálculo: " + e.getMessage(), e);
        }
    }
}
