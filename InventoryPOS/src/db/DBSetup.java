package db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class DBSetup {
    private static final String URL = "jdbc:mysql://localhost:3306/?useSSL=false";
    private static final String DB_NAME = "inventory_db";

    public static void init() throws Exception {
        Properties props = new Properties();
        
        try (FileInputStream in = new FileInputStream("db_config.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new Exception("Setup fail: Config file missing!");
        }

        String user = props.getProperty("db.user", "root");
        String pass = props.getProperty("db.pass", "");

        // Connection string mein database name nahi diya taake pehle server se connect ho
        try (Connection conn = DriverManager.getConnection(URL, user, pass);
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.executeUpdate("USE " + DB_NAME);

            // Tables creation logic (Aapka logic bilkul sahi tha)
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS categories (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL)");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) UNIQUE NOT NULL, password_hash VARCHAR(255) NOT NULL, role VARCHAR(20) DEFAULT 'cashier')");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, category_id INT, price DECIMAL(10, 2) NOT NULL, stock_quantity INT DEFAULT 0, FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS suppliers (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, contact VARCHAR(50), address TEXT)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sales (id INT AUTO_INCREMENT PRIMARY KEY, sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, total_amount DECIMAL(10, 2) NOT NULL, user_id INT, FOREIGN KEY (user_id) REFERENCES users(id))");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sale_items (id INT AUTO_INCREMENT PRIMARY KEY, sale_id INT, product_id INT, product_name VARCHAR(100), quantity INT NOT NULL, price DECIMAL(10, 2) NOT NULL, FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE, FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL)");

            // Optimization: Try-Catch blocks for Indexes (Taake agar index pehle se hai toh crash na ho)
            try { stmt.executeUpdate("CREATE INDEX idx_product_name ON products(name)"); } catch (Exception e) {}
            try { stmt.executeUpdate("CREATE INDEX idx_sale_date ON sales(sale_date)"); } catch (Exception e) {}
            try { stmt.executeUpdate("CREATE INDEX idx_item_sale_id ON sale_items(sale_id)"); } catch (Exception e) {}

            stmt.executeUpdate("INSERT IGNORE INTO users (username, password_hash, role) VALUES ('admin', 'admin123', 'Admin')");

           
        } catch (Exception e) {
            throw new Exception("Database Connection Failed: " + e.getMessage());
        }
    }
}