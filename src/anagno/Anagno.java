package anagno;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;

/**
 * Main entry point for the Anagno University Application.
 * Updated to ensure path reliability for FXML loading.
 */
public class Anagno extends Application {
    
    // Window dragging coordinates
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) {
        try {
            // 1. FIXED RESOURCE LOADING
            // Using absolute path from the root of the classpath
            String fxmlPath = "/anagno/view/login.fxml";
            URL fxmlLocation = getClass().getResource(fxmlPath);
            
            if (fxmlLocation == null) {
                // If the first attempt fails, try a relative path search as a fallback
                fxmlLocation = Anagno.class.getClassLoader().getResource("anagno/view/login.fxml");
            }

            if (fxmlLocation == null) {
                System.err.println("--- CRITICAL ERROR ---");
                System.err.println("Target File: " + fxmlPath);
                System.err.println("Error: File not found. Please verify the file is in: src/anagno/view/login.fxml");
                return;
            }

            // 2. LOAD FXML
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // 3. WINDOW STYLING
            // Removes standard OS title bar
            stage.initStyle(StageStyle.UNDECORATED); 
            
            // 4. DRAGGING LOGIC
            // Allows movement of the undecorated window
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            
            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

            // 5. SCENE SETUP
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); // Required for rounded stage corners
            
            stage.setTitle("Anagno University - Login");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            
        } catch (Exception e) {
            System.err.println("--- RUNTIME ERROR ---");
            System.err.println("An error occurred while loading the Login interface. Check if the Controller path in FXML is correct.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}