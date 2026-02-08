package util;

import java.text.DecimalFormat;

public class MoneyUtil{
    // Currency format set karne ke liye (2 decimal places)
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    public static String format(double amount) {
        return df.format(amount);
    }

    // String ko double mein convert karne ke liye helper
    public static double parse(String text) {
        try {
            return Double.parseDouble(text.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}