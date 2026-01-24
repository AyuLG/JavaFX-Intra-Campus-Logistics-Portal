package anagno.controller;

import anagno.utils.DatabaseConnection;
import anagno.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    // --- FXML Fields ---
    @FXML private VBox parentContainer;
    @FXML private Label errorLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    // Registration Specific Fields
    @FXML private TextField regUser;
    @FXML private PasswordField regPass;
    @FXML private ComboBox<String> roleBox;
    @FXML private ComboBox<String> genderBox;

    // Window Dragging coordinates
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        // Initialize ComboBoxes only if they exist in the current loaded FXML 
        if (roleBox != null) {
            roleBox.getItems().clear();
            roleBox.getItems().addAll("STUDENT", "MERCHANT", "CUSTOMER");
        }
        if (genderBox != null) {
            genderBox.getItems().clear();
            genderBox.getItems().addAll("MALE", "FEMALE");
        }
    }

    /**
     * Logic to open the add_product.fxml as a modal popup.
     */
    @FXML
    private void showAddProductPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/anagno/view/add_product.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); 
            popupStage.initStyle(StageStyle.UNDECORATED);       
            
            root.setOnMousePressed(e -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
            root.setOnMouseDragged(e -> {
                popupStage.setX(e.getScreenX() - xOffset);
                popupStage.setY(e.getScreenY() - yOffset);
            });

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); 
            popupStage.setScene(scene);
            popupStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error: Could not find add_product.fxml");
            e.printStackTrace();
        }
    }

    /**
     * Handles User Login and Routes to the correct Dashboard
     */
    @FXML
    public void handleLogin() {
        String user = (usernameField != null) ? usernameField.getText().trim() : "";
        String pass = (passwordField != null) ? passwordField.getText() : "";

        if (user.isEmpty() || pass.isEmpty()) {
            if (errorLabel != null) errorLabel.setText("Please enter your name and password.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT username, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                SessionManager.setSession(user, role); 

                String userRole = role.trim().toUpperCase();

                // Navigation Logic based on Role
                if (userRole.equals("MERCHANT")) {
                    navigateTo("/anagno/view/merchant.fxml", "Anagno - Merchant Dashboard");
                } else if (userRole.equals("CUSTOMER")) {
                    navigateTo("/anagno/view/taker_dashboard.fxml", "Anagno - Campus Marketplace");
                } else if (userRole.equals("STUDENT")) {
                    navigateTo("/anagno/view/delivery.fxml", "Anagno - Delivery Dispatch");
                } else {
                    if (errorLabel != null) errorLabel.setText("Role '" + role + "' not recognized.");
                }
            } else {
                if (errorLabel != null) errorLabel.setText("User not found or password incorrect.");
            }
        } catch (Exception e) {
            if (errorLabel != null) errorLabel.setText("Database error. Please try again.");
            e.printStackTrace();
        }
    }

    /**
     * Handles User Registration
     */
    @FXML
    public void handleRegister() {
        if (regUser.getText().isEmpty() || regPass.getText().isEmpty() || roleBox.getValue() == null) {
            if (errorLabel != null) errorLabel.setText("Please fill all fields.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO users (username, password, role, gender) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, regUser.getText().trim());
            pstmt.setString(2, regPass.getText());
            pstmt.setString(3, roleBox.getValue());
            pstmt.setString(4, genderBox.getValue());
            
            pstmt.executeUpdate();
            showLogin(); 
            
        } catch (Exception e) {
            e.printStackTrace();
            if (errorLabel != null) errorLabel.setText("Registration failed: " + e.getMessage());
        }
    }

    // --- Window Controls ---

    @FXML public void handleClose() { 
        System.exit(0); 
    }
    
    @FXML public void handleMinimize() { 
        if (parentContainer != null && parentContainer.getScene() != null) {
            Stage stage = (Stage) parentContainer.getScene().getWindow();
            stage.setIconified(true); 
        }
    }

    @FXML public void showRegistration() { 
        // FIXED: Pointing to register.fxml 
        navigateTo("/anagno/view/register.fxml", "Create Account"); 
    }
    
    @FXML public void showLogin() { 
        navigateTo("/anagno/view/login.fxml", "Login"); 
    }

    /**
     * Helper method to switch scenes.
     */
    private void navigateTo(String fxmlPath, String title) {
        try {
            if (parentContainer == null || parentContainer.getScene() == null) {
                System.err.println("Navigation Error: Parent container or scene is null.");
                return;
            }

            Stage stage = (Stage) parentContainer.getScene().getWindow();
            
            // Check if resource exists before loading
            URL res = getClass().getResource(fxmlPath);
            if (res == null) {
                throw new IOException("FXML file not found at: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(res);
            Parent root = loader.load();
            
            root.setOnMousePressed(e -> { 
                xOffset = e.getSceneX(); 
                yOffset = e.getSceneY(); 
            });
            root.setOnMouseDragged(e -> { 
                stage.setX(e.getScreenX() - xOffset); 
                stage.setY(e.getScreenY() - yOffset); 
            });

            // Adjusting dimensions for consistent mobile-app feel
            Scene scene = new Scene(root, 420, 700);
            scene.setFill(Color.TRANSPARENT); 
            
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen(); 
            stage.show();

        } catch (IOException e) {
            System.err.println("IO Error: Check FXML path spelling. Requested: " + fxmlPath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Controller Error: Initialization failed for " + fxmlPath);
            e.printStackTrace();
        }
    }
}