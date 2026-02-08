package ui;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import dao.SaleDAO;
import model.Sale;

// 1. JFrame ki jagah JDialog use kiya hai modal functionality ke liye
public class ReportsFrame extends JDialog {
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JLabel lblGrandTotal;
    private JDateChooser startDateChooser, endDateChooser;

    // 2. Constructor mein JFrame owner pass karein
    public ReportsFrame(JFrame owner) {
        // Modal set to true taake aik waqt mein sirf yahi screen open ho
        super(owner, "Business Performance & Sales Report", true);
        
        setSize(1000, 700);
        setLocationRelativeTo(owner); // Dashboard ke center mein open hoga
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // --- 1. Header & Calendar Filter Panel ---
        JPanel pnlNorth = new JPanel(new BorderLayout());
        
        JPanel pnlHeader = new JPanel(new GridLayout(1, 1));
        pnlHeader.setBackground(new Color(41, 128, 185));
        pnlHeader.setPreferredSize(new Dimension(0, 50));
        JLabel title = new JLabel("  EXECUTIVE SALES SUMMARY", JLabel.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pnlHeader.add(title);

        // Professional Calendar Panel
        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlFilters.setBackground(new Color(236, 240, 241));
        
        pnlFilters.add(new JLabel("From:"));
        startDateChooser = new JDateChooser();
        startDateChooser.setPreferredSize(new Dimension(150, 25));
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        pnlFilters.add(startDateChooser);
        
        pnlFilters.add(new JLabel("To:"));
        endDateChooser = new JDateChooser();
        endDateChooser.setPreferredSize(new Dimension(150, 25));
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        pnlFilters.add(endDateChooser);

        JButton btnSearch = new JButton("Filter by Date");
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> loadFilteredData());
        pnlFilters.add(btnSearch);

        pnlNorth.add(pnlHeader, BorderLayout.NORTH);
        pnlNorth.add(pnlFilters, BorderLayout.SOUTH);
        add(pnlNorth, BorderLayout.NORTH);

        // --- 2. Table Section ---
        String[] columns = {"Sale ID", "Date & Time", "Performed By", "Sold Products", "Amount (Rs.)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(35); // Height thori barhai hai readability ke liye
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Product list wali column thori bari rakhi hai
        reportTable.getColumnModel().getColumn(3).setPreferredWidth(300); 
        
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // --- 3. Footer ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        lblGrandTotal = new JLabel("Total Revenue: Rs. 0.00");
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblGrandTotal.setForeground(new Color(44, 62, 80));
        pnlFooter.add(lblGrandTotal);
        add(pnlFooter, BorderLayout.SOUTH);

        loadReportData();
    }

    private void loadFilteredData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = startDateChooser.getDate();
        Date end = endDateChooser.getDate();

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Please select both dates from the calendar!");
            return;
        }

        String startDateStr = sdf.format(start);
        String endDateStr = sdf.format(end);

        // DAO call
        updateTable(new SaleDAO().getFilteredSales(startDateStr, endDateStr));
    }

    private void updateTable(List<Sale> sales) {
        double grandTotal = 0;
        tableModel.setRowCount(0); 

        for (Sale s : sales) {
            grandTotal += s.getTotalAmount();
            tableModel.addRow(new Object[]{
                s.getId(),
                s.getSaleDate(),
                s.getUserName(), // Fixed: Pehle Sale model mein ye add kiya tha
                s.getProductsSummary(),
                "Rs. " + String.format("%.2f", s.getTotalAmount())
            });
        }
        lblGrandTotal.setText("Total Revenue: Rs. " + String.format("%.2f", grandTotal));
    }

    private void loadReportData() {
        updateTable(new SaleDAO().getAllSales());
    }
}