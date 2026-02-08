package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.SupplierDAO;
import model.Supplier;

// 1. Modal functionality ke liye JDialog use kiya hai
public class SupplierFrame extends JDialog {
    private JTextField txtName, txtContact, txtAddress;
    private JTable table;
    private DefaultTableModel tableModel;
    private SupplierDAO supplierDAO;

    // 2. Constructor mein JFrame owner (Dashboard) accept kiya hai
    public SupplierFrame(JFrame owner) {
        super(owner, "Supplier Management - Enterprise Edition", true); // Modal set to true
        
        supplierDAO = new SupplierDAO();
        setSize(800, 550);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // --- Top Panel: Responsive Form (GridBagLayout) ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder("Manage Suppliers"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        txtName = new JTextField();
        txtContact = new JTextField();
        txtAddress = new JTextField();

        // Row 0: Name
        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Supplier Name:"), gbc);
        gbc.gridx = 1; pnlForm.add(txtName, gbc);

        // Row 1: Contact
        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1; pnlForm.add(txtContact, gbc);

        // Row 2: Address
        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; pnlForm.add(txtAddress, gbc);

        // Row 3: Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save Supplier");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        
        JButton btnDelete = new JButton("Delete Selected");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);

        pnlButtons.add(btnDelete);
        pnlButtons.add(btnSave);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.NORTH);

        // --- Center Panel: Table ---
        String[] columns = {"ID", "Name", "Contact", "Address"};
      
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Listeners
        btnSave.addActionListener(e -> handleSaveSupplier());
        btnDelete.addActionListener(e -> handleDeleteSupplier());

        loadSupplierData();
    }

    private void loadSupplierData() {
        tableModel.setRowCount(0);
        List<Supplier> list = supplierDAO.getAllSuppliers();
        for (Supplier s : list) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getContact(), s.getAddress()});
        }
    }

    private void handleSaveSupplier() {
        String name = txtName.getText().trim();
        String contact = txtContact.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name aur Contact lazmi hain!");
            return;
        }

        if (supplierDAO.addSupplier(new Supplier(0, name, contact, address))) {
            JOptionPane.showMessageDialog(this, "Supplier Saved!");
            txtName.setText(""); txtContact.setText(""); txtAddress.setText("");
            loadSupplierData();
        }
    }

    private void handleDeleteSupplier() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Would you want to delete?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (supplierDAO.deleteSupplier(id)) {
                    JOptionPane.showMessageDialog(this, "Supplier Deleted!");
                    loadSupplierData();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please,first select from the table!");
        }
    }
}