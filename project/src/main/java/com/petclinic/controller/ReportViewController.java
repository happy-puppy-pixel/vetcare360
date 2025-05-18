package com.petclinic.controller;

import com.petclinic.service.ReportService;
import com.petclinic.util.DatabaseUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class ReportViewController {

    public TextArea reportOutput;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private Button generateButton;
    @FXML
    private Button exportButton;
    @FXML
    private TextArea reportPreviewArea;

    private final ReportService reportService = new ReportService();
    private List<Map<String, Object>> currentReportData;

    @FXML
    public void initialize() {
        // Initialize report types
        reportTypeComboBox.getItems().addAll(
                "Owners Summary",
                "Pets Summary",
                "Appointments"
        );

        // Set default dates
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());

        // Add listeners
        reportTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) ->
                updateDatePickersVisibility(newVal));

        generateButton.setOnAction(event -> generateReport());
        exportButton.setOnAction(event -> exportReport());

        // Initial setup
        reportTypeComboBox.setValue("Owners Summary");
        updateDatePickersVisibility("Owners Summary");
        exportButton.setDisable(true);
    }

    private void updateDatePickersVisibility(String reportType) {
        boolean showDatePickers = "Appointments".equals(reportType);
        startDatePicker.setVisible(showDatePickers);
        startDatePicker.setManaged(showDatePickers);
        endDatePicker.setVisible(showDatePickers);
        endDatePicker.setManaged(showDatePickers);
    }

    @FXML
    private void generateReport() {
        String selectedType = reportTypeComboBox.getValue();
        String reportResult = "";

        switch (selectedType) {
            case "Owners Summary":
                reportResult = generateOwnersSummary();
                break;
            case "Pets Summary":
                reportResult = generatePetsSummary();
                break;
            case "Appointments":
                if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                    reportOutput.setText("⚠️ Please select both start and end dates.");
                    return;
                }
                reportResult = generateAppointmentsReport(startDatePicker.getValue(), endDatePicker.getValue());
                break;
        }

        reportOutput.setText(reportResult);
    }


    private String generateAppointmentsReport(LocalDate start, LocalDate end) {
        StringBuilder report = new StringBuilder("📅 Appointments Report:\n\n");

        String sql = "SELECT a.date_time, a.reason, a.status, " +
                "p.name AS pet_name, CONCAT(o.first_name, ' ', o.last_name) AS owner_name " +
                "FROM appointments a " +
                "JOIN pets p ON a.pet_id = p.id " +
                "JOIN owners o ON p.owner_id = o.id " +
                "WHERE a.date_time BETWEEN ? AND ? " +
                "ORDER BY a.date_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(end.atTime(23, 59, 59)));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    report.append("📅 Date: ").append(rs.getString("date_time")).append("\n");
                    report.append("🐾 Pet: ").append(rs.getString("pet_name")).append("\n");
                    report.append("👤 Owner: ").append(rs.getString("owner_name")).append("\n");
                    report.append("📌 Reason: ").append(rs.getString("reason")).append("\n");
                    report.append("📋 Status: ").append(rs.getString("status")).append("\n");
                    report.append("-----------------------------\n");
                }
            }

        } catch (SQLException e) {
            return "❌ Error generating report: " + e.getMessage();
        }

        return report.toString();
    }


    private String generatePetsSummary() {
        StringBuilder report = new StringBuilder("🐾 Pets Summary:\n\n");

        String sql = "SELECT p.name, p.species, p.breed, p.birth_date, CONCAT(o.first_name, ' ', o.last_name) AS owner_name " +
                "FROM pets p JOIN owners o ON p.owner_id = o.id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.append("🐶 Name: ").append(rs.getString("name")).append("\n");
                report.append("📖 Species: ").append(rs.getString("species")).append("\n");
                report.append("🧬 Breed: ").append(rs.getString("breed")).append("\n");
                report.append("📅 Birth Date: ").append(rs.getString("birth_date")).append("\n");
                report.append("👤 Owner: ").append(rs.getString("owner_name")).append("\n");
                report.append("-----------------------------\n");
            }

        } catch (SQLException e) {
            return "❌ Error generating report: " + e.getMessage();
        }

        return report.toString();
    }


    private String generateOwnersSummary() {
        StringBuilder report = new StringBuilder("📋 Owners Summary:\n\n");

        String sql = "SELECT id, first_name, last_name, phone, email, address FROM owners";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.append("👤 Name: ").append(rs.getString("first_name")).append(" ").append(rs.getString("last_name")).append("\n");
                report.append("📞 Phone: ").append(rs.getString("phone")).append("\n");
                report.append("✉️ Email: ").append(rs.getString("email")).append("\n");
                report.append("🏠 Address: ").append(rs.getString("address")).append("\n");
                report.append("-----------------------------\n");
            }

        } catch (SQLException e) {
            return "❌ Error generating report: " + e.getMessage();
        }

        return report.toString();
    }



    private void displayReportPreview() {
        if (currentReportData == null || currentReportData.isEmpty()) {
            reportPreviewArea.setText("No data available for the selected report type and criteria.");
            return;
        }

        StringBuilder preview = new StringBuilder();

        // Add headers
        preview.append(String.join("\t", currentReportData.get(0).keySet())).append("\n");
        preview.append("-".repeat(80)).append("\n");

        // Add data rows
        for (Map<String, Object> row : currentReportData) {
            preview.append(String.join("\t",
                    row.values().stream()
                            .map(value -> value != null ? value.toString() : "")
                            .toList()
            )).append("\n");
        }

        reportPreviewArea.setText(preview.toString());
    }

    private void exportReport() {
        if (currentReportData == null || currentReportData.isEmpty()) {
            showError("Export Error", "No data to export");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        String defaultFileName = reportTypeComboBox.getValue().toLowerCase().replace(" ", "_") +
                "_report_" + LocalDate.now() + ".csv";
        fileChooser.setInitialFileName(defaultFileName);

        File file = fileChooser.showSaveDialog(getStage());
        if (file != null) {
            reportService.exportReportToCsv(currentReportData, file.getAbsolutePath());
        }
    }

    private Stage getStage() {
        return (Stage) generateButton.getScene().getWindow();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}