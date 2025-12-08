package com.tracker.view;

import com.tracker.controller.TrackerController;
import com.tracker.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class TrackerView {
    private Stage stage;
    private TrackerController controller;
    private TableView<Transaction> transactionTable;
    private VBox summaryCardsContainer;
    private VBox summaryTableContainer;

    public TrackerView(Stage stage, TrackerController controller) {
        this.stage = stage;
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f0f4f8;");

        root.setTop(createHeader());
        
        VBox centerContent = new VBox(15);
        summaryCardsContainer = createSummaryCards();
        summaryTableContainer = createExpectedVsActualTable();
        VBox transactionContainer = createTransactionTable();
        
        centerContent.getChildren().addAll(
            summaryCardsContainer,
            summaryTableContainer,
            transactionContainer
        );
        
        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Allowance Tracker - XAMPP SQL");
        stage.setOnCloseRequest(e -> {
            controller.closeDatabase();
            System.out.println("Application closed");
        });
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                       "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label title = new Label("Allowance Tracker");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#1e40af"));

        Label subtitle = new Label("XAMPP SQL");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subtitle.setTextFill(Color.web("#6b7280"));

        VBox titleBox = new VBox(5, title, subtitle);

        Button addButton = new Button("+ Add Transaction");
        addButton.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; " +
                          "-fx-font-size: 14px; -fx-padding: 10 20; " +
                          "-fx-background-radius: 8; -fx-cursor: hand;");
        addButton.setOnAction(e -> showAddTransactionDialog());

        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; " +
                              "-fx-font-size: 14px; -fx-padding: 10 20; " +
                              "-fx-background-radius: 8; -fx-cursor: hand;");
        refreshButton.setOnAction(e -> refreshAll());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, spacer, refreshButton, addButton);
        return header;
    }

    private VBox createSummaryCards() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(10, 0, 0, 0));

        Summary summary = controller.calculateSummary();

        GridPane grid = new GridPane();
        grid.setHgap(15);

        VBox allowanceCard = createCard(
            "Total Allowance", 
            String.format("₱%.2f", summary.getActualAllowance()),
            String.format("+ ₱%.2f expected", summary.getExpectedAllowance()),
            "#10b981"
        );

        VBox expensesCard = createCard(
            "Total Expenses",
            String.format("₱%.2f", summary.getActualExpenses()),
            String.format("+ ₱%.2f expected", summary.getExpectedExpenses()),
            "#ef4444"
        );

        String balanceColor = summary.getPredictedRemaining() >= 0 ? "#3b82f6" : "#f59e0b";
        String balanceStatus = summary.getPredictedRemaining() >= 0 ? "On track!" : "Over budget!";
        VBox balanceCard = createCard(
            "Predicted Balance",
            String.format("₱%.2f", summary.getPredictedRemaining()),
            balanceStatus,
            balanceColor
        );

        grid.add(allowanceCard, 0, 0);
        grid.add(expensesCard, 1, 0);
        grid.add(balanceCard, 2, 0);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(33.33);
        grid.getColumnConstraints().addAll(col, col, col);

        container.getChildren().add(grid);
        return container;
    }

    private VBox createCard(String title, String value, String subtitle, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 15; " +
                                   "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);", color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        titleLabel.setTextFill(Color.web("#ffffff", 0.9));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subtitleLabel.setTextFill(Color.web("#ffffff", 0.8));

        card.getChildren().addAll(titleLabel, valueLabel, subtitleLabel);
        return card;
    }

    private VBox createExpectedVsActualTable() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                          "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label title = new Label("📊 Expected vs Actual Summary");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 0, 0, 0));

        Summary summary = controller.calculateSummary();

        grid.add(createHeaderLabel("Type"), 0, 0);
        grid.add(createHeaderLabel("Expected"), 1, 0);
        grid.add(createHeaderLabel("Actual"), 2, 0);
        grid.add(createHeaderLabel("Difference"), 3, 0);

        grid.add(new Label("Allowance"), 0, 1);
        grid.add(createValueLabel(String.format("₱%.2f", summary.getExpectedAllowance()), "#3b82f6"), 1, 1);
        grid.add(createValueLabel(String.format("₱%.2f", summary.getActualAllowance()), "#10b981"), 2, 1);
        double allowanceDiff = summary.getActualAllowance() - summary.getExpectedAllowance();
        grid.add(createValueLabel(String.format("%s₱%.2f", allowanceDiff >= 0 ? "+" : "", allowanceDiff),
                allowanceDiff >= 0 ? "#10b981" : "#ef4444"), 3, 1);

        grid.add(new Label("Expenses"), 0, 2);
        grid.add(createValueLabel(String.format("₱%.2f", summary.getExpectedExpenses()), "#f59e0b"), 1, 2);
        grid.add(createValueLabel(String.format("₱%.2f", summary.getActualExpenses()), "#ef4444"), 2, 2);
        double expensesDiff = summary.getActualExpenses() - summary.getExpectedExpenses();
        grid.add(createValueLabel(String.format("%s₱%.2f", expensesDiff >= 0 ? "+" : "", expensesDiff),
                expensesDiff <= 0 ? "#10b981" : "#ef4444"), 3, 2);

        grid.add(createHeaderLabel("Net Savings"), 0, 3);
        grid.add(createValueLabel(String.format("₱%.2f", summary.getExpectedAllowance() - summary.getExpectedExpenses()), "#3b82f6"), 1, 3);
        grid.add(createValueLabel(String.format("₱%.2f", summary.getActualSavings()), "#6366f1"), 2, 3);
        grid.add(createValueLabel(String.format("₱%.2f", summary.getPredictedRemaining()),
                summary.getPredictedRemaining() >= 0 ? "#10b981" : "#ef4444"), 3, 3);

        container.getChildren().addAll(title, grid);
        return container;
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 13));
        return label;
    }

    private Label createValueLabel(String text, String color) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 13));
        label.setTextFill(Color.web(color));
        return label;
    }

    @SuppressWarnings("unchecked")
    private VBox createTransactionTable() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                          "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label title = new Label("📝 Transaction History");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        transactionTable = new TableView<>();
        
        // FIXED: Use non-deprecated resize policy
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        transactionTable.setPlaceholder(new Label("No transactions yet. Click '+ Add Transaction' to start!"));

        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryCol.setPrefWidth(150);

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);
        amountCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);

        TableColumn<Transaction, Boolean> expectedCol = new TableColumn<>("Expected");
        expectedCol.setCellValueFactory(new PropertyValueFactory<>("expected"));
        expectedCol.setPrefWidth(80);
        expectedCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Transaction, String> methodCol = new TableColumn<>("Payment");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        methodCol.setPrefWidth(100);

        TableColumn<Transaction, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(80);
        actionCol.setCellFactory(col -> {
            TableCell<Transaction, Void> cell = new TableCell<>() {
                private final Button deleteBtn = new Button("🗑");
                {
                    deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                                     "-fx-font-size: 12px; -fx-padding: 5 10; -fx-cursor: hand;");
                    deleteBtn.setOnAction(e -> {
                        Transaction t = getTableView().getItems().get(getIndex());
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Delete Transaction");
                        confirm.setHeaderText("Are you sure?");
                        confirm.setContentText("Delete: " + t.getDescription());
                        
                        if (confirm.showAndWait().get() == ButtonType.OK) {
                            controller.deleteTransaction(t.getId());
                            refreshAll();
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : deleteBtn);
                }
            };
            return cell;
        });

        // FIXED: Suppress type safety warning (this is safe code)
        transactionTable.getColumns().addAll(dateCol, descCol, categoryCol, amountCol, 
                                            typeCol, expectedCol, methodCol, actionCol);
        refreshTable();

        container.getChildren().addAll(title, transactionTable);
        VBox.setVgrow(transactionTable, Priority.ALWAYS);
        return container;
    }

    private void showAddTransactionDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Transaction");
        dialog.setHeaderText("Enter transaction details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField descField = new TextField();
        descField.setPromptText("e.g., Lunch at cafeteria");
        
        TextField amountField = new TextField();
        amountField.setPromptText("e.g., 150.00");
        
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("allowance", "expense", "savings");
        typeBox.setValue("expense");
        
        CheckBox expectedCheck = new CheckBox("Expected / Forecasted");
        
        ComboBox<Category> categoryBox = new ComboBox<>();
        List<Category> categories = controller.getAllCategories();
        categoryBox.getItems().addAll(categories);
        if (!categories.isEmpty()) categoryBox.setValue(categories.get(0));
        
        ComboBox<String> methodBox = new ComboBox<>();
        methodBox.getItems().addAll("Cash", "GCash", "Bank Transfer", "Credit Card");
        methodBox.setValue("Cash");

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Amount (₱):"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Type:"), 0, 3);
        grid.add(typeBox, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryBox, 1, 4);
        grid.add(new Label("Payment Method:"), 0, 5);
        grid.add(methodBox, 1, 5);
        grid.add(expectedCheck, 1, 6);

        expectedCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            methodBox.setDisable(newVal);
            if (newVal) methodBox.setValue(null);
        });

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (descField.getText().trim().isEmpty()) {
                        showError("Description cannot be empty!");
                        return;
                    }
                    
                    double amount = Double.parseDouble(amountField.getText());
                    if (amount <= 0) {
                        showError("Amount must be greater than zero!");
                        return;
                    }
                    
                    controller.addTransaction(
                        datePicker.getValue(),
                        descField.getText().trim(),
                        amount,
                        typeBox.getValue(),
                        expectedCheck.isSelected(),
                        categoryBox.getValue().getCategoryId(),
                        expectedCheck.isSelected() ? null : methodBox.getValue()
                    );
                    
                    refreshAll();
                    showSuccess("Transaction added successfully!");
                    
                } catch (NumberFormatException e) {
                    showError("Please enter a valid amount!");
                } catch (Exception e) {
                    showError("Error: " + e.getMessage());
                }
            }
        });
    }

    private void refreshTable() {
        transactionTable.getItems().clear();
        transactionTable.getItems().addAll(controller.getAllTransactions());
    }

    private void refreshAll() {
        refreshTable();
        
        Summary summary = controller.calculateSummary();
        
        summaryCardsContainer.getChildren().clear();
        GridPane newGrid = new GridPane();
        newGrid.setHgap(15);
        
        newGrid.add(createCard("Total Allowance", 
            String.format("₱%.2f", summary.getActualAllowance()),
            String.format("+ ₱%.2f expected", summary.getExpectedAllowance()), "#10b981"), 0, 0);
        
        newGrid.add(createCard("Total Expenses",
            String.format("₱%.2f", summary.getActualExpenses()),
            String.format("+ ₱%.2f expected", summary.getExpectedExpenses()), "#ef4444"), 1, 0);
        
        String balanceColor = summary.getPredictedRemaining() >= 0 ? "#3b82f6" : "#f59e0b";
        newGrid.add(createCard("Predicted Balance",
            String.format("₱%.2f", summary.getPredictedRemaining()),
            summary.getPredictedRemaining() >= 0 ? "On track!" : "Over budget!", balanceColor), 2, 0);
        
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(33.33);
        newGrid.getColumnConstraints().addAll(col, col, col);
        
        summaryCardsContainer.getChildren().add(newGrid);
        
        System.out.println("View refreshed");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
}