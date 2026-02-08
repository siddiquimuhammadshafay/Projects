package ui;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.Properties;

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
            props.setProperty("db.user", txtUser.getText());
            props.setProperty("db.pass", new String(txtPass.getPassword()));
            props.save(out, "Database Configuration");
            
            JOptionPane.showMessageDialog(this, "Settings Saved! Please restart the app.");
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}