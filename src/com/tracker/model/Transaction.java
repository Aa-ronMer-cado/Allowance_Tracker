package com.tracker.model;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private int categoryId;
    private String categoryName;
    private LocalDate date;
    private String description;
    private double amount;
    private String type;
    private boolean isExpected;
    private String paymentMethod;

    public Transaction(int id, int categoryId, String categoryName, LocalDate date, 
                      String description, double amount, String type, 
                      boolean isExpected, String paymentMethod) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.isExpected = isExpected;
        this.paymentMethod = paymentMethod;
    }

    // Getters
    public int getId() { return id; }
    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public boolean isExpected() { return isExpected; }
    public String getPaymentMethod() { return paymentMethod; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setType(String type) { this.type = type; }
    public void setExpected(boolean expected) { isExpected = expected; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}