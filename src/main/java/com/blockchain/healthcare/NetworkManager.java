package com.blockchain.healthcare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkManager {
    private final int port;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Node node;

    public NetworkManager(int port, Node node) {
        this.port = port;
        this.node = node;
    }

    public void startServer() {
        executor.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Node P2P server listening on port " + port);
                while (true) {
                    Socket socket = serverSocket.accept();
                    executor.submit(() -> handleClient(socket));
                }
            } catch (IOException e) {
                System.err.println("Could not start server on port " + port);
                e.printStackTrace();
            }
        });
    }

    private void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                node.receiveMessage(line);
            }
        } catch (IOException e) {
            // Client disconnected or error
        }
    }

    public void sendMessage(String host, int port, String message) {
        executor.submit(() -> {
            try (Socket socket = new Socket(host, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
            } catch (IOException e) {
                // Peer is likely offline
            }
        });
    }
}
