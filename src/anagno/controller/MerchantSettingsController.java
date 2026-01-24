package anagno.controller;

import anagno.utils.DatabaseConnection;
import anagno.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MerchantSettingsController {

    @FXML private TextField marketNameField;
    @FXML private RadioButton statusAvailable;
    @FXML private ToggleGroup statusGroup;
    @FXML private Slider radiusSlider;

    @FXML
    private void handleSaveBusinessSettings() {
        String marketName = marketNameField.getText();
        String status = statusAvailable.isSelected() ? "OPEN" : "CLOSED";
        int radius = (int) radiusSlider.getValue();
        String merchantUser = SessionManager.getCurrentUser();

        String sql = "UPDATE users SET market_name = ?, status = ?, delivery_radius = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, marketName);
            pstmt.setString(2, status);
            pstmt.setInt(3, radius);
            pstmt.setString(4, merchantUser);
            
            pstmt.executeUpdate();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Business profile updated!");
            alert.showAndWait();
            handleClose();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered by the underline link in your FXML.
     * Opens the specific merchant_business_settings.fxml popup.
     */
    @FXML
    private void showMerchantBusinessSettings() {
        try {
            // 1. Load the new FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/anagno/view/merchant_business_settings.fxml"));
            Parent root = loader.load();
            
            // 2. Prepare the new Stage
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);
            
            // Apply professional styling
            root.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 0, 5); " +
                         "-fx-background-color: white; -fx-background-radius: 20;");

            // 3. Close the current "Store Configuration" window before showing the new one
            handleClose();

            popupStage.setScene(new Scene(root));
            popupStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        if (marketNameField != null && marketNameField.getScene() != null) {
            Stage stage = (Stage) marketNameField.getScene().getWindow();
            stage.close();
        }
    }
}