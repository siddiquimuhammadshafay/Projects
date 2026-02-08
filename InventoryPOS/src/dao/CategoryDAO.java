package dao;

import db.DBConnection;
import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    
    
    public boolean addCategory(String name) {
        // Validation: Check karein ke name pehle se toh nahi
        String query = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, name);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                System.err.println("Category already exists: " + name);
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String query = "SELECT * FROM categories ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }

  
    public boolean deleteCategory(int id) throws SQLException {
    
        String checkQuery = "SELECT COUNT(*) FROM products WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstCheck = conn.prepareStatement(checkQuery)) {
            
            pstCheck.setInt(1, id);
            ResultSet rs = pstCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {

                throw new SQLException("Is category mein products maujood hain. Pehle unhe delete ya move karein!");
            }
        }

       
        String deleteQuery = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstDelete = conn.prepareStatement(deleteQuery)) {
            pstDelete.setInt(1, id);
            return pstDelete.executeUpdate() > 0;
        }
    }

   
    public boolean updateCategory(int id, String newName) {
        String query = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, newName);
            pst.setInt(2, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}