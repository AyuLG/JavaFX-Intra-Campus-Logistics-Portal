package anagno.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;

public class PaymentController {

    @FXML private VBox parentContainer;
    @FXML private Label subtotalLabel;
    @FXML private Label finalTotalLabel;
    
    // Payment Method Buttons
    @FXML private ToggleButton telebirrBtn;
    @FXML private ToggleButton cbeBtn;
    @FXML private ToggleButton cashBtn;
    @FXML private ToggleGroup paymentGroup;

    private double amount = 0.0;

    /**
     * Call this from ShopViewController to pass the total price
     */
    public void setPaymentAmount(double subtotal) {
        this.amount = subtotal;
        if (subtotalLabel != null) {
            subtotalLabel.setText(String.format("%.2f ETB", subtotal));
            finalTotalLabel.setText(String.format("%.2f ETB", subtotal + 15.0)); // Total + Delivery
        }
    }

    /**
     * Logic to make buttons clickable and visually change style when selected
     */
    @FXML
    private void handlePaymentSelection(ActionEvent event) {
        // Style Definitions
        String defaultStyle = "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;";
        String selectedStyle = "-fx-background-color: white; -fx-border-color: #6366F1; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;";

        // Update styles based on which toggle is selected
        telebirrBtn.setStyle(telebirrBtn.isSelected() ? selectedStyle : defaultStyle);
        cbeBtn.setStyle(cbeBtn.isSelected() ? selectedStyle : defaultStyle);
        cashBtn.setStyle(cashBtn.isSelected() ? selectedStyle : defaultStyle);
        
        System.out.println("Payment method updated.");
    }

    @FXML
    private void handleConfirmOrder(ActionEvent event) {
        // 1. Disable the button to prevent double-clicks
        ((Node)event.getSource()).setDisable(true);

        // 2. Identify selected method for the message
        String method = "Order";
        if (paymentGroup != null && paymentGroup.getSelectedToggle() != null) {
            method = ((ToggleButton) paymentGroup.getSelectedToggle()).getText();
        }

        // 3. Show the notification
        showNotification("Success! Order placed via " + method + ".");

        // 4. Navigate back to dashboard after 3 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> handleBack(event));
        delay.play();
    }

    private void showNotification(String message) {
        Label notification = new Label(message);
        notification.setStyle(
            "-fx-background-color: #10B981; " + // Emerald Green
            "-fx-text-fill: white; " +
            "-fx-padding: 15 30; " +
            "-fx-background-radius: 10; " +
            "-fx-font-weight: bold; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);"
        );

        // Add to the top of the container (index 1 is usually below the header)
        if (parentContainer.getChildren().size() > 1) {
            parentContainer.getChildren().add(1, notification);
        } else {
            parentContainer.getChildren().add(notification);
        }

        PauseTransition hide = new PauseTransition(Duration.seconds(2.5));
        hide.setOnFinished(e -> parentContainer.getChildren().remove(notification));
        hide.play();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/anagno/view/taker_dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 420, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleMinimize(ActionEvent event) { 
        if (parentContainer.getScene() != null) {
            ((Stage) parentContainer.getScene().getWindow()).setIconified(true);
        }
    }
    
    @FXML private void handleClose(ActionEvent event) { System.exit(0); }
}