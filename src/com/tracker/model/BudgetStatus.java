package com.tracker.model;

public class BudgetStatus {
    private String categoryName;
    private double monthlyLimit;
    private double currentSpending;
    private double utilizationPercent;
    private double alertThreshold;

    public BudgetStatus(String categoryName, double monthlyLimit, double currentSpending,
                       double utilizationPercent, double alertThreshold) {
        this.categoryName = categoryName;
        this.monthlyLimit = monthlyLimit;
        this.currentSpending = currentSpending;
        this.utilizationPercent = utilizationPercent;
        this.alertThreshold = alertThreshold;
    }

    public String getCategoryName() { return categoryName; }
    public double getMonthlyLimit() { return monthlyLimit; }
    public double getCurrentSpending() { return currentSpending; }
    public double getUtilizationPercent() { return utilizationPercent; }
    public double getAlertThreshold() { return alertThreshold; }
    public double getRemaining() { return monthlyLimit - currentSpending; }
    
    public String getStatus() {
        if (utilizationPercent >= 100) return "EXCEEDED";
        if (utilizationPercent >= alertThreshold) return "WARNING";
        return "OK";
    }
}