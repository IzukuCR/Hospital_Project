package data.logic;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import logic.*;

public class Server {

    private ServerSocket syncServer;
    private ServerSocket asyncServer;
    private static final Map<String, Worker> activeUsers = new ConcurrentHashMap<>();

    public Server() {
        try {
            syncServer = new ServerSocket(Protocol.PORT_SYNC);
            asyncServer = new ServerSocket(Protocol.PORT_ASYNC);
            System.out.println("Server running on ports: SYNC=" + Protocol.PORT_SYNC + ", ASYNC=" + Protocol.PORT_ASYNC);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server sockets: " + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            try {
                System.out.println("\nWaiting SYNC connection...");
                Socket syncSocket = syncServer.accept();
                ObjectOutputStream syncOs = new ObjectOutputStream(syncSocket.getOutputStream());
                syncOs.flush();
                ObjectInputStream syncIs = new ObjectInputStream(syncSocket.getInputStream());


                System.out.println("SYNC connection established.");

                // Esperar conexión asíncrona
                System.out.println("Waiting ASYNC connection...");
                Socket asyncSocket = asyncServer.accept();
                ObjectOutputStream asyncOs = new ObjectOutputStream(asyncSocket.getOutputStream());
                asyncOs.flush();
                ObjectInputStream asyncIs = new ObjectInputStream(asyncSocket.getInputStream());

                Worker worker = new Worker(this, syncSocket, syncOs, syncIs, asyncSocket, asyncOs, asyncIs);
                activeUsers.put(worker.getSessionId(), worker);
                worker.start();

                System.out.println("Connected new Worker. Active users: " + activeUsers.size());
            } catch (Exception e) {
                System.err.println("Server error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void remove(String sessionId) {
        activeUsers.remove(sessionId);
        System.out.println("Disconnected: " + sessionId + " (Remaining: " + activeUsers.size() + ")");
    }

    public void sendMessage(Message msg) {
        Worker receiver = activeUsers.get(msg.getReceiver());
        if (receiver != null) {
            receiver.sendAsync(msg);
            System.out.println("[SERVER] Forwarded from " + msg.getSender() + " → " + msg.getReceiver());
        } else {
            System.out.println("[SERVER] Receiver not connected: " + msg.getReceiver());
        }
    }
    public static Map<String, Worker> getActiveUsers() {
        return activeUsers;
    }

    public void broadcastActiveUsers() {
        try {
            List<String> activeUserIds = new ArrayList<>(getActiveUsers().keySet());
            for (Worker w : getActiveUsers().values()) {
                try {
                    System.out.println("[DEBUG] Broadcasting to " + getActiveUsers().size() + " clients.");
                    w.sendActiveUsers(activeUserIds);
                } catch (Exception e) {
                    System.err.println("Error sending active users to " + w.getSessionId() + ": " + e.getMessage());
                }
            }
            System.out.println("[SERVER] Active users broadcast: " + activeUserIds);
        } catch (Exception e) {
            System.err.println("[SERVER] Failed to broadcast active users: " + e.getMessage());
        }
    }

}