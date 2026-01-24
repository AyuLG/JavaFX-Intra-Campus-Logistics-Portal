package anagno.controller;

import anagno.utils.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TakerController {

    @FXML private VBox parentContainer;
    @FXML private FlowPane merchantContainer;
    @FXML private TextField searchField;

    private final String CHIP_BASE_STYLE = 
        "-fx-background-color: white; " +
        "-fx-border-color: #E2E8F0; " +
        "-fx-border-radius: 12; " +
        "-fx-background-radius: 12; " +
        "-fx-padding: 15; " +
        "-fx-cursor: hand; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);";

    @FXML
    public void initialize() {
        // Safe load to prevent crash if DB is not connected
        try {
            refreshMarketplace();
        } catch (Exception e) {
            System.err.println("DB Connection failed during initialization. Loading fallbacks.");
            showFallbackMerchants();
        }
        
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterShops(newVal));
        }
    }

    /**
     * Matches onAction="#refreshMerchants" in taker_dashboard.fxml
     */
    @FXML
    private void refreshMerchants(ActionEvent event) {
        refreshMarketplace();
    }

    /**
     * Connects to database to find registered Merchants or shows fallbacks on failure
     */
    private void refreshMarketplace() {
        if (merchantContainer == null) return;
        merchantContainer.getChildren().clear();
        
        String query = "SELECT username FROM users WHERE role = 'MERCHANT'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                // Dynamically create a card for each merchant found in the database
                addShopCard(rs.getString("username"), "University Vendor", "Campus Market");
            }
            
            // If the database is connected but empty, show the fallback merchants
            if (!found) showFallbackMerchants();

        } catch (Exception e) {
            // If the connection itself fails, show the fallback merchants
            showFallbackMerchants();
        }
    }

    /**
     * Original fallback merchants to ensure the screen is never empty
     */
    private void showFallbackMerchants() {
        addShopCard("Fikir migb", "Beyaynet & Shiro", "Main Gate");
        addShopCard("Enat Migb", "Firfir & Shiro", "Opposite entrance");
        addShopCard("Amu Migb", "Pasta be Sigo & Macaroni", "Peace Forum");
        addShopCard("Ertib Meshecha", "Ertib & Chips", "Main Gate");
        addShopCard("Suk", "Fresh Juice & Snacks", "Dorm 224");
    }

    private void addShopCard(String name, String specialty, String location) {
        VBox card = new VBox(8);
        card.setPrefSize(165, 110);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(CHIP_BASE_STYLE);

        Label nameLabel = new Label(name.toUpperCase());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E293B;");
        
        Label foodLabel = new Label(specialty);
        foodLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        
        Label locLabel = new Label("📍 " + location);
        locLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #6366F1; -fx-font-weight: bold;");

        card.getChildren().addAll(nameLabel, foodLabel, locLabel);

        // Hover animation
        card.setOnMouseEntered(e -> card.setStyle(CHIP_BASE_STYLE + "-fx-border-color: #6366F1; -fx-scale-x: 1.02; -fx-scale-y: 1.02;"));
        card.setOnMouseExited(e -> card.setStyle(CHIP_BASE_STYLE));
        
        // Navigation link to the shop menu
        card.setOnMouseClicked(event -> openShop(name));

        merchantContainer.getChildren().add(card);
    }

    private void openShop(String merchantName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/anagno/view/shop_view.fxml"));
            Parent root = loader.load();
            
            // Passes the clicked name to the ShopViewController
            ShopViewController controller = loader.getController();
            controller.setShopDetails(merchantName);
            
            Stage stage = (Stage) merchantContainer.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) { 
            System.err.println("Navigation Error: Check if shop_view.fxml exists.");
            e.printStackTrace(); 
        }
    }

    /**
     * Matches onAction="#handleOpenSettings" in taker_dashboard.fxml
     */
    @FXML
    private void handleOpenSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/anagno/view/settings.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); 
            popupStage.initStyle(StageStyle.UNDECORATED);       
            
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); 
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/anagno/view/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 420, 700));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML 
    private void handleMinimize(ActionEvent event) { 
        if (parentContainer != null && parentContainer.getScene() != null) {
            ((Stage) parentContainer.getScene().getWindow()).setIconified(true); 
        }
    }

    @FXML 
    private void handleClose(ActionEvent event) { 
        System.exit(0); 
    }

    private void filterShops(String query) {
        String lowerQuery = query.toLowerCase();
        for (Node node : merchantContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;
                Label nameLabel = (Label) card.getChildren().get(0);
                boolean match = nameLabel.getText().toLowerCase().contains(lowerQuery);
                card.setVisible(match);
                card.setManaged(match);
            }
        }
    }
}