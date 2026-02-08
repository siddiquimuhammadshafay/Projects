package ui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import app.Session;

public class AdminDashboardFrame extends JFrame {
    private JPanel mainContent;
    private JDialog activeDialog = null; // Purani window ko track karne ke liye
    private JLabel lblDateTime;

    public AdminDashboardFrame() {
        setTitle("Inventory POS - Enterprise Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. Top Header ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(41, 128, 185));
        pnlHeader.setPreferredSize(new Dimension(0, 75));

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("   INVENTORY MANAGEMENT SYSTEM", JLabel.LEFT);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        lblDateTime = new JLabel("   Loading Date & Time...", JLabel.LEFT);
        lblDateTime.setForeground(new Color(236, 240, 241));
        lblDateTime.setFont(new Font("Segoe UI", Font.ITALIC, 14));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblDateTime);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // User Info Section
        String name = (Session.getCurrentUser() != null) ? Session.getCurrentUser().getUsername() : "Admin";
        String role = (Session.getCurrentUser() != null) ? Session.getCurrentUser().getRole() : "Master";
        JLabel lblUser = new JLabel("<html><div style='text-align: right;'>Welcome, <b>" + name
                + "</b><br><small>Role: " + role + "</small></div></html>", JLabel.RIGHT);
        lblUser.setForeground(Color.WHITE);
        lblUser.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        pnlHeader.add(lblUser, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. Left Sidebar (Menu) ---
        JPanel pnlSidebar = new JPanel(new GridLayout(10, 1, 8, 8));
        pnlSidebar.setBackground(new Color(52, 73, 94));
        pnlSidebar.setPreferredSize(new Dimension(240, 0));
        pnlSidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        String[] navItems = { "Products", "Purchases", "Categories", "Suppliers", "Sales Report", "POS System", "Low Stock", "Users",
                "Logout" };
        String currentRole = (Session.getCurrentUser() != null) ? Session.getCurrentUser().getRole() : "cashier";

        for (String item : navItems) {
            if (item.equals("Users") && !currentRole.equalsIgnoreCase("admin"))
                continue;

            JButton btn = new JButton(item);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setBackground(new Color(44, 62, 80));
            btn.setForeground(Color.WHITE);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> handleNavigation(item));
            pnlSidebar.add(btn);
        }
        add(pnlSidebar, BorderLayout.WEST);

        // --- 3. Main Content Area ---
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(236, 240, 241));
        JLabel lblWelcome = new JLabel(
                "<html><center><h1>System Online</h1>Select a module to begin operations</center></html>",
                JLabel.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        mainContent.add(lblWelcome, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        startClock();
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            String currentDateTime = new SimpleDateFormat("EEEE, dd MMMM yyyy | hh:mm:ss a").format(new Date());
            lblDateTime.setText("   " + currentDateTime);
        });
        timer.start();
    }

    private void handleNavigation(String item) {
        switch (item) {
            case "Products":
                showModalWindow(new ProductFrame(this));
                break;
            case "Categories":
                showModalWindow(new CategoryFrame(this));
                break;
            case "POS System":
                showModalWindow(new POSFrame(this));
                break;
            case "Sales Report":
                showModalWindow(new ReportsFrame(this));
                break;
            case "Purchases":
                showModalWindow(new PurchaseFrame(this));
                break;
            case "Suppliers":
                showModalWindow(new SupplierFrame(this));
                break;
            case "Users":
                showModalWindow(new UserFrame(this));
                break;
            case "Low Stock":
                showModalWindow(new LowStockFrame(this));
                break;
            case "Logout":
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Exit?", "Logout",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Session.logout();
                    System.exit(0);
                }
                break;
            default:
                JOptionPane.showMessageDialog(this, item + " Module is under construction.");
        }
    }

    private void showModalWindow(JDialog dialog) {
        // Agar pehle se koi screen khuli hai, toh ye usay dispose (close) kar dege
        if (activeDialog != null && activeDialog.isVisible()) {
            activeDialog.dispose();
        }

        activeDialog = dialog;

        // Window settings
        dialog.setModal(true);
        dialog.setVisible(true);
    }
}