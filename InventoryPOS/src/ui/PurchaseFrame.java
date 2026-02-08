package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import dao.ProductDAO;
import dao.SupplierDAO;
import dao.PurchaseDAO;
import model.PurchaseItem;



public class PurchaseFrame extends JDialog {
    private JComboBox<String> cmbSupplier, cmbProduct;
    private JTextField txtQty, txtCost;
    private JTable purchaseTable;
    private DefaultTableModel tableModel;
    private List<PurchaseItem> itemList = new ArrayList<>();
    private JLabel lblTotalDisplay;
    private double grandTotal = 0.0;

   
    public PurchaseFrame(JFrame owner) {
        super(owner, "Purchase Management (Stock In)", true); 
        
        setSize(900, 650);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // --- Top Panel: Selection (Responsive GridBagLayout) ---
        JPanel pnlTop = new JPanel(new GridBagLayout());
        pnlTop.setBorder(BorderFactory.createTitledBorder("Purchase Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        cmbSupplier = new JComboBox<>();
        cmbProduct = new JComboBox<>();
        txtQty = new JTextField();
        txtCost = new JTextField();

        loadSuppliers();
        loadProducts();

        // Row 1: Supplier
        gbc.gridx = 0; gbc.gridy = 0; pnlTop.add(new JLabel("Select Supplier:"), gbc);
        gbc.gridx = 1; pnlTop.add(cmbSupplier, gbc);

        // Row 2: Product
        gbc.gridx = 0; gbc.gridy = 1; pnlTop.add(new JLabel("Select Product:"), gbc);
        gbc.gridx = 1; pnlTop.add(cmbProduct, gbc);

        // Row 3: Qty & Cost
        gbc.gridx = 0; gbc.gridy = 2; pnlTop.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; pnlTop.add(txtQty, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; pnlTop.add(new JLabel("Cost Price (per unit):"), gbc);
        gbc.gridx = 1; pnlTop.add(txtCost, gbc);

        add(pnlTop, BorderLayout.NORTH);

        // --- Center: Professional Table ---
        String[] cols = {"Product ID", "Product Name", "Qty", "Cost Price", "Subtotal"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        purchaseTable = new JTable(tableModel);
        purchaseTable.setRowHeight(30);
        add(new JScrollPane(purchaseTable), BorderLayout.CENTER);

        // --- Bottom: Buttons & Total ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotalDisplay = new JLabel("Grand Total: Rs. 0.00  ");
        lblTotalDisplay.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        JButton btnAdd = new JButton("Add to List");
        btnAdd.setBackground(new Color(52, 152, 219));
        btnAdd.setForeground(Color.WHITE);

        JButton btnSave = new JButton("COMPLETE PURCHASE");
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(200, 40));

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnSave);

        pnlBottom.add(lblTotalDisplay, BorderLayout.WEST);
        pnlBottom.add(pnlButtons, BorderLayout.EAST);
        add(pnlBottom, BorderLayout.SOUTH);

        // Actions
        btnAdd.addActionListener(e -> addItemToTable());
        btnSave.addActionListener(e -> processPurchase());
    }

    private void loadSuppliers() {
        cmbSupplier.removeAllItems();
        new SupplierDAO().getAllSuppliers().forEach(s -> 
            cmbSupplier.addItem(s.getId() + " - " + s.getName()));
    }

    private void loadProducts() {
        cmbProduct.removeAllItems();
        new ProductDAO().getAllProducts().forEach(p -> 
            cmbProduct.addItem(p.getId() + " - " + p.getName()));
    }

    private void addItemToTable() {
        try {
            String selectedProd = cmbProduct.getSelectedItem().toString();
            int pid = Integer.parseInt(selectedProd.split(" - ")[0]);
            String pName = selectedProd.split(" - ")[1];
            
            int qty = Integer.parseInt(txtQty.getText().trim());
            double cost = Double.parseDouble(txtCost.getText().trim());
            
            if (qty <= 0 || cost <= 0) throw new Exception();

            double subtotal = qty * cost;
            tableModel.addRow(new Object[]{pid, pName, qty, cost, subtotal});
            itemList.add(new PurchaseItem(0, 0, pid, qty, cost));
            
            grandTotal += subtotal;
            lblTotalDisplay.setText("Grand Total: Rs. " + String.format("%.2f", grandTotal));
            
            txtQty.setText("");
            txtCost.setText("");
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, "Please enter valid Quantity and Cost!"); 
        }
    }

    private void processPurchase() {
        if (itemList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "List is empty!");
            return;
        }

        int sid = Integer.parseInt(cmbSupplier.getSelectedItem().toString().split(" - ")[0]);
        
        // Final Checkout
        if (new PurchaseDAO().processPurchase(sid, grandTotal, itemList)) {
            JOptionPane.showMessageDialog(this, "Stock Updated Successfully!");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error processing purchase!");
        }
    }
}