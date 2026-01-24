package anagno.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import anagno.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddProductController {

    // These IDs must match the fx:id in your add_product.fxml
    @FXML private VBox popupContainer; 
    @FXML private TextField nameField;
    @FXML private TextField stockField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> categoryBox;

    @FXML
    public void initialize() {
        // Populates the category box if it exists in your FXML
        if (categoryBox != null) {
            categoryBox.getItems().addAll("Traditional", "Pasta", "Beverages", "Snacks");
        }
    }

    @FXML
    private void handleSave() {
        // Validate inputs to prevent empty database entries
        if (nameField.getText().isEmpty() || stockField.getText().isEmpty() || priceField.getText().isEmpty()) {
            System.err.println("Validation Error: All fields are required.");
            return; 
        }

        // Note: Adding 'category' to the SQL if you decide to use categoryBox
        String query = "INSERT INTO products (name, stock, price, status) VALUES (?, ?, ?, 'Available')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, nameField.getText());
            pstmt.setInt(2, Integer.parseInt(stockField.getText()));
            pstmt.setDouble(3, Double.parseDouble(priceField.getText()));
            
            pstmt.executeUpdate();
            handleClose(); // Close the popup window after successful save
            
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error saving product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        // This is critical for the "✕" button in your popup
        Stage stage = (Stage) popupContainer.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMinimize() {
        // This is critical for the "—" button in your popup
        Stage stage = (Stage) popupContainer.getScene().getWindow();
        stage.setIconified(true);
    }
    
    @FXML
    private void handleCancel() {
        handleClose();
    }
}