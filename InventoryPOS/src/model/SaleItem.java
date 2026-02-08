package model;

public class SaleItem {
    private int id;
    private int saleId;
    private int productId;
    private int quantity;
    private double price; 

    // Constructor
    public SaleItem(int id, int saleId, int productId, int quantity, double price) {
        this.id = id;
        this.saleId = saleId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; } 
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
}