package com.tracker.database;

import com.tracker.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class DatabaseManager {
    // XAMPP MySQL Connection Settings
    private static final String DB_URL = "jdbc:mysql://localhost:3306/allowance_tracker?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";  // XAMPP default: empty password
    
    private Connection connection;

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Connected to MySQL (XAMPP) successfully!");
            
            // Auto-create default user if missing
            ensureDefaultUserExists();
            
            printDatabaseStats();
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC Driver not found!");
            System.err.println("  Add mysql-connector-java-8.0.33.jar to lib/ folder");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed!");
            System.err.println("  1. Check if XAMPP MySQL is running");
            System.err.println("  2. Verify database 'allowance_tracker' exists");
            System.err.println("  3. Check port 3306 is not blocked");
            e.printStackTrace();
        }
    }
    
    // Auto-create default user if not exists
    private void ensureDefaultUserExists() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE user_id = 1");
            
            if (rs.next() && rs.getInt(1) == 0) {
                // No user with ID 1, create it
                String insertUser = "INSERT INTO users (user_id, name, email) VALUES (1, 'Demo User', 'demo@allowancetracker.com')";
                stmt.executeUpdate(insertUser);
                System.out.println("✓ Default user created (ID: 1)");
            } else {
                System.out.println("✓ Default user exists (ID: 1)");
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("⚠ Warning: Could not verify default user");
            e.printStackTrace();
        }
    }

    // CREATE
    public int addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, category_id, description, amount, " +
                    "transaction_type, is_expected, payment_method, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, 1);
            pstmt.setInt(2, transaction.getCategoryId());
            pstmt.setString(3, transaction.getDescription());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setString(5, transaction.getType());
            pstmt.setBoolean(6, transaction.isExpected());
            pstmt.setString(7, transaction.getPaymentMethod());
            pstmt.setDate(8, Date.valueOf(transaction.getDate()));
            
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                System.out.println("✓ Transaction added: ID " + keys.getInt(1));
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error adding transaction: " + e.getMessage());
        }
        return -1;
    }

    public void addBudget(int categoryId, double monthlyLimit, double alertThreshold, String monthYear) {
        String sql = "INSERT INTO budgets (user_id, category_id, monthly_limit, alert_threshold, month_year) " +
                    "VALUES (1, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "monthly_limit = VALUES(monthly_limit), alert_threshold = VALUES(alert_threshold)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            pstmt.setDouble(2, monthlyLimit);
            pstmt.setDouble(3, alertThreshold);
            pstmt.setString(4, monthYear);
            pstmt.executeUpdate();
            System.out.println("✓ Budget set for category " + categoryId);
        } catch (SQLException e) {
            System.err.println("✗ Error setting budget: " + e.getMessage());
        }
    }

    // READ
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, c.name as category_name, c.icon FROM transactions t " +
                    "JOIN categories c ON t.category_id = c.category_id " +
                    "WHERE t.user_id = 1 ORDER BY t.transaction_date DESC, t.transaction_id DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getDate("transaction_date").toLocalDate(),
                    rs.getString("description"),
                    rs.getDouble("amount"),
                    rs.getString("transaction_type"),
                    rs.getBoolean("is_expected"),
                    rs.getString("payment_method")
                ));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY type, name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("category_id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getString("icon")
                ));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error fetching categories: " + e.getMessage());
        }
        return categories;
    }

    // UPDATE
    public void updateTransaction(int id, String description, double amount, 
                                  int categoryId, String paymentMethod, LocalDate date) {
        String sql = "UPDATE transactions SET description=?, amount=?, category_id=?, " +
                    "payment_method=?, transaction_date=? WHERE transaction_id=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setDouble(2, amount);
            pstmt.setInt(3, categoryId);
            pstmt.setString(4, paymentMethod);
            pstmt.setDate(5, Date.valueOf(date));
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            System.out.println("✓ Transaction " + id + " updated");
        } catch (SQLException e) {
            System.err.println("✗ Error updating transaction: " + e.getMessage());
        }
    }

    // DELETE
    public void deleteTransaction(int transactionId) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            pstmt.executeUpdate();
            System.out.println("✓ Transaction " + transactionId + " deleted");
        } catch (SQLException e) {
            System.err.println("✗ Error deleting transaction: " + e.getMessage());
        }
    }

    // SUMMARY CALCULATION
    public Summary calculateSummary() {
        String sql = "SELECT " +
                    "COALESCE(SUM(CASE WHEN transaction_type='allowance' AND is_expected=0 THEN amount ELSE 0 END), 0) as actual_allowance, " +
                    "COALESCE(SUM(CASE WHEN transaction_type='allowance' AND is_expected=1 THEN amount ELSE 0 END), 0) as expected_allowance, " +
                    "COALESCE(SUM(CASE WHEN transaction_type='expense' AND is_expected=0 THEN amount ELSE 0 END), 0) as actual_expenses, " +
                    "COALESCE(SUM(CASE WHEN transaction_type='expense' AND is_expected=1 THEN amount ELSE 0 END), 0) as expected_expenses " +
                    "FROM transactions WHERE user_id = 1";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return new Summary(
                    rs.getDouble("actual_allowance"),
                    rs.getDouble("expected_allowance"),
                    rs.getDouble("actual_expenses"),
                    rs.getDouble("expected_expenses")
                );
            }
        } catch (SQLException e) {
            System.err.println("✗ Error calculating summary: " + e.getMessage());
        }
        return new Summary(0, 0, 0, 0);
    }

    public boolean testConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
        }
        return false;
    }

    public void printDatabaseStats() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories");
            if (rs.next()) System.out.println("  → Categories: " + rs.getInt(1));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM transactions");
            if (rs.next()) System.out.println("  → Transactions: " + rs.getInt(1));
        } catch (SQLException e) {
            System.err.println("Error fetching stats: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}