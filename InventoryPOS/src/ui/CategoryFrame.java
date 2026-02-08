package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import dao.CategoryDAO;
import model.Category;

// 1. JFrame ki jagah JDialog use karein
public class CategoryFrame extends JDialog {
    private JTextField txtName;
    private JTable table;
    private DefaultTableModel model;
    private CategoryDAO categoryDAO;

    // 2. Constructor mein JFrame owner accept karein
    public CategoryFrame(JFrame owner) {
        // Modal set karne ke liye super constructor call karein
        super(owner, "Category Management", true); 
        
        categoryDAO = new CategoryDAO();
        setSize(650, 500);
        setLocationRelativeTo(owner); // Dashboard ke center mein khulega
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Fixed Responsive Layout ---
        JPanel pnlForm = new JPanel(new GridBagLayout()); 
        pnlForm.setBorder(BorderFactory.createTitledBorder("Manage Categories"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Category Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        txtName = new JTextField(15);
        pnlForm.add(txtName, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        JButton btnSave = new JButton("Save");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        pnlForm.add(btnSave, gbc);

        gbc.gridx = 3; gbc.gridy = 0;
        JButton btnDelete = new JButton("Delete Selected");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        pnlForm.add(btnDelete, gbc);

        add(pnlForm, BorderLayout.NORTH);

        // --- Center Panel: Table ---
        String[] cols = {"ID", "Category Name"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnSave.addActionListener(e -> handleSaveCategory());
        btnDelete.addActionListener(e -> handleDeleteCategory());

        loadCategoryData();
    }

    private void handleSaveCategory() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Category Name!");
            return;
        }
        if (categoryDAO.addCategory(name)) {
            JOptionPane.showMessageDialog(this, "Category Saved!");
            txtName.setText("");
            loadCategoryData();
        }
    }
private void handleDeleteCategory() {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please first select Category form the list!");
        return;
    }

    int id = (int) model.getValueAt(row, 0);
    String name = (String) model.getValueAt(row, 1);

    int confirm = JOptionPane.showConfirmDialog(this, "Would you seriously want to delete this '" + name + "' ?", "Confirm", JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        try {
          
            if (categoryDAO.deleteCategory(id)) {
                JOptionPane.showMessageDialog(this, "Category is successfully deleted !");
                loadCategoryData();
            }
        } catch (SQLException ex) {
          
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Constraint Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    private void loadCategoryData() {
        model.setRowCount(0);
        List<Category> list = categoryDAO.getAllCategories();
        for (Category c : list) {
            model.addRow(new Object[]{c.getId(), c.getName()});
        }
    }
}