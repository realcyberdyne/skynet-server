package com.cyberdyne.skynet.Services.VPN.Functions;

import com.cyberdyne.skynet.Services.VPN.Config.Config;
import com.cyberdyne.skynet.connection.manager.DTO.ConnectionDTO;
import com.cyberdyne.skynet.connection.manager.Models.Connectin_Models;
import com.cyberdyne.skynet.connection.manager.Statics.Statics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * VPN Handshake Server - Refactored Version
 *
 * This server handles initial client authentication and key registration.
 *
 * Process:
 * 1. Client connects and sends connection hash
 * 2. Server looks up hash in database to get encryption key
 * 3. Server validates protocol (T600)
 * 4. Server stores IP → Key mapping in Statics
 * 5. Client can now use the main VPN server with encryption
 *
 * This must run BEFORE the main VPN server (EncVPNCoreAutoKey)
 * so that keys are registered before encrypted connections arrive.
 *
 * @author Cyberdyne Skynet
 * @version 2.0 - Refactored with thread pool and better error handling
 */
public class VPNHandShakeAI
{
    // ==================== CONFIGURATION ====================

    /**
     * Thread pool for handling multiple handshake connections concurrently.
     * More efficient than creating a new thread for each connection.
     */
    private final ExecutorService threadPool;

    /**
     * Server socket for accepting handshake connections.
     */
    private ServerSocket handshakeServerSocket;

    /**
     * Flag to control server running state.
     */
    private volatile boolean running = true;

    /**
     * Port for handshake connections (from Config.VPNTPort).
     */
    private final int handshakePort;

    // ==================== CONSTRUCTOR ====================

    /**
     * Initializes and starts the handshake server.
     * Reads port from Config.VPNTPort.
     */
    public VPNHandShakeAI()
    {
        // Get port from configuration
        this.handshakePort = Config.VPNTPort;

        // Create thread pool for better resource management
        this.threadPool = Executors.newCachedThreadPool();

        // Start the handshake server in a separate thread
        startHandshakeServer();
    }

    // ==================== SERVER STARTUP ====================

    /**
     * Starts the handshake server in a background thread.
     * This allows the main thread to continue and start other services.
     */
    private void startHandshakeServer()
    {
        new Thread(() -> {
            try
            {
                // Create server socket
                handshakeServerSocket = new ServerSocket(handshakePort);

                System.out.println("========================================");
                System.out.println("[HANDSHAKE SERVER] Started successfully");
                System.out.println("[HANDSHAKE SERVER] Listening on port: " + handshakePort);
                System.out.println("[HANDSHAKE SERVER] Waiting for client authentication...");
                System.out.println("========================================");

                // Main accept loop - run until stopped
                while (running)
                {
                    try
                    {
                        // Block until a client connects
                        Socket shakedSocket = handshakeServerSocket.accept();
                        String clientIP = shakedSocket.getInetAddress().getHostAddress();

                        System.out.println("[HANDSHAKE] New connection from IP: " + clientIP);

                        // Handle handshake in thread pool (not creating new thread each time)
                        threadPool.submit(() -> HandShakeSocket(shakedSocket, clientIP));
                    }
                    catch (Exception e)
                    {
                        if (running) {
                            System.err.println("[HANDSHAKE ERROR] Failed to accept connection: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                System.err.println("[FATAL] Handshake server initialization failed: " + e.getMessage());
                e.printStackTrace();
                threadPool.shutdown();
            }
        }).start();
    }

    // ==================== HANDSHAKE HANDLING ====================

    /**
     * Handles a single handshake connection from a client.
     *
     * Process:
     * 1. Read connection hash from client
     * 2. Look up hash in database to get connection details
     * 3. Validate protocol is "T600"
     * 4. Extract encryption key
     * 5. Store IP → Key mapping in Statics
     * 6. Send success response
     * 7. Close connection
     *
     * @param shakedSocket The socket connected to the client
     * @param clientIP The client's IP address (for logging)
     */
    private void HandShakeSocket(Socket shakedSocket, String clientIP)
    {
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try
        {
            // Get input/output streams
            dis = new DataInputStream(shakedSocket.getInputStream());
            dos = new DataOutputStream(shakedSocket.getOutputStream());

            // ==================== READ HASH ====================

            System.out.println("[HANDSHAKE] Reading authentication hash from " + clientIP);

            // Read connection hash from client
            String hash = dis.readUTF();

            if (hash == null || hash.isEmpty()) {
                System.err.println("[HANDSHAKE ERROR] Received null/empty hash from " + clientIP);
                dos.writeUTF("ERROR: Invalid hash");
                dos.flush();
                return;
            }

            System.out.println("[HANDSHAKE] Received hash from " + clientIP + ": " +
                    (hash.length() > 16 ? hash.substring(0, 16) + "..." : hash));

            // ==================== DATABASE LOOKUP ====================

            // Look up connection details in database using the hash
            Connectin_Models connection = new ConnectionDTO().GetConnectionByHash(hash);

            // Validate connection exists
            if (connection == null) {
                System.err.println("[HANDSHAKE ERROR] No connection found for hash from " + clientIP);
                System.err.println("[HANDSHAKE ERROR] Hash may be invalid or expired");
                dos.writeUTF("ERROR: Invalid or expired connection hash");
                dos.flush();
                return;
            }

            // ==================== VALIDATE PROTOCOL ====================

            String protocol = connection.getProtocol();

            if (protocol == null || !protocol.equals("T600")) {
                System.err.println("[HANDSHAKE ERROR] Invalid protocol for " + clientIP + ": " + protocol);
                System.err.println("[HANDSHAKE ERROR] Expected: T600, Got: " + protocol);
                dos.writeUTF("ERROR: Invalid protocol");
                dos.flush();
                return;
            }

            System.out.println("[HANDSHAKE] Valid protocol (T600) for " + clientIP);

            // ==================== EXTRACT AND VALIDATE KEY ====================

            String encryptionKey = connection.getKey();

            if (encryptionKey == null || encryptionKey.isEmpty()) {
                System.err.println("[HANDSHAKE ERROR] Connection found but key is null/empty for " + clientIP);
                dos.writeUTF("ERROR: Invalid encryption key");
                dos.flush();
                return;
            }

            System.out.println("[HANDSHAKE] Found encryption key for " + clientIP + ": " +
                    (encryptionKey.length() > 8 ? encryptionKey.substring(0, 8) + "..." : "SHORT_KEY"));

            // ==================== STORE KEY MAPPING ====================

            // Store IP → Key mapping in Statics for later use by VPN server
            // The new thread-safe Statics class will handle this properly
            Statics.SetConnection(clientIP, encryptionKey, protocol);

            System.out.println("[HANDSHAKE SUCCESS] ✅ Registered " + clientIP + " with encryption key");
            System.out.println("[HANDSHAKE SUCCESS] Client can now connect to VPN proxy server");

            // Send success response to client
            dos.writeUTF("SUCCESS");
            dos.flush();

            // ==================== STATISTICS ====================

            // Print current status
            System.out.println("[HANDSHAKE] Total registered clients: " + Statics.GetConnectionCount());
            System.out.println("[HANDSHAKE] All registered IPs: " + Statics.GetAllIPs());

        }
        catch (Exception e)
        {
            System.err.println("[HANDSHAKE ERROR] Failed to process handshake for " + clientIP);
            System.err.println("[HANDSHAKE ERROR] Exception: " + e.getMessage());
            e.printStackTrace();

            // Try to send error response to client
            try {
                if (dos != null) {
                    dos.writeUTF("ERROR: " + e.getMessage());
                    dos.flush();
                }
            } catch (Exception ignored) {
                // Ignore if we can't send error response
            }
        }
        finally
        {
            // ==================== CLEANUP ====================

            // Always close resources to prevent memory leaks
            try {
                if (dis != null) {
                    dis.close();
                }
                if (dos != null) {
                    dos.close();
                }
                if (shakedSocket != null && !shakedSocket.isClosed()) {
                    shakedSocket.close();
                }
                System.out.println("[HANDSHAKE] Closed connection with " + clientIP);
            } catch (Exception e) {
                System.err.println("[HANDSHAKE ERROR] Error closing connection: " + e.getMessage());
            }
        }
    }

    // ==================== LIFECYCLE MANAGEMENT ====================

    /**
     * Stops the handshake server and releases all resources.
     * Call this when shutting down the VPN system.
     */
    public void shutdown()
    {
        System.out.println("[HANDSHAKE] Shutting down handshake server...");

        running = false;

        try {
            if (handshakeServerSocket != null && !handshakeServerSocket.isClosed()) {
                handshakeServerSocket.close();
            }
        } catch (Exception e) {
            System.err.println("[HANDSHAKE ERROR] Error closing server socket: " + e.getMessage());
        }

        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
        }

        System.out.println("[HANDSHAKE] Server shut down successfully");
    }

    // ==================== STATUS & MONITORING ====================

    /**
     * Checks if the handshake server is running.
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning()
    {
        return running && handshakeServerSocket != null && !handshakeServerSocket.isClosed();
    }

    /**
     * Gets the port the handshake server is listening on.
     *
     * @return The handshake port number
     */
    public int getHandshakePort()
    {
        return handshakePort;
    }

    /**
     * Prints detailed status information.
     */
    public void printStatus()
    {
        System.out.println("========================================");
        System.out.println("[HANDSHAKE STATUS]");
        System.out.println("  Running: " + isRunning());
        System.out.println("  Port: " + handshakePort);
        System.out.println("  Registered clients: " + Statics.GetConnectionCount());
        System.out.println("  Registered IPs: " + Statics.GetAllIPs());
        System.out.println("========================================");
    }
}