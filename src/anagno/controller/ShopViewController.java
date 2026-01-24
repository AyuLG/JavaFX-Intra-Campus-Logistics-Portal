package anagno.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;

public class ShopViewController {

    @FXML private VBox parentContainer;
    @FXML private Label shopNameLabel;
    @FXML private Label cartTotalLabel; // Matches fx:id in FXML
    @FXML private FlowPane productContainer;
    @FXML private TextField searchField;

    private double totalAmount = 0.0; // Tracks the running total for payment

    private final String FOOD_CHIP_STYLE = 
        "-fx-background-color: white; " +
        "-fx-border-color: #E2E8F0; " +
        "-fx-padding: 15; " +
        "-fx-border-radius: 12; " +
        "-fx-background-radius: 12; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 4);";

    /**
     * Receives shop name from TakerController and sets up the menu
     */
    public void setShopDetails(String name) {
        if (shopNameLabel != null) {
            shopNameLabel.setText(name);
        }
        loadMenu(name.toLowerCase());
    }

    /**
     * Integrated menu logic: Combines your specific shop items with the price system
     */
    private void loadMenu(String shopName) {
        if (productContainer == null) return;
        productContainer.getChildren().clear();

        if (shopName.contains("mama") || shopName.contains("enat") || shopName.contains("fikir")) {
            createProductCard("Special Beyaynet", 120.0);
            createProductCard("Shiro Tegabino", 85.0);
            createProductCard("Firfir", 90.0);
        } else if (shopName.contains("pasta") || shopName.contains("amu")) {
            createProductCard("Pasta be Sigo", 110.0);
            createProductCard("Vegetable Macaroni", 85.0);
        } else if (shopName.contains("burger") || shopName.contains("fast food")) {
            createProductCard("Ertib", 50.0);
            createProductCard("Chips", 20.0);
            createProductCard("Classic Burger", 160.0);
        } else {
            createProductCard("Fresh Juice", 60.0);
            createProductCard("Sambusa", 15.0);
            createProductCard("Ertib", 40.0);
        }
    }

    /**
     * Creates a Product Card with an active "Add to Basket" button
     */
    private void createProductCard(String name, double price) {
        VBox card = new VBox(12);
        card.setPrefWidth(160);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(FOOD_CHIP_STYLE);

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B; -fx-font-size: 13px;");
        
        Label priceLbl = new Label(String.format("%.2f ETB", price));
        priceLbl.setStyle("-fx-text-fill: #6366F1; -fx-font-weight: bold; -fx-font-size: 12px;");

        Button addBtn = new Button("ADD TO BASKET");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setPrefHeight(30);
        addBtn.setStyle(
            "-fx-background-color: #F1F5F9; " +
            "-fx-text-fill: #475569; " +
            "-fx-font-size: 10px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );

        // Hover Effect
        addBtn.setOnMouseEntered(e -> addBtn.setStyle("-fx-background-color: #6366F1; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 8;"));
        addBtn.setOnMouseExited(e -> addBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 8;"));

        // Logic to update the price when clicked
        addBtn.setOnAction(e -> handleAddToCart(name, price));

        card.getChildren().addAll(nameLbl, priceLbl, addBtn);
        productContainer.getChildren().add(card);
    }

    /**
     * Updates the UI total amount whenever an item is added
     */
    private void handleAddToCart(String itemName, double price) {
        totalAmount += price;
        if (cartTotalLabel != null) {
            cartTotalLabel.setText(String.format("%.2f ETB", totalAmount));
        }
        System.out.println("Cart: " + itemName + " added. New Total: " + totalAmount);
    }

    /**
     * PROCEED TO PAYMENT
     * Triggered by the "VIEW CART" or "PROCEED" button in FXML
     */
    @FXML
private void handleViewCart(ActionEvent event) {
    if (totalAmount <= 0) return;

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/anagno/view/payment_view.fxml"));
        Parent root = loader.load();
        
        // GET THE CONTROLLER AND PASS DATA
        PaymentController controller = loader.getController();
        controller.setPaymentAmount(totalAmount);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 420, 700));
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    /**
     * Navigates back to the Dashboard
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/anagno/view/taker_dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 420, 700));
        } catch (IOException e) {
            System.err.println("Error finding taker_dashboard.fxml");
            e.printStackTrace();
        }
    }

    @FXML 
    private void handleMinimize(ActionEvent event) { 
        ((Stage) parentContainer.getScene().getWindow()).setIconified(true); 
    }

    @FXML 
    private void handleClose(ActionEvent event) { 
        System.exit(0); 
    }
}