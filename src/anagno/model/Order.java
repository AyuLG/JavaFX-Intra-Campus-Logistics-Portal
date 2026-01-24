package anagno.model;

import javafx.beans.property.*;

public class Order {
    private final IntegerProperty id;
    private final StringProperty productName;
    private final IntegerProperty quantity;
    private final DoubleProperty totalPrice;
    private final StringProperty status;

    public Order(int id, String productName, int quantity, double totalPrice, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.totalPrice = new SimpleDoubleProperty(totalPrice);
        this.status = new SimpleStringProperty(status);
    }

    // --- GETTERS (Fixes the red lines in MerchantController) ---
    
    public int getId() { 
        return id.get(); 
    }

    public String getProductName() { 
        return productName.get(); 
    }

    public int getQuantity() { 
        return quantity.get(); 
    }

    public String getStatus() { 
        return status.get(); 
    }

    public double getTotalPrice() { 
        return totalPrice.get(); 
    }

    // --- PROPERTY METHODS (Required for TableView columns) ---
    
    public IntegerProperty idProperty() { return id; }
    public StringProperty productNameProperty() { return productName; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty totalPriceProperty() { return totalPrice; }
    public StringProperty statusProperty() { return status; }
}