package com.tracker;

import javafx.application.Application;
import javafx.stage.Stage;
import com.tracker.database.DatabaseManager;
import com.tracker.view.TrackerView;
import com.tracker.controller.TrackerController;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("=== ALLOWANCE TRACKER STARTING ===");
            
            // Initialize database connection
            DatabaseManager dbManager = new DatabaseManager();
            
            // Test connection
            if (!dbManager.testConnection()) {
                System.err.println("Failed to connect to database!");
                System.err.println("Make sure XAMPP MySQL is running on port 3306");
                return;
            }
            
            // Create controller
            TrackerController controller = new TrackerController(dbManager);
            
            // Create and show view
            TrackerView view = new TrackerView(primaryStage, controller);
            view.show();
            
            System.out.println("=== APPLICATION STARTED SUCCESSFULLY ===");
            
        } catch (Exception e) {
            System.err.println("Error starting application:");
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        System.out.println("=== APPLICATION CLOSING ===");
    }

    public static void main(String[] args) {
        launch(args);
    }
}