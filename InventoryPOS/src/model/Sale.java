package model;

import java.util.Date;

public class Sale {
    private int id;
    private Date saleDate;
    private double totalAmount;
    
   
    private String userName;        
    private String productsSummary; 

   
    public String getUserName() { 
        return userName; 
    }

    
    public void setUserName(String userName) { 
        this.userName = userName; 
    }

  
    public String getProductsSummary() { 
        return productsSummary; 
    }

    public void setProductsSummary(String productsSummary) { 
        this.productsSummary = productsSummary; 
    }

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getSaleDate() { return saleDate; }
    public void setSaleDate(Date saleDate) { this.saleDate = saleDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}