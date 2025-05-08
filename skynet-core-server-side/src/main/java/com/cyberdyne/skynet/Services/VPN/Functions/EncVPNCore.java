package com.cyberdyne.skynet.Services.VPN.Functions;

import com.cyberdyne.skynet.Services.Encription.EncriptionBytesCLS;
import com.cyberdyne.skynet.Services.Encription.EncriptionBytesCLS;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EncVPNCore
{
    private final ExecutorService threadPool;
    private final String encryptionKey;

    // Constructor for the encrypted proxy server
    public EncVPNCore(int Port, String encryptionKey)
    {
        // Store encryption key
        this.encryptionKey = encryptionKey;

        // Create a thread pool for better resource management
        threadPool = Executors.newCachedThreadPool();

        try
        {
            ServerSocket Server = new ServerSocket(Port);
            System.out.println("Encrypted proxy server started on port " + Port);

            while (true)
            {
                Socket request = Server.accept();
                System.out.println("New connection from " + request.getInetAddress());

                threadPool.submit(() -> {
                    try
                    {
                        HandleEncryptedRequest(request);
                    }
                    catch (Exception e)
                    {
                        System.err.println("Error handling proxy request: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        }
        catch (Exception e)
        {
            System.err.println("Server initialization error: " + e.getMessage());
            e.printStackTrace();
            threadPool.shutdown();
        }
    }

    // Handle encrypted request from client proxy
    private void HandleEncryptedRequest(Socket clientSocket)
    {
        Socket targetSocket = null;

        try
        {
            InputStream clientInput = clientSocket.getInputStream();
            OutputStream clientOutput = clientSocket.getOutputStream();

            // Wait for first packet of data
            while (clientInput.available() == 0) {
                Thread.sleep(5);
            }

            // Read all available data
            byte[] firstPacket = readAvailableData(clientInput);
            if (firstPacket.length == 0) {
                System.err.println("Empty initial packet received");
                clientSocket.close();
                return;
            }

            // Decrypt the initial data - this should be the CONNECT request
            byte[] decryptedFirstPacket;
            try {
                decryptedFirstPacket = EncriptionBytesCLS.decrypt(firstPacket, encryptionKey);
            } catch (Exception e) {
                System.err.println("Failed to decrypt initial request: " + e.getMessage());
                e.printStackTrace();
                clientSocket.close();
                return;
            }

            // Parse the decrypted request (first line should be the CONNECT request)
            String decryptedRequest = new String(decryptedFirstPacket);
            String[] requestLines = decryptedRequest.split("\r\n");

            if (requestLines.length == 0) {
                System.err.println("Invalid request format after decryption");
                clientSocket.close();
                return;
            }

            // Parse the first line (CONNECT host:port HTTP/1.1)
            String[] firstLineParams = requestLines[0].split(" ");
            if (firstLineParams.length < 2 || !firstLineParams[0].toUpperCase().equals("CONNECT")) {
                System.err.println("Not a CONNECT request: " + requestLines[0]);
                String errorResponse = "HTTP/1.1 400 Bad Request\r\n\r\n";
                clientOutput.write(errorResponse.getBytes());
                clientOutput.flush();
                clientSocket.close();
                return;
            }

            // Extract target host and port
            String[] hostParts = firstLineParams[1].split(":");
            String targetHost = hostParts[0];
            int targetPort = hostParts.length > 1 ? Integer.parseInt(hostParts[1]) : 443;

            System.out.println("Connecting to target: " + targetHost + ":" + targetPort);

            // Connect to the target server
            try {
                targetSocket = new Socket(targetHost, targetPort);
            } catch (Exception e) {
                System.err.println("Failed to connect to target: " + e.getMessage());
                String errorResponse = "HTTP/1.1 502 Bad Gateway\r\n\r\n";
                clientOutput.write(errorResponse.getBytes());
                clientOutput.flush();
                clientSocket.close();
                return;
            }

            // Send 200 Connection Established response
            String successResponse = "HTTP/1.1 200 Connection Established\r\n\r\n";
            clientOutput.write(successResponse.getBytes());
            clientOutput.flush();

            System.out.println("Connected to " + targetHost + ":" + targetPort);

            // Start bidirectional data transfer
            Thread clientToTarget = decryptAndForward(clientSocket, targetSocket);
            Thread targetToClient = forwardToClient(targetSocket, clientSocket);

            // Wait for threads to complete
            clientToTarget.join();
            targetToClient.join();

        } catch (Exception e) {
            System.err.println("Error in proxy handling: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (targetSocket != null && !targetSocket.isClosed()) {
                    targetSocket.close();
                }
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing sockets: " + e.getMessage());
            }
        }
    }

    // Helper method to read all available data from an input stream
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

    // Thread to decrypt data from client and forward to target
    private Thread decryptAndForward(Socket clientSocket, Socket targetSocket) {
        Thread thread = new Thread(() -> {
            try {
                InputStream clientInput = clientSocket.getInputStream();
                OutputStream targetOutput = targetSocket.getOutputStream();
                byte[] buffer = new byte[16384]; // Larger buffer for encrypted data

                while (!clientSocket.isClosed() && !targetSocket.isClosed()) {
                    if (clientInput.available() > 0) {
                        // Read encrypted data
                        int bytesRead = clientInput.read(buffer);
                        if (bytesRead <= 0) break;

                        // Extract the actual data
                        byte[] encryptedChunk = new byte[bytesRead];
                        System.arraycopy(buffer, 0, encryptedChunk, 0, bytesRead);

                        try {
                            // Decrypt the data
                            byte[] decryptedData = EncriptionBytesCLS.decrypt(encryptedChunk, encryptionKey);

                            // Forward to target
                            targetOutput.write(decryptedData);
                            targetOutput.flush();
                            System.out.println("Client → Target: Decrypted and forwarded " + bytesRead +
                                    " bytes → " + decryptedData.length + " bytes");
                        } catch (Exception e) {
                            System.err.println("Decryption error: " + e.getMessage());
                            break;
                        }
                    } else {
                        // Sleep to prevent CPU spinning
                        Thread.sleep(5);
                    }
                }
            } catch (Exception e) {
                System.err.println("Client → Target forwarding error: " + e.getMessage());
            }
        });

        thread.start();
        return thread;
    }

    // Thread to forward data from target to client (no encryption)
    private Thread forwardToClient(Socket targetSocket, Socket clientSocket) {
        Thread thread = new Thread(() -> {
            try {
                InputStream targetInput = targetSocket.getInputStream();
                OutputStream clientOutput = clientSocket.getOutputStream();
                byte[] buffer = new byte[8192];

                while (!targetSocket.isClosed() && !clientSocket.isClosed()) {
                    if (targetInput.available() > 0) {
                        int bytesRead = targetInput.read(buffer);
                        if (bytesRead <= 0) break;

                        // Forward data directly without encryption
                        clientOutput.write(buffer, 0, bytesRead);
                        clientOutput.flush();
                        System.out.println("Target → Client: Forwarded " + bytesRead + " bytes");
                    } else {
                        // Sleep to prevent CPU spinning
                        Thread.sleep(5);
                    }
                }
            } catch (Exception e) {
                System.err.println("Target → Client forwarding error: " + e.getMessage());
            }
        });

        thread.start();
        return thread;
    }
}