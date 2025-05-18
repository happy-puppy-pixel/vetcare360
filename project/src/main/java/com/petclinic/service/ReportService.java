package com.petclinic.service;

import com.petclinic.util.DatabaseUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    public List<Map<String, Object>> generateOwnerReport() throws SQLException {
        List<Map<String, Object>> reportData = new ArrayList<>();

        String query = """
            SELECT 
                o.id,
                o.first_name,
                o.last_name,
                o.email,
                o.phone,
                COUNT(p.id) as pet_count,
                MAX(p.created_at) as last_pet_added
            FROM owners o
            LEFT JOIN pets p ON o.id = p.owner_id
            GROUP BY o.id, o.first_name, o.last_name, o.email, o.phone
            ORDER BY o.last_name, o.first_name
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("Owner ID", rs.getInt("id"));
                row.put("First Name", rs.getString("first_name"));
                row.put("Last Name", rs.getString("last_name"));
                row.put("Email", rs.getString("email"));
                row.put("Phone", rs.getString("phone"));
                row.put("Number of Pets", rs.getInt("pet_count"));
                row.put("Last Pet Added", rs.getTimestamp("last_pet_added"));
                reportData.add(row);
            }
        }

        return reportData;
    }

    public List<Map<String, Object>> generatePetReport() throws SQLException {
        List<Map<String, Object>> reportData = new ArrayList<>();

        String query = """
            SELECT 
                p.id,
                p.name,
                p.species,
                p.breed,
               p.birth_date,
                p.gender,
                CONCAT(o.first_name, ' ', o.last_name) as owner_name,
                COUNT(a.id) as appointment_count,
                MAX(a.appointment_date) as last_appointment
            FROM pets p
            JOIN owners o ON p.owner_id = o.id
            LEFT JOIN appointments a ON p.id = a.pet_id
            GROUP BY p.id, p.name, p.species, p.breed, p.date_of_birth, p.gender, o.first_name, o.last_name
            ORDER BY p.name
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("Pet ID", rs.getInt("id"));
                row.put("Name", rs.getString("name"));
                row.put("Species", rs.getString("species"));
                row.put("Breed", rs.getString("breed"));
                row.put("Date of Birth", rs.getDate("date_of_birth"));
                row.put("Gender", rs.getString("gender"));
                row.put("Owner", rs.getString("owner_name"));
                row.put("Total Appointments", rs.getInt("appointment_count"));
                row.put("Last Appointment", rs.getTimestamp("last_appointment"));
                reportData.add(row);
            }
        }

        return reportData;
    }

    public List<Map<String, Object>> generateAppointmentReport(LocalDateTime startDate, LocalDateTime endDate)
            throws SQLException {
        List<Map<String, Object>> reportData = new ArrayList<>();

        String query = """
            SELECT 
                a.id,
              a.date_time,
                a.reason,
                a.status,
                p.name as pet_name,
                p.species,
                CONCAT(o.first_name, ' ', o.last_name) as owner_name
            FROM appointments a
            JOIN pets p ON a.pet_id = p.id
            JOIN owners o ON p.owner_id = o.id
            WHERE a.date_time BETWEEN ? AND ?
            ORDER BY a.date_time
            """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("Appointment ID", rs.getInt("id"));
                    row.put("Date", rs.getTimestamp("appointment_date"));
                    row.put("Pet Name", rs.getString("pet_name"));
                    row.put("Species", rs.getString("species"));
                    row.put("Owner", rs.getString("owner_name"));
                    row.put("Reason", rs.getString("reason"));
                    row.put("Status", rs.getString("status"));
                    reportData.add(row);
                }
            }
        }

        return reportData;
    }

    public void exportReportToCsv(List<Map<String, Object>> reportData, String filePath) {
        if (reportData == null || reportData.isEmpty()) {
            return;
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write headers
            writer.write(String.join(",", reportData.get(0).keySet()));
            writer.write("\n");

            // Write data rows
            for (Map<String, Object> row : reportData) {
                List<String> values = row.values().stream()
                        .map(value -> {
                            if (value == null) {
                                return "";
                            }
                            String str = value.toString();
                            // Escape commas and quotes
                            if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
                                return "\"" + str.replace("\"", "\"\"") + "\"";
                            }
                            return str;
                        })
                        .toList();

                writer.write(String.join(",", values));
                writer.write("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to export report: " + e.getMessage());
        }
    }
}