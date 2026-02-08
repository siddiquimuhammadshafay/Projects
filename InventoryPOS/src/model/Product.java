package model;

public class Product {
    private int id;
    private String name;
    private int categoryId;
    private double price;
    private int stockQuantity;

   
    public Product(int id, String name, int categoryId, double price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

  
    public int getId() { return id; }
    public String getName() { return name; }
    public int getCategoryId() { return categoryId; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    @Override
public String toString() {

    return this.getName(); 
}
}