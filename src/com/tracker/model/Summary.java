package com.tracker.model;

public class Summary {
    private double actualAllowance;
    private double expectedAllowance;
    private double actualExpenses;
    private double expectedExpenses;

    public Summary(double actualAllowance, double expectedAllowance,
                double actualExpenses, double expectedExpenses) {
        this.actualAllowance = actualAllowance;
        this.expectedAllowance = expectedAllowance;
        this.actualExpenses = actualExpenses;
        this.expectedExpenses = expectedExpenses;
    }

    public double getActualAllowance() { return actualAllowance; }
    public double getExpectedAllowance() { return expectedAllowance; }
    public double getTotalAllowance() { return actualAllowance + expectedAllowance; }
    public double getActualExpenses() { return actualExpenses; }
    public double getExpectedExpenses() { return expectedExpenses; }
    public double getTotalExpenses() { return actualExpenses + expectedExpenses; }
    public double getActualSavings() { return actualAllowance - actualExpenses; }
    public double getPredictedRemaining() { 
        return (actualAllowance + expectedAllowance) - (actualExpenses + expectedExpenses); 
    }
}