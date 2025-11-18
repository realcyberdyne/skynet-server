package com.cyberdyne.skynet.Services.VPN.Functions;

import com.cyberdyne.skynet.Services.VPN.Encription.EncriptionBytesCLS;
import com.cyberdyne.skynet.connection.manager.Statics.Statics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Encrypted VPN Core with Automatic Key Management
 *
 * This class implements a high-performance encrypted proxy server that:
 * - Accepts encrypted connections from clients
 * - Decrypts traffic using IP-based key lookup
 * - Forwards decrypted traffic to target servers
 * - Handles bidirectional data transfer
 *
 * Optimized for low-resource servers with limited RAM and CPU.
 * Thread-safe and designed to handle multiple concurrent connections.
 *
 * @author Cyberdyne Skynet
 * @version 2.0 - Fixed for low-resource environments
 */
public class EncVPNCoreAutoKey_AI
{
    // ==================== CONFIGURATION ====================

    /**
     * Thread pool for handling multiple client connections concurrently.
     * Uses cached thread pool which creates threads on demand and reuses them.
     * Automatically scales based on load.
     */
    private final ExecutorService threadPool;

    /**
     * Buffer size for encrypted data transfer (16KB).
     * Larger buffer = better performance but more memory usage.
     * Adjust based on server RAM availability.
     */
    private static final int ENCRYPTED_BUFFER_SIZE = 16384;

    /**
     * Buffer size for plain data transfer (8KB).
     * Used for target-to-client forwarding (no encryption).
     */
    private static final int PLAIN_BUFFER_SIZE = 8192;

    /**
     * Sleep interval in milliseconds when waiting for data.
     * Prevents CPU spinning while waiting for I/O.
     */
    private static final int SLEEP_INTERVAL_MS = 5;

    // ==================== CONSTRUCTOR ====================

    /**
     * Initializes and starts the encrypted VPN proxy server.
     *
     * @param Port The port number to listen on (e.g., 8080, 1080)
     */
    public EncVPNCoreAutoKey_AI(int Port)
    {
        // Create thread pool for concurrent connection handling
        threadPool = Executors.newCachedThreadPool();

        try
        {
            // Create server socket and bind to port
            ServerSocket Server = new ServerSocket(Port);
            System.out.println("[SERVER] Encrypted proxy server started successfully on port " + Port);
            System.out.println("[SERVER] Waiting for client connections...");

            // Main server loop - accept incoming connections
            while (true)
            {
                // Block until a client connects
                Socket request = Server.accept();
                String clientIP = request.getInetAddress().getHostAddress();
                System.out.println("[CONNECTION] New connection from IP: " + clientIP);

                // Handle each connection in a separate thread
                threadPool.submit(() -> {
                    try
                    {
                        HandleEncryptedRequest(request);
                    }
                    catch (Exception e)
                    {
                        System.err.println("[ERROR] Failed to handle request from " + clientIP + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        }
        catch (Exception e)
        {
            System.err.println("[FATAL] Server initialization error: " + e.getMessage());
            e.printStackTrace();
            threadPool.shutdown();
        }
    }

    // ==================== REQUEST HANDLING ====================

    /**
     * Handles an encrypted connection from a client.
     *
     * Process flow:
     * 1. Get encryption key based on client IP
     * 2. Wait for and decrypt initial CONNECT request
     * 3. Parse target host and port from request
     * 4. Establish connection to target server
     * 5. Send success response to client
     * 6. Start bidirectional data forwarding
     *
     * @param clientSocket The socket connected to the client
     */
    private void HandleEncryptedRequest(Socket clientSocket)
    {
        Socket targetSocket = null;
        String encryptionKey = null;
        String clientIP = clientSocket.getInetAddress().getHostAddress();

        try
        {
            // Get input/output streams for client communication
            InputStream clientInput = clientSocket.getInputStream();
            OutputStream clientOutput = clientSocket.getOutputStream();

            // ==================== KEY RETRIEVAL ====================

            // Get encryption key ONCE at the beginning
            // This avoids race conditions and memory issues
            encryptionKey = Statics.GetKey(clientIP);

            // Validate that encryption key exists
            if (encryptionKey == null || encryptionKey.isEmpty()) {
                System.err.println("[ERROR] No encryption key found for IP: " + clientIP);
                System.err.println("[ERROR] Client must authenticate first before using VPN");

                // Send unauthorized response
                String errorResponse = "HTTP/1.1 401 Unauthorized\r\n\r\n";
                clientOutput.write(errorResponse.getBytes());
                clientOutput.flush();
                clientSocket.close();
                return;
            }

            System.out.println("[AUTH] Successfully retrieved encryption key for IP: " + clientIP);

            // ==================== RECEIVE INITIAL REQUEST ====================

            // Wait for client to send first packet of data
            // This should be the encrypted CONNECT request
            while (clientInput.available() == 0) {
                Thread.sleep(SLEEP_INTERVAL_MS);
            }

            // Read all available data from the initial packet
            byte[] firstPacket = readAvailableData(clientInput);
            if (firstPacket.length == 0) {
                System.err.println("[ERROR] Received empty initial packet from " + clientIP);
                clientSocket.close();
                return;
            }

            System.out.println("[DATA] Received encrypted initial request: " + firstPacket.length + " bytes");

            // ==================== DECRYPT INITIAL REQUEST ====================

            byte[] decryptedFirstPacket;
            try {
                // Decrypt the CONNECT request using the encryption key
                decryptedFirstPacket = EncriptionBytesCLS.decrypt(firstPacket, encryptionKey);
                System.out.println("[DECRYPT] Successfully decrypted initial request (" +
                        firstPacket.length + " → " + decryptedFirstPacket.length + " bytes)");
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to decrypt initial request from " + clientIP);
                System.err.println("[ERROR] Possible causes: wrong key, corrupted data, or incompatible encryption");
                e.printStackTrace();
                clientSocket.close();
                return;
            }

            // ==================== PARSE CONNECT REQUEST ====================

            // Convert decrypted bytes to string and split by lines
            String decryptedRequest = new String(decryptedFirstPacket);
            String[] requestLines = decryptedRequest.split("\r\n");

            // Validate request format
            if (requestLines.length == 0) {
                System.err.println("[ERROR] Invalid request format after decryption");
                clientSocket.close();
                return;
            }

            // Parse first line: "CONNECT host:port HTTP/1.1"
            String[] firstLineParams = requestLines[0].split(" ");
            if (firstLineParams.length < 2 || !firstLineParams[0].toUpperCase().equals("CONNECT")) {
                System.err.println("[ERROR] Not a CONNECT request: " + requestLines[0]);

                // Send bad request response
                String errorResponse = "HTTP/1.1 400 Bad Request\r\n\r\n";
                clientOutput.write(errorResponse.getBytes());
                clientOutput.flush();
                clientSocket.close();
                return;
            }

            // Extract target host and port (e.g., "example.com:443")
            String[] hostParts = firstLineParams[1].split(":");
            String targetHost = hostParts[0];
            int targetPort = hostParts.length > 1 ? Integer.parseInt(hostParts[1]) : 443; // Default to HTTPS

            System.out.println("[TARGET] Attempting to connect to: " + targetHost + ":" + targetPort);

            // ==================== CONNECT TO TARGET SERVER ====================

            try {
                // Establish TCP connection to the target server
                targetSocket = new Socket(targetHost, targetPort);
                System.out.println("[SUCCESS] Connected to target server: " + targetHost + ":" + targetPort);
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to connect to target " + targetHost + ":" + targetPort);
                System.err.println("[ERROR] " + e.getMessage());

                // Send gateway error response
                String errorResponse = "HTTP/1.1 502 Bad Gateway\r\n\r\n";
                clientOutput.write(errorResponse.getBytes());
                clientOutput.flush();
                clientSocket.close();
                return;
            }

            // ==================== SEND SUCCESS RESPONSE ====================

            // Send HTTP 200 to client indicating tunnel is established
            String successResponse = "HTTP/1.1 200 Connection Established\r\n\r\n";
            clientOutput.write(successResponse.getBytes());
            clientOutput.flush();

            System.out.println("[TUNNEL] Tunnel established: Client <-> Proxy <-> " + targetHost + ":" + targetPort);

            // ==================== START DATA FORWARDING ====================

            // Create two threads for bidirectional data transfer:
            // 1. Client → Target (encrypted, needs decryption)
            // 2. Target → Client (plain, no encryption needed)

            Thread clientToTarget = decryptAndForward(clientSocket, targetSocket, encryptionKey);
            Thread targetToClient = forwardToClient(targetSocket, clientSocket);

            // Wait for both threads to complete (connection closed)
            clientToTarget.join();
            targetToClient.join();

            System.out.println("[DISCONNECT] Connection closed for " + clientIP);

        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error in proxy handling for " + clientIP + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ==================== CLEANUP ====================

            // Always close sockets to free resources
            try {
                if (targetSocket != null && !targetSocket.isClosed()) {
                    targetSocket.close();
                }
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Error closing sockets: " + e.getMessage());
            }
        }
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Reads all currently available data from an input stream.
     * Non-blocking operation that reads whatever is available now.
     *
     * @param inputStream The stream to read from
     * @return Byte array containing all available data
     * @throws IOException If read operation fails
     */
    private byte[] readAvailableData(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int available = inputStream.available();

        if (available > 0) {
            byte[] data = new byte[available];
            int bytesRead = inputStream.read(data);

            if (bytesRead > 0) {
                buffer.write(data, 0, bytesRead);
            }
        }

        return buffer.toByteArray();
    }

    // ==================== DATA FORWARDING THREADS ====================

    /**
     * Thread that decrypts data from client and forwards to target server.
     *
     * This handles the CLIENT → TARGET direction:
     * 1. Read encrypted data from client
     * 2. Decrypt using the encryption key
     * 3. Forward decrypted data to target server
     *
     * Runs in a loop until connection is closed.
     *
     * @param clientSocket Socket connected to the VPN client
     * @param targetSocket Socket connected to the target server
     * @param encryptionKey The encryption key for this connection (passed as parameter to avoid cache issues)
     * @return The started thread
     */
    private Thread decryptAndForward(Socket clientSocket, Socket targetSocket, String encryptionKey) {
        Thread thread = new Thread(() -> {
            String clientIP = clientSocket.getInetAddress().getHostAddress();

            try {
                InputStream clientInput = clientSocket.getInputStream();
                OutputStream targetOutput = targetSocket.getOutputStream();
                byte[] buffer = new byte[ENCRYPTED_BUFFER_SIZE];

                System.out.println("[FORWARD] Started Client → Target forwarding thread for " + clientIP);

                // Main forwarding loop
                while (!clientSocket.isClosed() && !targetSocket.isClosed()) {

                    // Check if data is available
                    if (clientInput.available() > 0) {

                        // Read encrypted data from client
                        int bytesRead = clientInput.read(buffer);
                        if (bytesRead <= 0) {
                            System.out.println("[INFO] Client closed connection (read returned " + bytesRead + ")");
                            break;
                        }

                        // Extract the actual data read (don't send entire buffer)
                        byte[] encryptedChunk = new byte[bytesRead];
                        System.arraycopy(buffer, 0, encryptedChunk, 0, bytesRead);

                        try {
                            // Decrypt the data using the stored encryption key
                            // Note: We use the key passed as parameter, NOT fetching it again
                            // This avoids race conditions and cache misses
                            byte[] decryptedData = EncriptionBytesCLS.decrypt(encryptedChunk, encryptionKey);

                            // Forward decrypted data to target server
                            targetOutput.write(decryptedData);
                            targetOutput.flush();

                            System.out.println("[DATA] Client → Target: Decrypted " + bytesRead +
                                    " bytes → " + decryptedData.length + " bytes");

                        } catch (Exception e) {
                            System.err.println("[ERROR] Decryption failed for data from " + clientIP);
                            System.err.println("[ERROR] " + e.getMessage());
                            e.printStackTrace();
                            break; // Stop forwarding on decryption error
                        }

                    } else {
                        // No data available - sleep briefly to prevent CPU spinning
                        Thread.sleep(SLEEP_INTERVAL_MS);
                    }
                }

                System.out.println("[INFO] Client → Target forwarding stopped for " + clientIP);

            } catch (Exception e) {
                System.err.println("[ERROR] Client → Target forwarding error for " + clientIP + ": " + e.getMessage());
            }
        });

        thread.start();
        return thread;
    }

    /**
     * Thread that forwards data from target server to client.
     *
     * This handles the TARGET → CLIENT direction:
     * 1. Read plain data from target server
     * 2. Forward directly to client (no encryption/decryption)
     *
     * Note: In this implementation, server-to-client traffic is NOT encrypted.
     * If you need encryption in both directions, add encryption here.
     *
     * Runs in a loop until connection is closed.
     *
     * @param targetSocket Socket connected to the target server
     * @param clientSocket Socket connected to the VPN client
     * @return The started thread
     */
    private Thread forwardToClient(Socket targetSocket, Socket clientSocket) {
        Thread thread = new Thread(() -> {
            String clientIP = clientSocket.getInetAddress().getHostAddress();

            try {
                InputStream targetInput = targetSocket.getInputStream();
                OutputStream clientOutput = clientSocket.getOutputStream();
                byte[] buffer = new byte[PLAIN_BUFFER_SIZE];

                System.out.println("[FORWARD] Started Target → Client forwarding thread for " + clientIP);

                // Main forwarding loop
                while (!targetSocket.isClosed() && !clientSocket.isClosed()) {

                    // Check if data is available
                    if (targetInput.available() > 0) {

                        // Read data from target server
                        int bytesRead = targetInput.read(buffer);
                        if (bytesRead <= 0) {
                            System.out.println("[INFO] Target server closed connection (read returned " + bytesRead + ")");
                            break;
                        }

                        // Forward data directly to client without encryption
                        // TODO: Add encryption here if needed for bidirectional encryption
                        clientOutput.write(buffer, 0, bytesRead);
                        clientOutput.flush();

                        System.out.println("[DATA] Target → Client: Forwarded " + bytesRead + " bytes");

                    } else {
                        // No data available - sleep briefly to prevent CPU spinning
                        Thread.sleep(SLEEP_INTERVAL_MS);
                    }
                }

                System.out.println("[INFO] Target → Client forwarding stopped for " + clientIP);

            } catch (Exception e) {
                System.err.println("[ERROR] Target → Client forwarding error for " + clientIP + ": " + e.getMessage());
            }
        });

        thread.start();
        return thread;
    }
}