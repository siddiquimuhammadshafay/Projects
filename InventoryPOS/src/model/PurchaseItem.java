package model;

public class PurchaseItem {
    private int id;
    private int purchaseId;
    private int productId;
    private int quantity;
    private double costPrice;

    public PurchaseItem(int id, int purchaseId, int productId, int quantity, double costPrice) {
        this.id = id;
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.quantity = quantity;
        this.costPrice = costPrice;
    }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getCostPrice() { return costPrice; }
}