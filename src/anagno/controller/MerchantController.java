package anagno.controller;

import anagno.model.Product;
import anagno.utils.DatabaseConnection;
import anagno.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.sql.*;

public class MerchantController {

    @FXML private VBox parentContainer;
    @FXML private Label pageTitle;
    @FXML private TextField searchField; 

    @FXML private TableView<Product> inventoryTable; 
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, Double> colPrice;

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        try {
            if (inventoryTable != null) {
                setupTable();
                loadInventory();
                if (searchField != null) setupSearchFilter();
            }
            setupWindowDragging();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. THE SMALL SIGN SETTING
     * Opens a compact window for personal account settings (Password, etc.)
     */
    @FXML
    private void showUserProfileSettings() {
        openSettingPopup("/anagno/view/settings.fxml");
    }

    /**
     * 2. THE MERCHANT PURPOSE SETTING
     * This is triggered when you click your "Market Settings" button/link.
     */
    @FXML
    private void showMerchantBusinessSettings() {
        openSettingPopup("/anagno/view/merchant_settings.fxml");
    }

    /**
     * Reusable logic to open any setting as a professional, 
     * undecorated, draggable popup with shadow effects.
     */
    private void openSettingPopup(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);
            
            // Professional Styling: Shadow, Radius, and Border
            root.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 0, 5); " +
                         "-fx-background-color: white; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #E2E8F0;");

            // Make the popup draggable
            final double[] offset = {0, 0};
            root.setOnMousePressed(e -> {
                offset[0] = e.getSceneX();
                offset[1] = e.getSceneY();
            });
            root.setOnMouseDragged(e -> {
                popupStage.setX(e.getScreenX() - offset[0]);
                popupStage.setY(e.getScreenY() - offset[1]);
            });

            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
            
            // Auto-refresh inventory in case business name or data changed
            loadInventory(); 
            
        } catch (IOException e) {
            showErrorAlert("UI Error", "Could not load the settings window.");
            e.printStackTrace();
        }
    }

    /**
     * Compatibility wrapper for existing FXMLs using onAction="#showSettings"
     */
    @FXML
    private void showSettings() {
        showMerchantBusinessSettings();
    }

    // --- Database & Table Logic ---

    private void setupTable() {
        if (colName != null) colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (colStock != null) colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        if (colPrice != null) colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventoryTable.setPlaceholder(new Label("Inventory is empty."));
    }

    private void setupSearchFilter() {
        FilteredList<Product> filteredData = new FilteredList<>(productList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return product.getName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(inventoryTable.comparatorProperty());
        inventoryTable.setItems(sortedData);
    }

    @FXML
    public void loadInventory() {
        ObservableList<Product> tempItems = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY id DESC")) {
            while (rs.next()) {
                tempItems.add(new Product(rs.getInt("id"), rs.getString("name"), rs.getInt("stock"), rs.getDouble("price"), rs.getString("status")));
            }
            productList.setAll(tempItems);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showAddProductPopup() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/anagno/view/add_product.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadInventory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        Product selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
                pstmt.setInt(1, selected.getId());
                pstmt.executeUpdate();
                loadInventory();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupWindowDragging() {
        if (parentContainer != null) {
            parentContainer.setOnMousePressed(e -> { xOffset = e.getSceneX(); yOffset = e.getSceneY(); });
            parentContainer.setOnMouseDragged(e -> {
                Stage stage = (Stage) parentContainer.getScene().getWindow();
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML private void handleClose() { System.exit(0); }
    @FXML private void handleMinimize() { if (parentContainer != null) ((Stage) parentContainer.getScene().getWindow()).setIconified(true); }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            SessionManager.clearSession();
            Parent root = FXMLLoader.load(getClass().getResource("/anagno/view/login.fxml"));
            ((Stage) parentContainer.getScene().getWindow()).getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }
}