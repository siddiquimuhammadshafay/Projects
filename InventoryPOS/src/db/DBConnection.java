package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    // Database name constant taake spelling ki galti na ho
    private static final String DB_NAME = "inventory_db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 

            Properties props = new Properties();
            // Project root se file uthane ke liye simple path
            try (FileInputStream in = new FileInputStream("db_config.properties")) {
                props.load(in); 
            } catch (IOException e) {
                throw new SQLException("Database config file (db_config.properties) nahi mili!");
            }

            String user = props.getProperty("db.user", "root");
            String pass = props.getProperty("db.pass", "");
            
            // Fix: Pehle sirf server se connect karein, phir database use karein
            String url = "jdbc:mysql://localhost:3306/" + DB_NAME + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";

            return DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver missing! Please,Check connector from lib folder.");
        }
    }
}