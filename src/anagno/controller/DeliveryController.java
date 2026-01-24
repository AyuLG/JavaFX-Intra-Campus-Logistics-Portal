package anagno.controller;

import anagno.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import java.io.IOException;

public class DeliveryController {

    @FXML private Label studentNameLabel;
    @FXML private VBox parentContainer;
    
    // UI Table Components mapped from delivery.fxml 
    @FXML private TableView<Job> deliveryTable;
    @FXML private TableColumn<Job, String> colFood;
    @FXML private TableColumn<Job, String> colLocation;
    @FXML private TableColumn<Job, String> colStatus;

    private ObservableList<Job> jobData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Retrieve current dispatcher name from session
        if (SessionManager.getCurrentUser() != null) {
            studentNameLabel.setText("Dispatcher: " + SessionManager.getCurrentUser());
        }
        
        // FIXED: Using PropertyValueFactory for correct data binding
        colFood.setCellValueFactory(new PropertyValueFactory<>("food"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Ensure columns fill the width of the table
        deliveryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        refreshJobs();
    }

    /**
     * Opens the Account Settings popup in a modal window.
     * Matches the uniform styling and dragging logic used across the system.
     */
    @FXML
    private void handleOpenSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/anagno/view/settings.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); 
            popupStage.initStyle(StageStyle.UNDECORATED);       
            
            // Logic to allow moving the undecorated window
            final double[] xOffset = {0};
            final double[] yOffset = {0};
            root.setOnMousePressed(e -> {
                xOffset[0] = e.getSceneX();
                yOffset[0] = e.getSceneY();
            });
            root.setOnMouseDragged(e -> {
                popupStage.setX(e.getScreenX() - xOffset[0]);
                popupStage.setY(e.getScreenY() - yOffset[0]);
            });

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); 
            popupStage.setScene(scene);
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showSimpleAlert(Alert.AlertType.ERROR, "System Error", "Could not load settings.fxml");
        }
    }

    @FXML
    private void refreshJobs() {
        jobData.clear();
        // Mock data representing active assignments
        jobData.add(new Job("Ertib", "DOrm 233", "READY"));
        jobData.add(new Job("Juice", "Dorm 230", "IN TRANSIT"));
        jobData.add(new Job("Fruit Platter", "Dorm 228", "PENDING"));
        
        deliveryTable.setItems(jobData);
    }

    @FXML
    private void handleStartDelivery(ActionEvent event) {
        Job selected = deliveryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showSimpleAlert(Alert.AlertType.INFORMATION, "Success", "Pickup confirmed for: " + selected.getFood());
        } else {
            showSimpleAlert(Alert.AlertType.WARNING, "No Selection", "Please select a job from the table first.");
        }
    }

    @FXML
    private void handleConfirmArrived(ActionEvent event) {
        Job selected = deliveryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showSimpleAlert(Alert.AlertType.INFORMATION, "Arrival Confirmed", "Customer notified of arrival at: " + selected.getLocation());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.clearSession();
        switchScene(event, "/anagno/view/login.fxml");
    }

    @FXML 
    private void handleClose(ActionEvent event) { 
        System.exit(0); 
    }

    @FXML
    private void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) parentContainer.getScene().getWindow();
        stage.setIconified(true);
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Maintain the system-wide standard portal size
            stage.setScene(new Scene(root, 420, 700));
            stage.show();
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

    private void showSimpleAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Anagno Dispatch");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Inner data model for the Delivery TableView rows.
     */
    public static class Job {
        private final SimpleStringProperty food;
        private final SimpleStringProperty location;
        private final SimpleStringProperty status;

        public Job(String food, String location, String status) {
            this.food = new SimpleStringProperty(food);
            this.location = new SimpleStringProperty(location);
            this.status = new SimpleStringProperty(status);
        }

        public String getFood() { return food.get(); }
        public String getLocation() { return location.get(); }
        public String getStatus() { return status.get(); }
    }
}