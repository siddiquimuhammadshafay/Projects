package dao;

import db.DBConnection;
import model.PurchaseItem;
import java.sql.*;
import java.util.List;

public class PurchaseDAO {
    public boolean processPurchase(int supplierId, double total, List<PurchaseItem> items) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String purSql = "INSERT INTO purchases (supplier_id, total_amount) VALUES (?, ?)";
            PreparedStatement pstPur = conn.prepareStatement(purSql, Statement.RETURN_GENERATED_KEYS);
            pstPur.setInt(1, supplierId);
            pstPur.setDouble(2, total);
            pstPur.executeUpdate();

            ResultSet rs = pstPur.getGeneratedKeys();
            if (rs.next()) {
                int purId = rs.getInt(1);
                for (PurchaseItem item : items) {

                    String itemSql = "INSERT INTO purchase_items (purchase_id, product_id, quantity, cost_price) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstItem = conn.prepareStatement(itemSql);
                    pstItem.setInt(1, purId);
                    pstItem.setInt(2, item.getProductId());
                    pstItem.setInt(3, item.getQuantity());
                    pstItem.setDouble(4, item.getCostPrice());
                    pstItem.executeUpdate();

                    String stockSql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?";
                    PreparedStatement pstStock = conn.prepareStatement(stockSql);
                    pstStock.setInt(1, item.getQuantity());
                    pstStock.setInt(2, item.getProductId());
                    pstStock.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
}