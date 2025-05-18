package com.petclinic.service;

import com.petclinic.model.MedicalRecord;
import com.petclinic.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordService {

    public List<MedicalRecord> getAllMedicalRecords() {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT m.*, p.name AS pet_name " +
                "FROM medical_records m " +
                "JOIN pets p ON m.pet_id = p.id " +
                "ORDER BY m.date_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MedicalRecord record = extractMedicalRecordFromResultSet(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    public List<MedicalRecord> getMedicalRecordsForPet(int petId) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT m.*, p.name AS pet_name " +
                "FROM medical_records m " +
                "JOIN pets p ON m.pet_id = p.id " +
                "WHERE m.pet_id = ? " +
                "ORDER BY m.date_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, petId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MedicalRecord record = extractMedicalRecordFromResultSet(rs);
                    records.add(record);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    public List<MedicalRecord> searchMedicalRecords(String searchTerm) {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT m.*, p.name AS pet_name " +
                "FROM medical_records m " +
                "JOIN pets p ON m.pet_id = p.id " +
                "WHERE LOWER(p.name) LIKE ? OR LOWER(m.type) LIKE ? " +
                "OR LOWER(m.description) LIKE ? OR LOWER(m.treatment) LIKE ? " +
                "ORDER BY m.date_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MedicalRecord record = extractMedicalRecordFromResultSet(rs);
                    records.add(record);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }
    public void addMedicalRecord(MedicalRecord record) {
        String sql = "INSERT INTO medical_records (pet_id, appointment_id, date_time, type, description, treatment, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, record.getPetId());

            // تعويض هذه:
            // pstmt.setInt(2, record.getAppointmentId());

            // بهاذ السطر:
            pstmt.setNull(2, java.sql.Types.INTEGER);

            pstmt.setTimestamp(3, Timestamp.valueOf(record.getDateTime()));
            pstmt.setString(4, record.getType());
            pstmt.setString(5, record.getDescription());
            pstmt.setString(6, record.getTreatment());
            pstmt.setString(7, record.getNotes());

            int affectedRows = pstmt.executeUpdate();

            System.out.println("✅ Rows inserted: " + affectedRows); // سطر للمراقبة

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateMedicalRecord(MedicalRecord record) {
        String sql = "UPDATE medical_records SET pet_id = ?, appointment_id = ?, date_time = ?, " +
                "type = ?, description = ?, treatment = ?, notes = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, record.getPetId());
            pstmt.setInt(2, record.getAppointmentId());
            pstmt.setTimestamp(3, Timestamp.valueOf(record.getDateTime()));
            pstmt.setString(4, record.getType());
            pstmt.setString(5, record.getDescription());
            pstmt.setString(6, record.getTreatment());
            pstmt.setString(7, record.getNotes());
            pstmt.setInt(8, record.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMedicalRecord(int id) {
        String sql = "DELETE FROM medical_records WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private MedicalRecord extractMedicalRecordFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int petId = rs.getInt("pet_id");
        String petName = rs.getString("pet_name");
        int appointmentId = rs.getInt("appointment_id");
        LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
        String type = rs.getString("type");
        String description = rs.getString("description");
        String treatment = rs.getString("treatment");
        String notes = rs.getString("notes");

        return new MedicalRecord(id, petId, petName, appointmentId, dateTime, type, description, treatment, notes);
    }
}