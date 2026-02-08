package model;

import java.sql.Timestamp;

public class Purchase {
    private int id;
    private int supplierId;
    private Timestamp purchaseDate;
    private double totalAmount;

    public Purchase(int id, int supplierId, Timestamp purchaseDate, double totalAmount) {
        this.id = id;
        this.supplierId = supplierId;
        this.purchaseDate = purchaseDate;
        this.totalAmount = totalAmount;
    }
}