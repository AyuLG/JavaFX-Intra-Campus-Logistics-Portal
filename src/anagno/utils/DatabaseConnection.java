package anagno.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to manage MySQL database connections for Anagno University.
 */
public class DatabaseConnection {
    // FIXED: Changed 'anagno_db' to 'anagno_university' to match your DB
    private static final String URL = "jdbc:mysql://localhost:3306/anagno_university";
    private static final String USER = "root";
    private static final String PASS = ""; // Default for XAMPP is empty

    public static Connection getConnection() throws SQLException {
        try {
            // Loading the modern MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            return DriverManager.getConnection(URL, USER, PASS);
            
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found! Ensure mysql-connector-j-x.x.x.jar is in your Libraries.");
            throw new SQLException(e);
        }
    }
}