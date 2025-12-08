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

    public int getId() { return id; }
    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public boolean isExpected() { return isExpected; }
    public String getPaymentMethod() { return paymentMethod; }
}
