package com.cyberdyne.skynet.connection.manager.Statics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;

/**
 * Thread-safe static manager for IP-based VPN connections and encryption keys.
 * Uses ConcurrentHashMap for fast O(1) lookups and thread safety.
 *
 * @author Cyberdyne Skynet
 * @version 2.0 - Optimized for low-resource servers
 */
public class Statics
{
    // ==================== STORAGE ====================

    /**
     * Thread-safe map storing IP connections with encryption keys.
     * ConcurrentHashMap allows multiple threads to read/write safely.
     * Key: IP address (String)
     * Value: IpConnection_Model containing key and protocol info
     */
    private static final ConcurrentHashMap<String, IpConnection_Model> ipConnectionMap =
            new ConcurrentHashMap<>();

    /**
     * Deprecated: Kept for backward compatibility with legacy code.
     * New code should use the methods below instead of direct access.
     */
    @Deprecated
    public static ArrayList<IpConnection_Model> IpConnection = new ArrayList<>();

    // ==================== KEY MANAGEMENT ====================

    /**
     * Retrieves the encryption key for a given IP address.
     * Thread-safe and optimized for O(1) lookup performance.
     *
     * @param IP The client's IP address
     * @return The encryption key, or null if not found
     */
    public static String GetKey(String IP)
    {
        // Validate input
        if (IP == null || IP.isEmpty()) {
            System.err.println("[WARNING] GetKey called with null or empty IP address");
            return null;
        }

        // Retrieve connection from map (O(1) operation)
        IpConnection_Model connection = ipConnectionMap.get(IP);

        // Handle missing connection
        if (connection == null) {
            System.err.println("[ERROR] No encryption key found for IP: " + IP);
            System.err.println("[DEBUG] Currently registered IPs: " + ipConnectionMap.keySet());
            return null; // Return null instead of empty string for proper error handling
        }

        // Retrieve and validate key
        String key = connection.getKey();

        if (key == null || key.isEmpty()) {
            System.err.println("[ERROR] Connection exists but key is null/empty for IP: " + IP);
            return null;
        }

        // Log successful retrieval (truncate key for security)
        System.out.println("[SUCCESS] Retrieved encryption key for IP: " + IP +
                " (Key preview: " + key.substring(0, Math.min(8, key.length())) + "...)");

        return key;
    }

    /**
     * Stores or updates a connection with encryption key and protocol.
     * Thread-safe operation that can be called from multiple threads.
     *
     * @param IP The client's IP address
     * @param Key The encryption key to store
     * @param Protocol The VPN protocol being used (e.g., "OpenVPN", "WireGuard")
     */
    public static void SetConnection(String IP, String Key, String Protocol)
    {
        // Validate IP address
        if (IP == null || IP.isEmpty()) {
            System.err.println("[WARNING] Cannot store connection: IP is null or empty");
            return;
        }

        // Validate encryption key
        if (Key == null || Key.isEmpty()) {
            System.err.println("[WARNING] Storing connection with null/empty key for IP: " + IP);
        }

        // Create new connection model
        IpConnection_Model connection = new IpConnection_Model(IP, Key, Protocol);

        // Store in thread-safe map (automatically replaces if exists)
        IpConnection_Model previousConnection = ipConnectionMap.put(IP, connection);

        // Log the operation
        if (previousConnection != null) {
            System.out.println("[UPDATE] Updated connection for IP: " + IP +
                    " (Protocol: " + Protocol + ")");
        } else {
            System.out.println("[SUCCESS] Stored new connection for IP: " + IP +
                    " (Protocol: " + Protocol + ", Key preview: " +
                    (Key != null && Key.length() > 8 ? Key.substring(0, 8) + "..." : "N/A") + ")");
        }

        // Also add to legacy ArrayList for backward compatibility
        synchronized(IpConnection) {
            // Remove old entry if exists
            IpConnection.removeIf(conn -> conn.getIp().equals(IP));
            // Add new entry
            IpConnection.add(connection);
        }
    }

    /**
     * Removes a connection from storage.
     * Use this when a client disconnects to free memory.
     *
     * @param IP The IP address to remove
     * @return true if connection was removed, false if it didn't exist
     */
    public static boolean RemoveConnection(String IP)
    {
        // Validate input
        if (IP == null || IP.isEmpty()) {
            System.err.println("[WARNING] RemoveConnection called with null/empty IP");
            return false;
        }

        // Remove from map
        IpConnection_Model removed = ipConnectionMap.remove(IP);

        // Remove from legacy ArrayList
        synchronized(IpConnection) {
            IpConnection.removeIf(conn -> conn.getIp().equals(IP));
        }

        // Log result
        if (removed != null) {
            System.out.println("[SUCCESS] Removed connection for IP: " + IP);
            return true;
        } else {
            System.out.println("[INFO] No connection found to remove for IP: " + IP);
            return false;
        }
    }

    // ==================== CONNECTION QUERIES ====================

    /**
     * Retrieves the complete connection model for an IP address.
     *
     * @param IP The client's IP address
     * @return The IpConnection_Model object, or null if not found
     */
    public static IpConnection_Model GetConnection(String IP)
    {
        if (IP == null || IP.isEmpty()) {
            return null;
        }
        return ipConnectionMap.get(IP);
    }

    /**
     * Retrieves the protocol being used for a specific IP.
     *
     * @param IP The client's IP address
     * @return The protocol name, or null if connection not found
     */
    public static String GetProtocol(String IP)
    {
        IpConnection_Model connection = ipConnectionMap.get(IP);
        return connection != null ? connection.getProtocol() : null;
    }

    /**
     * Checks if a connection exists for the given IP address.
     *
     * @param IP The IP address to check
     * @return true if connection exists, false otherwise
     */
    public static boolean HasConnection(String IP)
    {
        return IP != null && !IP.isEmpty() && ipConnectionMap.containsKey(IP);
    }

    // ==================== STATISTICS & DEBUGGING ====================

    /**
     * Returns all currently registered IP addresses.
     * Useful for debugging and monitoring.
     *
     * @return Set of all IP addresses with active connections
     */
    public static Set<String> GetAllIPs()
    {
        return ipConnectionMap.keySet();
    }

    /**
     * Returns the total number of active connections.
     *
     * @return Count of stored connections
     */
    public static int GetConnectionCount()
    {
        return ipConnectionMap.size();
    }

    /**
     * Prints detailed statistics about all connections.
     * For debugging and monitoring purposes.
     */
    public static void PrintConnectionStats()
    {
        System.out.println("========== CONNECTION STATISTICS ==========");
        System.out.println("Total connections: " + ipConnectionMap.size());
        System.out.println("Registered IPs:");

        for (Map.Entry<String, IpConnection_Model> entry : ipConnectionMap.entrySet()) {
            IpConnection_Model conn = entry.getValue();
            System.out.println("  - IP: " + entry.getKey() +
                    " | Protocol: " + conn.getProtocol() +
                    " | Has Key: " + (conn.getKey() != null && !conn.getKey().isEmpty()));
        }

        System.out.println("===========================================");
    }

    // ==================== MAINTENANCE ====================

    /**
     * Removes all invalid connections (null or empty keys).
     * Call this periodically to clean up corrupted entries and free memory.
     *
     * @return Number of connections removed
     */
    public static int CleanupInvalidConnections()
    {
        System.out.println("[MAINTENANCE] Starting cleanup of invalid connections...");

        int removedCount = 0;

        // Find and remove invalid connections
        for (Map.Entry<String, IpConnection_Model> entry : ipConnectionMap.entrySet()) {
            IpConnection_Model conn = entry.getValue();
            boolean isInvalid = conn == null ||
                    conn.getKey() == null ||
                    conn.getKey().isEmpty();

            if (isInvalid) {
                ipConnectionMap.remove(entry.getKey());
                System.out.println("[CLEANUP] Removed invalid connection for IP: " + entry.getKey());
                removedCount++;
            }
        }

        // Also clean legacy ArrayList
        synchronized(IpConnection) {
            IpConnection.removeIf(conn ->
                    conn == null || conn.getKey() == null || conn.getKey().isEmpty()
            );
        }

        System.out.println("[MAINTENANCE] Cleanup completed. Removed " + removedCount + " invalid connections.");
        System.out.println("[MAINTENANCE] Remaining connections: " + ipConnectionMap.size());

        return removedCount;
    }

    /**
     * Removes all connections from storage.
     * WARNING: Use with caution! This will disconnect all active clients.
     */
    public static void ClearAll()
    {
        int count = ipConnectionMap.size();
        ipConnectionMap.clear();

        synchronized(IpConnection) {
            IpConnection.clear();
        }

        System.out.println("[WARNING] Cleared all " + count + " connections from memory");
    }

    /**
     * Checks system health and reports any issues.
     * Useful for monitoring in production environments.
     *
     * @return true if system is healthy, false if issues detected
     */
    public static boolean CheckSystemHealth()
    {
        boolean healthy = true;

        System.out.println("[HEALTH CHECK] Starting system health check...");

        // Check for null connections
        long nullConnections = ipConnectionMap.values().stream()
                .filter(conn -> conn == null)
                .count();

        if (nullConnections > 0) {
            System.err.println("[HEALTH CHECK] WARNING: Found " + nullConnections + " null connections");
            healthy = false;
        }

        // Check for connections with invalid keys
        long invalidKeys = ipConnectionMap.values().stream()
                .filter(conn -> conn != null && (conn.getKey() == null || conn.getKey().isEmpty()))
                .count();

        if (invalidKeys > 0) {
            System.err.println("[HEALTH CHECK] WARNING: Found " + invalidKeys + " connections with invalid keys");
            healthy = false;
        }

        // Check synchronization between map and ArrayList
        if (ipConnectionMap.size() != IpConnection.size()) {
            System.err.println("[HEALTH CHECK] WARNING: Map size (" + ipConnectionMap.size() +
                    ") doesn't match ArrayList size (" + IpConnection.size() + ")");
            healthy = false;
        }

        if (healthy) {
            System.out.println("[HEALTH CHECK] System is healthy. " +
                    ipConnectionMap.size() + " connections active.");
        } else {
            System.err.println("[HEALTH CHECK] System health check FAILED. Issues detected.");
        }

        return healthy;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Updates only the encryption key for an existing connection.
     * More efficient than SetConnection when only key needs updating.
     *
     * @param IP The IP address
     * @param newKey The new encryption key
     * @return true if updated successfully, false if connection doesn't exist
     */
    public static boolean UpdateKey(String IP, String newKey)
    {
        IpConnection_Model connection = ipConnectionMap.get(IP);

        if (connection == null) {
            System.err.println("[ERROR] Cannot update key: No connection found for IP: " + IP);
            return false;
        }

        connection.setKey(newKey);
        System.out.println("[SUCCESS] Updated encryption key for IP: " + IP);

        return true;
    }

    /**
     * Validates that a connection has all required fields.
     *
     * @param IP The IP address to validate
     * @return true if connection is valid, false otherwise
     */
    public static boolean ValidateConnection(String IP)
    {
        if (IP == null || IP.isEmpty()) {
            return false;
        }

        IpConnection_Model connection = ipConnectionMap.get(IP);

        if (connection == null) {
            return false;
        }

        return connection.getKey() != null &&
                !connection.getKey().isEmpty() &&
                connection.getProtocol() != null &&
                !connection.getProtocol().isEmpty();
    }
}