package anagno.utils;

/**
 * Global Session Manager for Anagno University.
 * This class persists the logged-in user's data across different scenes
 * (Login -> Merchant/Delivery/Taker).
 */
public class SessionManager {
    
    // Static variables stay in memory as long as the app is running
    private static String currentUser;
    private static String currentRole;

    /**
     * Called by LoginController upon successful authentication.
     */
    public static void setSession(String username, String role) {
        currentUser = username;
        currentRole = role;
    }

    /**
     * Used by Merchant and Delivery dashboards to show "Welcome, [User]"
     */
    public static String getCurrentUser() {
        return currentUser;
    }

    /**
     * CRITICAL: Used by controllers to verify permissions
     */
    public static String getCurrentRole() {
        return currentRole;
    }

    /**
     * Called when the "Logout" button is clicked.
     * Prevents the next user from seeing the previous session's data.
     */
    public static void clearSession() {
        currentUser = null;
        currentRole = null;
    }
}