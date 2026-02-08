package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import dao.UserDAO;
import model.User;
import app.Session;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Inventory POS - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());

        // Header Panel
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(new Color(41, 128, 185));
        pnlHeader.add(new JLabel("<html><h2 style='color:white;'>System Login</h2></html>"));
        add(pnlHeader, BorderLayout.NORTH);

        // Form Panel
        JPanel pnlForm = new JPanel(new GridLayout(3, 1, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        txtUser = new JTextField();
        txtUser.setBorder(BorderFactory.createTitledBorder("Username"));
        pnlForm.add(txtUser);

        txtPass = new JPasswordField();
        txtPass.setBorder(BorderFactory.createTitledBorder("Password"));
        pnlForm.add(txtPass);

        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.BLACK);
        pnlForm.add(btnLogin);

        add(pnlForm, BorderLayout.CENTER);

        this.getRootPane().setDefaultButton(btnLogin);

        // Mouse Button Action
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = txtUser.getText();
        String password = new String(txtPass.getPassword());

        UserDAO dao = new UserDAO();
        User user = dao.login(username, password);

        if (user != null) {
            Session.login(user);
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + user.getUsername());
            
            this.dispose(); 
            new AdminDashboardFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}