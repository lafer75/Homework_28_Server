package org.example;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    public static final int PORT = 8088;
    public static PrintWriter out;
    private static LocalDateTime startTime;
    private static volatile boolean isRunning = true;
    protected static final Map<String, ClientConnection> activeConnections = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    static Map<String, ClientConnection> connections;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                String clientName = generateClientName();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                LocalDateTime clientStartTime = getClientStartTime();
                ClientConnection clientConnection = new ClientConnection(clientName, clientSocket);
                activeConnections.put(clientName, clientConnection);
                executorService.submit(clientConnection);

                System.out.println("[SERVER] " + clientName + " connected, Time:"+ LocalDateTime.now());
                broadcast("[SERVER] " + clientName + " connected", connections);


            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
    public static LocalDateTime getClientStartTime() {
        return startTime;
    }

    public static void broadcast(String message, Map<String, ClientConnection> connections) throws IOException {
        for (ClientConnection connection : activeConnections.values()) {
            connection.sendMessage(message);
            connection.sendMessage("Server: OK!");
        }
    }

    public static void removeConnection(ClientConnection connection) throws IOException {
        activeConnections.remove(connection.getClientName());
        System.out.println("[SERVER] " + connection.getClientName() + " disconnected");
        broadcast("[SERVER] " + connection.getClientName() + " disconnected", connections);
    }

    public static String generateClientName() {
        return "client-" + UUID.randomUUID().toString().substring(0, 8);
    }

//    public static void receiveFile(ClientConnection connection, String filePath) throws IOException {
//        try (InputStream inputStream = connection.getClientSocket().getInputStream();
//             FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                fileOutputStream.write(buffer, 0, bytesRead);
//            }
//            System.out.println("[SERVER] File received from " + connection.getClientName() + ": " + filePath);
//        } catch (IOException e) {
//            System.err.println("Error receiving file from " + connection.getClientName() + ": " + e.getMessage());
//        }
//    }

}