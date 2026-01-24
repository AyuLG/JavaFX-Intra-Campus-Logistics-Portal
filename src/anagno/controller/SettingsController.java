package anagno.controller;

import anagno.utils.DatabaseConnection;
import anagno.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SettingsController {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;

    @FXML
    private void handleSavePassword() {
        String newPass = newPasswordField.getText();
        String currentUser = SessionManager.getCurrentUser(); // Assuming you store username here

        if (newPass.isEmpty() || newPass.length() < 4) {
            showError("Invalid Password", "Password must be at least 4 characters.");
            return;
        }

        String query = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, newPass);
            pstmt.setString(2, currentUser);
            
            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                showInfo("Success", "Password updated successfully!");
                handleClose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Error", "Could not update password.");
        }
    }

    @FXML
    private void handleClose() {
        // Get the current stage from any element (like the password field) and close it
        Stage stage = (Stage) currentPasswordField.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}