package dao;

import db.DBConnection;
import model.Sale;
import model.SaleItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public List<Sale> getFilteredSales(String startDate, String endDate) {
        List<Sale> list = new ArrayList<>();

        String query = "SELECT s.id, s.sale_date, s.total_amount, u.username, " +
                "GROUP_CONCAT(si.product_name SEPARATOR ', ') as items " +
                "FROM sales s " +
                "JOIN users u ON s.user_id = u.id " +
                "JOIN sale_items si ON s.id = si.sale_id " +
                "WHERE DATE(s.sale_date) BETWEEN ? AND ? " +
                "GROUP BY s.id";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, startDate);
            pst.setString(2, endDate);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Sale s = new Sale();
                s.setId(rs.getInt("id"));
                s.setSaleDate(rs.getTimestamp("sale_date"));
                s.setTotalAmount(rs.getDouble("total_amount"));
                s.setUserName(rs.getString("username")); // Fixed
                s.setProductsSummary(rs.getString("items")); // Fixed
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Sale> getAllSales() {
        return getFilteredSales("2000-01-01", "2099-12-31");
    }

    public boolean processSale(double totalAmount, int userId, List<SaleItem> items) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Transaction management

            String saleSQL = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";
            PreparedStatement pstSale = conn.prepareStatement(saleSQL, Statement.RETURN_GENERATED_KEYS);
            pstSale.setDouble(1, totalAmount);
            pstSale.setInt(2, userId);
            pstSale.executeUpdate();

            ResultSet rs = pstSale.getGeneratedKeys();
            int saleId = 0;
            if (rs.next()) {
                saleId = rs.getInt(1);
            }

            String itemSQL = "INSERT INTO sale_items (sale_id, product_id, product_name, quantity, price) VALUES (?, ?, ?, ?, ?)";
            String stockSQL = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?";

            PreparedStatement pstItem = conn.prepareStatement(itemSQL);
            PreparedStatement pstStock = conn.prepareStatement(stockSQL);

            for (SaleItem item : items) {

                pstItem.setInt(1, saleId);
                pstItem.setInt(2, item.getProductId());
                pstItem.setString(3, "Product-" + item.getProductId());
                pstItem.setInt(4, item.getQuantity());
                pstItem.setDouble(5, item.getPrice()); // Error fixed here
                pstItem.addBatch();

                // Stock update
                pstStock.setInt(1, item.getQuantity());
                pstStock.setInt(2, item.getProductId());
                pstStock.addBatch();
            }

            pstItem.executeBatch();
            pstStock.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}