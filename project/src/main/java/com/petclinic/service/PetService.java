package com.petclinic.service;

import com.petclinic.model.Pet;
import com.petclinic.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PetService {

    public List<Pet> getAllPets() {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT p.*, o.first_name || ' ' || o.last_name AS owner_name " +
                     "FROM pets p JOIN owners o ON p.owner_id = o.id " +
                     "ORDER BY p.name";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Pet pet = extractPetFromResultSet(rs);
                pets.add(pet);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return pets;
    }
    
    public Pet getPetById(int id) {
        String sql = "SELECT p.*, o.first_name || ' ' || o.last_name AS owner_name " +
                     "FROM pets p JOIN owners o ON p.owner_id = o.id " +
                     "WHERE p.id = ?";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPetFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<Pet> getPetsByOwnerId(int ownerId) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT p.*, o.first_name || ' ' || o.last_name AS owner_name " +
                     "FROM pets p JOIN owners o ON p.owner_id = o.id " +
                     "WHERE p.owner_id = ? " +
                     "ORDER BY p.name";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ownerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Pet pet = extractPetFromResultSet(rs);
                    pets.add(pet);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return pets;
    }
    
    public List<Pet> searchPets(String searchTerm) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT p.*, o.first_name || ' ' || o.last_name AS owner_name " +
                     "FROM pets p JOIN owners o ON p.owner_id = o.id " +
                     "WHERE LOWER(p.name) LIKE ? OR LOWER(p.species) LIKE ? OR LOWER(p.breed) LIKE ? " +
                     "OR LOWER(o.first_name || ' ' || o.last_name) LIKE ? " +
                     "ORDER BY p.name";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Pet pet = extractPetFromResultSet(rs);
                    pets.add(pet);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return pets;
    }
    
    public void addPet(Pet pet) {
        String sql = "INSERT INTO pets (name, species, breed, birth_date, owner_id, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, pet.getName());
            pstmt.setString(2, pet.getSpecies());
            pstmt.setString(3, pet.getBreed());
            pstmt.setDate(4, Date.valueOf(pet.getBirthDate()));
            pstmt.setInt(5, pet.getOwnerId());
            pstmt.setString(6, pet.getNotes());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pet.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updatePet(Pet pet) {
        String sql = "UPDATE pets SET name = ?, species = ?, breed = ?, birth_date = ?, " +
                     "owner_id = ?, notes = ? WHERE id = ?";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pet.getName());
            pstmt.setString(2, pet.getSpecies());
            pstmt.setString(3, pet.getBreed());
            pstmt.setDate(4, Date.valueOf(pet.getBirthDate()));
            pstmt.setInt(5, pet.getOwnerId());
            pstmt.setString(6, pet.getNotes());
            pstmt.setInt(7, pet.getId());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deletePet(int id) {
        String sql = "DELETE FROM pets WHERE id = ?";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public int getTotalPets() {
        String sql = "SELECT COUNT(*) FROM pets";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    private Pet extractPetFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String species = rs.getString("species");
        String breed = rs.getString("breed");
        LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
        int ownerId = rs.getInt("owner_id");
        String ownerName = rs.getString("owner_name");
        String notes = rs.getString("notes");
        
        return new Pet(id, name, species, breed, birthDate, ownerId, ownerName, notes);
    }
}