package ui;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.Properties;
import db.DBSetup;

public class DbConfigFrame extends JFrame {
    private JPasswordField txtPass;
    private JTextField txtUser;

    public DbConfigFrame() {
        setTitle("Database Setup");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel(" MySQL Username:"));
        txtUser = new JTextField("root");
        add(txtUser);

        add(new JLabel(" MySQL Password:"));
        txtPass = new JPasswordField();
        add(txtPass);

        JButton btnSave = new JButton("Save & Connect");
        btnSave.addActionListener(e -> saveConfig());
        add(new JLabel(""));
        add(btnSave);
    }

    private void saveConfig() {
        try (FileOutputStream out = new FileOutputStream("db_config.properties")) {
            Properties props = new Properties();
            props.setProperty("db.user", txtUser.getText().trim());
            props.setProperty("db.pass", new String(txtPass.getPassword()));
            props.store(out, "Database Configuration");
            
            // Auto-Initialize tables
            DBSetup.init(); 
            
            JOptionPane.showMessageDialog(this, "Settings Saved & Database Ready!");
            this.dispose();
            new LoginFrame().setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage() + "\nTry running as Administrator.");
            ex.printStackTrace();
        }
    }
}