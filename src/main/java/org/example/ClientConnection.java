package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static org.example.Server.*;

public class ClientConnection implements Runnable {
    private final String clientName;
    private final Socket clientSocket;
    public BufferedReader in;
    private final PrintWriter out;

    public ClientConnection(String clientName, Socket clientSocket) {
        this.clientName = clientName;
        this.clientSocket = clientSocket;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error setting up client connection: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getClientName() {
        return clientName;
    }

    public void sendMessage(String message) throws IOException {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String input = in.readLine();
                if (input == null) {
                    break;
                } else if (input.equals("-exit")) {
                    break;
               }
//                else if (input.startsWith("-file")) {
//                    receiveFile(this, "C:\\Users\\fafo8\\IdeaProjects\\Homework_28\\src\\main\\java\\org\\example\\test.txt");
//                }
               else {
                    System.out.println("[" + clientName + "]: " + input);
                    broadcast("[" + clientName + "]: " + input, connections);
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
            try {
                removeConnection(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
