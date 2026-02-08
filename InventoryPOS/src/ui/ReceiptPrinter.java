package ui;

import java.awt.*;
import java.awt.print.*;
import java.util.List;


public class ReceiptPrinter implements Printable {
    private List<Object[]> cartData; // Table ka data
    private double total;

    public ReceiptPrinter(List<Object[]> cartData, double total) {
        this.cartData = cartData;
        this.total = total;
    }
@Override
public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
    if (pageIndex > 0) return NO_SUCH_PAGE;

    Graphics2D g2d = (Graphics2D) graphics;
    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

    int y = 20;
    g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
    g2d.drawString("INVENTORY MANAGEMENT SYSTEM", 50, y); y += 20;
    g2d.drawString("--------------------------------", 10, y); y += 15;
    g2d.drawString("Item          Qty    Price    Total", 10, y); y += 15;
    g2d.drawString("--------------------------------", 10, y); y += 15;

    g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
    
    for (Object[] row : cartData) {
        // FIXED: Column alignment aur indexes sahi kar diye
        String name = String.format("%-13s", row[0].toString()); // Product Name (Index 0)
        String qty = String.format("%-6s", row[1].toString());  // Qty (Index 1)
        String price = String.format("%-8s", row[2].toString()); // Price (Index 2)
        String sub = row[3].toString();                         // Subtotal (Index 3)
        
        g2d.drawString(name + qty + price + sub, 10, y);
        y += 15;
    }

    g2d.drawString("--------------------------------", 10, y); y += 15;
    g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
    g2d.drawString("GRAND TOTAL: Rs. " + total, 10, y); y += 20; // RS. kar diya local currency ke liye
    g2d.drawString("   THANK YOU FOR SHOPPING!", 30, y);

    return PAGE_EXISTS;
}
}