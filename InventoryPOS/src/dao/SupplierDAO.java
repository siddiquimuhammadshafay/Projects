package dao;

import db.DBConnection;
import model.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    
    // 1. Add Supplier (return boolean for UI )
    public boolean addSupplier(Supplier s) {
        String query = "INSERT INTO suppliers (name, contact, address) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, s.getName());
            pst.setString(2, s.getContact());
            pst.setString(3, s.getAddress());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;
        }
    }

    // 2. Fetch All Suppliers
    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM suppliers")) {
            while (rs.next()) {
                list.add(new Supplier(
                    rs.getInt("id"), 
                    rs.getString("name"), 
                    rs.getString("contact"), 
                    rs.getString("address")
                ));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    // 3. DELETE Supplier Method (Jo missing tha)
    public boolean deleteSupplier(int id) {
        String query = "DELETE FROM suppliers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            // Agar ye supplier kisi purchase record mein use ho raha hai toh error aayega
            e.printStackTrace();
            return false;
        }
    }
}