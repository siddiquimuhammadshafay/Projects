package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.ProductDAO;
import model.Product;

public class LowStockFrame extends JDialog {
    public LowStockFrame(JFrame owner) {
        super(owner, "⚠️ Low Stock Alerts", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Table setup
        String[] cols = {"Product Name", "Current Stock"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setForeground(Color.RED); // Warning look ke liye red text

        // ScrollPane add kiya taake scroll ho sakay
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Data Load karein (Threshold = 10)
        List<Product> lowStock = new ProductDAO().getLowStockProducts(10);
        for (Product p : lowStock) {
            model.addRow(new Object[]{p.getName(), p.getStockQuantity()});
        }

        if (lowStock.isEmpty()) {
            add(new JLabel("There is no low stock product.", JLabel.CENTER), BorderLayout.NORTH);
        }
    }
}