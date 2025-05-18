package com.petclinic.service;

import com.petclinic.model.Veterinarian;
import com.petclinic.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeterinarianService {

    public List<Veterinarian> getAllVeterinarians() {
        List<Veterinarian> list = new ArrayList<>();
        String sql = "SELECT * FROM veterinarians";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addVeterinarian(Veterinarian vet) {
        String sql = "INSERT INTO veterinarians "
                + "(first_name,last_name,specialization,phone,email,schedule,is_available,notes) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, vet.getFirstName());
            p.setString(2, vet.getLastName());
            p.setString(3, vet.getSpecialization());
            p.setString(4, vet.getPhone());
            p.setString(5, vet.getEmail());
            p.setString(6, vet.getSchedule());
            p.setBoolean(7, vet.isAvailable());
            p.setString(8, vet.getNotes());
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) vet.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateVeterinarian(Veterinarian vet) {
        String sql = "UPDATE veterinarians SET first_name=?,last_name=?,specialization=?,"
                + "phone=?,email=?,schedule=?,is_available=?,notes=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, vet.getFirstName());
            p.setString(2, vet.getLastName());
            p.setString(3, vet.getSpecialization());
            p.setString(4, vet.getPhone());
            p.setString(5, vet.getEmail());
            p.setString(6, vet.getSchedule());
            p.setBoolean(7, vet.isAvailable());
            p.setString(8, vet.getNotes());
            p.setInt(9, vet.getId());
            p.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVeterinarian(int id) {
        String sql = "DELETE FROM veterinarians WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, id);
            p.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Veterinarian> searchVeterinarians(String term) {
        List<Veterinarian> list = new ArrayList<>();
        String sql = "SELECT * FROM veterinarians WHERE "
                + "LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? OR LOWER(specialization) LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            String t = "%" + term.toLowerCase() + "%";
            p.setString(1, t);
            p.setString(2, t);
            p.setString(3, t);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Veterinarian mapRow(ResultSet rs) throws SQLException {
        return new Veterinarian(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("specialization"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("schedule"),
                rs.getBoolean("is_available"),
                rs.getString("notes")
        );
    }
}
