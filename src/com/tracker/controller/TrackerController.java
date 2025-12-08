package com.tracker.controller;

import com.tracker.database.DatabaseManager;
import com.tracker.model.*;
import java.time.LocalDate;
import java.util.List;

public class TrackerController {
    private DatabaseManager dbManager;

    public TrackerController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void addTransaction(LocalDate date, String description, double amount,
                              String type, boolean isExpected, int categoryId, 
                              String paymentMethod) {
        Transaction transaction = new Transaction(
            0, categoryId, "", date, description, 
            amount, type, isExpected, paymentMethod
        );
        dbManager.addTransaction(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return dbManager.getAllTransactions();
    }

    public List<Category> getAllCategories() {
        return dbManager.getAllCategories();
    }

    public void deleteTransaction(int id) {
        dbManager.deleteTransaction(id);
    }

    public Summary calculateSummary() {
        return dbManager.calculateSummary();
    }

    public void closeDatabase() {
        dbManager.close();
    }
}