package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import dao.ProductDAO;
import dao.CategoryDAO;
import model.Product;
import model.Category;

public class ProductFrame extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtPrice, txtStock;
    private JComboBox<String> cmbCategory;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    private int currentPage = 1;
    private final int pageSize = 50; 
    private JLabel lblPageInfo;

    public ProductFrame(JFrame owner) {
        super(owner, "Product Management - Professional Edition", true);
        
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
        
        setSize(1000, 700);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // --- Left Panel: Input Form ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Product Details"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        pnlForm.setPreferredSize(new Dimension(320, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        txtName = new JTextField();
        txtPrice = new JTextField();
        txtStock = new JTextField();
        cmbCategory = new JComboBox<>();
        loadCategories();

        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Product Name:"), gbc);
        gbc.gridy = 1; pnlForm.add(txtName, gbc);
        gbc.gridy = 2; pnlForm.add(new JLabel("Category:"), gbc);
        gbc.gridy = 3; pnlForm.add(cmbCategory, gbc);
        gbc.gridy = 4; pnlForm.add(new JLabel("Price (Rs.):"), gbc);
        gbc.gridy = 5; pnlForm.add(txtPrice, gbc);
        gbc.gridy = 6; pnlForm.add(new JLabel("Opening Stock:"), gbc);
        gbc.gridy = 7; pnlForm.add(txtStock, gbc);

        JButton btnAdd = new JButton("SAVE PRODUCT");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        
        JButton btnClear = new JButton("Clear Form");

        gbc.gridy = 8; gbc.insets = new Insets(20, 5, 5, 5);
        pnlForm.add(btnAdd, gbc);
        gbc.gridy = 9; gbc.insets = new Insets(5, 5, 5, 5);
        pnlForm.add(btnClear, gbc);

        add(pnlForm, BorderLayout.WEST);

        // --- Center Panel: Table ---
        String[] columns = {"ID", "Product Name", "Category ID", "Price", "Current Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Bottom Panel: Pagination ---
        JPanel pnlPagination = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        JButton btnPrev = new JButton("<< Previous");
        JButton btnNext = new JButton("Next >>");
        lblPageInfo = new JLabel("Page: " + currentPage);
        pnlPagination.add(btnPrev);
        pnlPagination.add(lblPageInfo);
        pnlPagination.add(btnNext);
        add(pnlPagination, BorderLayout.SOUTH);

       
        ActionListener saveAction = e -> handleAddProduct(); // Save logic reuse

        btnAdd.addActionListener(saveAction);
        txtName.addActionListener(saveAction);  // Field 1 Enter support
        txtPrice.addActionListener(saveAction); // Field 2 Enter support
        txtStock.addActionListener(saveAction); // Field 3 Enter support

        btnClear.addActionListener(e -> clearFields());
        btnPrev.addActionListener(e -> { if (currentPage > 1) { currentPage--; loadProductData(); } });
        btnNext.addActionListener(e -> { currentPage++; loadProductData(); });

        loadProductData();
    }

    private void loadCategories() {
        cmbCategory.removeAllItems();
        List<Category> cats = categoryDAO.getAllCategories();
        if (cats.isEmpty()) { cmbCategory.addItem("No Categories Found"); }
        else { for (Category c : cats) { cmbCategory.addItem(c.getId() + " - " + c.getName()); } }
    }

    private void loadProductData() {
        tableModel.setRowCount(0);
        List<Product> products = productDAO.getProductsByPage(currentPage, pageSize);
        if (products.isEmpty() && currentPage > 1) {
            currentPage--; 
            JOptionPane.showMessageDialog(this, "There is no record!");
            return;
        }
        for (Product p : products) {
            tableModel.addRow(new Object[]{ p.getId(), p.getName(), p.getCategoryId(), "Rs. " + p.getPrice(), p.getStockQuantity() });
        }
        lblPageInfo.setText("Page: " + currentPage);
    }

    private void handleAddProduct() {
        String name = txtName.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String stockStr = txtStock.getText().trim();
        String selectedCat = (String) cmbCategory.getSelectedItem();

        // Validation Check (Enter press par bhi check hoga)
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || selectedCat == null || selectedCat.equals("No Categories Found")) {
            JOptionPane.showMessageDialog(this, "Please, Fill all the Fileds", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);
            if (price <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Price and Stock must be non negative!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int catId = Integer.parseInt(selectedCat.split(" - ")[0]);
            if (productDAO.addProduct(new Product(0, name, catId, price, stock))) {
                JOptionPane.showMessageDialog(this, "Product Saved!");
                currentPage = 1; loadProductData(); clearFields();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Format! Please, Enter correct numbers in Price and Stock!.");
        }
    }

    private void clearFields() {
        txtName.setText(""); txtPrice.setText(""); txtStock.setText("");
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
    }
}