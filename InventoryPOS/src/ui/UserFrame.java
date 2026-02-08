package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import dao.UserDAO;

// 1. JFrame ki jagah JDialog use kiya hai modal functionality ke liye
public class UserFrame extends JDialog {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> cmbRole;
    private JTable userTable;
    private DefaultTableModel model;
    private UserDAO userDAO = new UserDAO();

    // 2. Constructor mein JFrame owner accept kiya hai
    public UserFrame(JFrame owner) {
        super(owner, "User Management - Professional Edition", true); // Modal set to true
        
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // --- Top Panel: Responsive Form (GridBagLayout) ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder("User Operations"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        txtUser = new JTextField();
        txtPass = new JPasswordField();
        cmbRole = new JComboBox<>(new String[]{"Admin", "Cashier"});

        // Layout Mapping
        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; pnlForm.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Password (New/Update):"), gbc);
        gbc.gridx = 1; pnlForm.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; pnlForm.add(cmbRole, gbc);

        // Buttons Panel inside Form
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton btnAdd = new JButton("Add User");
        JButton btnUpdate = new JButton("Update Password");
        JButton btnDelete = new JButton("Delete User");

        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.NORTH);

        // --- Center Panel: Table ---
        String[] cols = {"ID", "Username", "Role"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(model);
        userTable.setRowHeight(30);
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        // --- Listeners ---
        btnAdd.addActionListener(e -> handleAddUser());
        btnUpdate.addActionListener(e -> handleUpdatePassword());
        btnDelete.addActionListener(e -> handleDeleteUser());
        
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userTable.getSelectedRow() != -1) {
                int row = userTable.getSelectedRow();
                txtUser.setText(model.getValueAt(row, 1).toString());
                cmbRole.setSelectedItem(model.getValueAt(row, 2).toString());
            }
        });

        loadUserData();
    }

    private void handleAddUser() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        String role = (String) cmbRole.getSelectedItem();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password must be entered!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.addUser(user, pass, role)) {
            JOptionPane.showMessageDialog(this, "User '" + user + "' added successfully!");
            loadUserData();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists!", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdatePassword() {
        String user = txtUser.getText().trim();
        String newPass = new String(txtPass.getPassword());

        if (user.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select user and enter new password!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.updatePassword(user, newPass)) {
            JOptionPane.showMessageDialog(this, "Password updated successfully for " + user + "!");
            clearFields();
        }
    }

    private void handleDeleteUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete!");
            return;
        }

        String user = model.getValueAt(row, 1).toString();
        if (user.equals("admin")) {
            JOptionPane.showMessageDialog(this, "Main Admin cannot be deleted!", "Action Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete '" + user + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(user)) {
                JOptionPane.showMessageDialog(this, "User '" + user + "' deleted!");
                loadUserData();
                clearFields();
            }
        }
    }

    private void clearFields() {
        txtUser.setText("");
        txtPass.setText("");
        userTable.clearSelection();
    }

    private void loadUserData() {
        model.setRowCount(0);
        for (Object[] row : userDAO.getAllUsers()) {
            model.addRow(row);
        }
    }
}