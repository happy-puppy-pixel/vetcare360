package com.petclinic.service;

import com.petclinic.model.Appointment;
import com.petclinic.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS pet_name, o.first_name || ' ' || o.last_name AS owner_name " +
                "FROM appointments a " +
                "JOIN pets p ON a.pet_id = p.id " +
                "JOIN owners o ON p.owner_id = o.id " +
                "ORDER BY a.date_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Appointment appointment = extractAppointmentFromResultSet(rs);
                appointments.add(appointment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public Appointment getAppointmentById(int id) {
        String sql = "SELECT a.*, p.name AS pet_name, o.first_name || ' ' || o.last_name AS owner_name " +
                "FROM appointments a " +
                "JOIN pets p ON a.pet_id = p.id " +
                "JOIN owners o ON p.owner_id = o.id " +
                "WHERE a.id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAppointmentFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Appointment> getAppointmentsForPet(int petId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS pet_name, o.first_name || ' ' || o.last_name AS owner_name " +
                "FROM appointments a " +
                "JOIN pets p ON a.pet_id = p.id " +
                "JOIN owners o ON p.owner_id = o.id " +
                "WHERE a.pet_id = ? " +
                "ORDER BY a.date_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, petId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(rs);
                    appointments.add(appointment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public List<Appointment> getAppointmentsForOwner(int ownerId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS pet_name, o.first_name || ' ' || o.last_name AS owner_name " +
                "FROM appointments a " +
                "JOIN pets p ON a.pet_id = p.id " +
                "JOIN owners o ON p.owner_id = o.id " +
                "WHERE o.id = ? " +
                "ORDER BY a.date_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(rs);
                    appointments.add(appointment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public List<Appointment> getAppointmentsForDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS pet_name, o.first_name || ' ' || o.last_name AS owner_name " +
                "FROM appointments a " +
                "JOIN pets p ON a.pet_id = p.id " +
                "JOIN owners o ON p.owner_id = o.id " +
                "WHERE DATE(a.date_time) = ? " +
                "ORDER BY a.date_time";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(rs);
                    appointments.add(appointment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public List<Appointment> getUpcomingAppointments(int limit) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS pet_name, o.first_name || ' ' || o.last_name AS owner_name " +
                "FROM appointments a " +
                "JOIN pets p ON a.pet_id = p.id " +
                "JOIN owners o ON p.owner_id = o.id " +
                "WHERE a.date_time >= CURRENT_TIMESTAMP AND a.status = 'Scheduled' " +
                "ORDER BY a.date_time " +
                "LIMIT ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(rs);
                    appointments.add(appointment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public List<Appointment> getPendingAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS pet_name, o.first_name || ' ' || o.last_name AS owner_name " +
                "FROM appointments a " +
                "JOIN pets p ON a.pet_id = p.id " +
                "JOIN owners o ON p.owner_id = o.id " +
                "WHERE a.status = 'Scheduled' " +
                "ORDER BY a.date_time";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Appointment appointment = extractAppointmentFromResultSet(rs);
                appointments.add(appointment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public void addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (pet_id, date_time, reason, diagnosis, treatment, notes, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, appointment.getPetId());
            pstmt.setTimestamp(2, Timestamp.valueOf(appointment.getDateTime()));
            pstmt.setString(3, appointment.getReason());
            pstmt.setString(4, appointment.getDiagnosis());
            pstmt.setString(5, appointment.getTreatment());
            pstmt.setString(6, appointment.getNotes());
            pstmt.setString(7, appointment.getStatus());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET pet_id = ?, date_time = ?, reason = ?, " +
                "diagnosis = ?, treatment = ?, notes = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointment.getPetId());
            pstmt.setTimestamp(2, Timestamp.valueOf(appointment.getDateTime()));
            pstmt.setString(3, appointment.getReason());
            pstmt.setString(4, appointment.getDiagnosis());
            pstmt.setString(5, appointment.getTreatment());
            pstmt.setString(6, appointment.getNotes());
            pstmt.setString(7, appointment.getStatus());
            pstmt.setInt(8, appointment.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAppointment(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int petId = rs.getInt("pet_id");
        String petName = rs.getString("pet_name");
        int ownerId = 0; // We'll get this from the owner name
        String ownerName = rs.getString("owner_name");
        LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
        String reason = rs.getString("reason");
        String diagnosis = rs.getString("diagnosis");
        String treatment = rs.getString("treatment");
        String notes = rs.getString("notes");
        String status = rs.getString("status");

        return new Appointment(id, petId, petName, ownerId, ownerName, dateTime, reason, diagnosis, treatment, notes, status);
    }
}