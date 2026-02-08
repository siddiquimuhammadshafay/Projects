package ui;

import javax.swing.*;
import java.awt.*;


public class CashierDashboardFrame extends JFrame {

    public CashierDashboardFrame() {
        setTitle("Cashier Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 260);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnPOS = new JButton("Open POS");
        JButton btnExit = new JButton("Exit");

        btnPOS.addActionListener(e -> {
            POSFrame pos = new POSFrame(this);
            pos.setVisible(true);
        });

        p.add(new JLabel("Welcome Cashier", SwingConstants.CENTER));
        p.add(btnPOS);
        p.add(btnExit);

        add(p);
    }
}
