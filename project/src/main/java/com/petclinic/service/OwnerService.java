package com.petclinic.service;

import com.petclinic.model.Owner;
import com.petclinic.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OwnerService {

    public List<Owner> getAllOwners() {
        List<Owner> owners = new ArrayList<>();
        String sql = "SELECT * FROM owners ORDER BY last_name, first_name";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Owner owner = extractOwnerFromResultSet(rs);
                owners.add(owner);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return owners;
    }
    
    public Owner getOwnerById(int id) {
        String sql = "SELECT * FROM owners WHERE id = ?";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractOwnerFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<Owner> searchOwners(String searchTerm) {
        List<Owner> owners = new ArrayList<>();
        String sql = "SELECT * FROM owners " +
                     "WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? " +
                     "OR LOWER(phone) LIKE ? OR LOWER(email) LIKE ? " +
                     "ORDER BY last_name, first_name";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Owner owner = extractOwnerFromResultSet(rs);
                    owners.add(owner);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return owners;
    }
    
    public void addOwner(Owner owner) {
        String sql = "INSERT INTO owners (first_name, last_name, phone, email, address) " +
                     "VALUES (?, ?, ?, ?, ?)";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, owner.getFirstName());
            pstmt.setString(2, owner.getLastName());
            pstmt.setString(3, owner.getPhone());
            pstmt.setString(4, owner.getEmail());
            pstmt.setString(5, owner.getAddress());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        owner.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateOwner(Owner owner) {
        String sql = "UPDATE owners SET first_name = ?, last_name = ?, phone = ?, email = ?, " +
                     "address = ? WHERE id = ?";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, owner.getFirstName());
            pstmt.setString(2, owner.getLastName());
            pstmt.setString(3, owner.getPhone());
            pstmt.setString(4, owner.getEmail());
            pstmt.setString(5, owner.getAddress());
            pstmt.setInt(6, owner.getId());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteOwner(int id) {

        String deletePetsSql = "DELETE FROM pets WHERE owner_id = ?";
        

        String deleteOwnerSql = "DELETE FROM owners WHERE id = ?";
                     
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            

            try (PreparedStatement pstmt = conn.prepareStatement(deletePetsSql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            

            try (PreparedStatement pstmt = conn.prepareStatement(deleteOwnerSql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private Owner extractOwnerFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        String address = rs.getString("address");
        
        return new Owner(id, firstName, lastName, phone, email, address);
    }
}