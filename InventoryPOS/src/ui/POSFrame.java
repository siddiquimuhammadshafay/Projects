package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import dao.ProductDAO;
import dao.SaleDAO;
import model.Product;
import model.SaleItem;
import java.awt.print.*;
import app.Session;
import java.text.SimpleDateFormat;
import java.util.Date;

public class POSFrame extends JDialog {
    private JComboBox<Product> comboProducts; 
    private JTextField txtQty;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotal, lblTime;
    private double totalAmount = 0.0;
    private List<SaleItem> cartItems = new ArrayList<>();

    public POSFrame(JFrame owner) {
        super(owner, "Enterprise POS - Billing Terminal", true);
        
        setSize(1100, 750);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(15, 15));

        // --- 1. Header: Modern Billing Section ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(41, 128, 185));
        pnlHeader.setPreferredSize(new Dimension(0, 60));
        
        JLabel lblHeader = new JLabel("  🛒 POS SYSTEM - CHECKOUT");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pnlHeader.add(lblHeader, BorderLayout.WEST);

        lblTime = new JLabel();
        lblTime.setForeground(Color.WHITE);
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTime.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        pnlHeader.add(lblTime, BorderLayout.EAST);
        startClock(); 
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. Left Panel: Product Selection ---
        JPanel pnlInput = new JPanel(new GridLayout(8, 1, 10, 10));
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Sales Console"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        pnlInput.setPreferredSize(new Dimension(320, 0));

        comboProducts = new JComboBox<>();
        loadProductList(); 
        comboProducts.setBorder(BorderFactory.createTitledBorder("Select Product"));
        
        txtQty = new JTextField("1");
        txtQty.setFont(new Font("Arial", Font.BOLD, 16));
        txtQty.setBorder(BorderFactory.createTitledBorder("Quantity"));

        JButton btnAdd = new JButton("ADD TO CART");
        btnAdd.setBackground(new Color(52, 152, 219));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));

        pnlInput.add(comboProducts);
        pnlInput.add(txtQty);
        pnlInput.add(btnAdd);
        add(pnlInput, BorderLayout.WEST);

        // --- 3. Center: Cart Table ---
        String[] columns = { "ID", "Product Name", "Qty", "Price", "Subtotal" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(35);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // --- 4. Bottom: Billing & Controls ---
        JPanel pnlBottom = new JPanel(new BorderLayout(15, 15));
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblTotal = new JLabel("Total Payable: Rs. 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 35));
        lblTotal.setForeground(new Color(192, 57, 43));

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        
        // --- UPDATED: Button Name changed to DELETE FROM CART ---
        JButton btnRemove = new JButton("DELETE FROM CART");
        btnRemove.setBackground(new Color(231, 76, 60));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setPreferredSize(new Dimension(180, 50)); 

        JButton btnCheckout = new JButton("COMPLETE SALE & PRINT");
        btnCheckout.setPreferredSize(new Dimension(300, 60));
        btnCheckout.setBackground(new Color(39, 174, 96));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 18));

        pnlButtons.add(btnRemove);
        pnlButtons.add(btnCheckout);

        pnlBottom.add(lblTotal, BorderLayout.WEST);
        pnlBottom.add(pnlButtons, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);

        // Listeners
        btnAdd.addActionListener(e -> addToCart());
        btnRemove.addActionListener(e -> removeFromCart());
        btnCheckout.addActionListener(e -> handleCheckout());
    }

    private void loadProductList() {
        ProductDAO pdao = new ProductDAO();
        List<Product> products = pdao.getAllProducts();
        comboProducts.removeAllItems();
        for (Product p : products) {
            comboProducts.addItem(p); 
        }
    }

    private void startClock() {
        Timer t = new Timer(1000, e -> {
            lblTime.setText(new SimpleDateFormat("hh:mm:ss a").format(new Date()));
        });
        t.start();
    }

    private void addToCart() {
        Product selected = (Product) comboProducts.getSelectedItem();
        String qtyStr = txtQty.getText().trim();

        if (selected == null || qtyStr.isEmpty()) return;

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) throw new NumberFormatException();

            if (selected.getStockQuantity() < qty) {
                JOptionPane.showMessageDialog(this, "Limited Stock! Only " + selected.getStockQuantity() + " left.");
                return;
            }

            double subtotal = selected.getPrice() * qty;
            tableModel.addRow(new Object[] { 
                selected.getId(), selected.getName(), qty, selected.getPrice(), subtotal 
            });
            
            cartItems.add(new SaleItem(0, 0, selected.getId(), qty, selected.getPrice()));

            totalAmount += subtotal;
            lblTotal.setText("Total Payable: Rs. " + String.format("%.2f", totalAmount));
            txtQty.setText("1");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid positive quantity!");
        }
    }

    private void handleCheckout() {
        if (cartItems.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Confirm Sale: Rs. " + totalAmount, "Payment", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SaleDAO sdao = new SaleDAO();
            int userId = (Session.getCurrentUser() != null) ? Session.getCurrentUser().getId() : 1;

            if (sdao.processSale(totalAmount, userId, cartItems)) {
                printReceipt();
                clearCart();
                loadProductList(); 
            }
        }
    }

    private void printReceipt() {
        List<Object[]> data = new ArrayList<>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
        data.add(new Object[]{
            tableModel.getValueAt(i, 1), // Product Name
            tableModel.getValueAt(i, 2), // Qty
            tableModel.getValueAt(i, 3), // Price
            tableModel.getValueAt(i, 4)  // Subtotal (Index 4)
        });
    }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new ui.ReceiptPrinter(data, totalAmount)); 

        if (job.printDialog()) {
            try { job.print(); } catch (PrinterException ex) { ex.printStackTrace(); }
        }
    }

    // --- UPDATED: Method with Validation ---
    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        
        // Check karein ke row select hui hai ya nahi
        if (row != -1) {
            totalAmount -= (double) tableModel.getValueAt(row, 4);
            lblTotal.setText("Total Payable: Rs. " + String.format("%.2f", totalAmount));
            cartItems.remove(row);
            tableModel.removeRow(row);
        } else {
            // Agar select nahi ki, toh ye warning dikhaye
            JOptionPane.showMessageDialog(this, 
                "Please first select the item from the cart list!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearCart() {
        tableModel.setRowCount(0);
        cartItems.clear();
        totalAmount = 0;
        lblTotal.setText("Total Payable: Rs. 0.00");
    }
}