package anagno.model;

import javafx.beans.property.*;

/**
 * Optimized Model class using JavaFX Properties.
 * This ensures the TableView automatically syncs with data changes.
 */
public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final IntegerProperty stock;
    private final DoubleProperty price;
    private final StringProperty status;

    // Full Constructor
    public Product(int id, String name, int stock, double price, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.stock = new SimpleIntegerProperty(stock);
        this.price = new SimpleDoubleProperty(price);
        this.status = new SimpleStringProperty(status);
    }

    // Overloaded Constructor for quick database fetches
    public Product(int id, String name, int stock, double price) {
        this(id, name, stock, price, "Available");
    }

    // --- Property Getters (Internal JavaFX Logic) ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public IntegerProperty stockProperty() { return stock; }
    public DoubleProperty priceProperty() { return price; }
    public StringProperty statusProperty() { return status; }

    // --- Standard Getters (Used by PropertyValueFactory) ---
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public int getStock() { return stock.get(); }
    public double getPrice() { return price.get(); }
    public String getStatus() { return status.get(); }

    // --- Standard Setters (Allows updating values) ---
    public void setId(int value) { this.id.set(value); }
    public void setName(String value) { this.name.set(value); }
    public void setStock(int value) { this.stock.set(value); }
    public void setPrice(double value) { this.price.set(value); }
    public void setStatus(String value) { this.status.set(value); }
}