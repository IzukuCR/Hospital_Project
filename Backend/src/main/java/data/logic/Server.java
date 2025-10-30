package data.logic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.Protocol;

public class Server {
    private ServerSocket ss;
    private List<Worker> workers;

    public Server() {
        try {
            ss = new ServerSocket(Protocol.PORT);
            workers = Collections.synchronizedList(new ArrayList<Worker>());
            System.out.println("Server started on port " + Protocol.PORT + "...");
        } catch (IOException ex) {
            System.err.println("Failed to start server socket on port " + Protocol.PORT + ": " + ex);
            throw new IllegalStateException("Cannot start server", ex);
        }
    }

    public void run() {
        if (ss == null) {
            System.err.println("Server socket is null, aborting run.");
            return;
        }

        boolean continueRunning = true;
        while (continueRunning) {
            try {
                // Accept synchronous connection (SYNC)
                Socket syncSocket = ss.accept();
                System.out.println("SYNC Connection made...");

                // Create input/output streams for the sync socket
                ObjectInputStream syncIs = new ObjectInputStream(syncSocket.getInputStream());
                ObjectOutputStream syncOs = new ObjectOutputStream(syncSocket.getOutputStream());

                // Read the session ID from the sync socket
                String sessionId = (String) syncIs.readObject();  // Read session ID

                // Create the worker for the synchronous connection
                Worker worker = new Worker(this, syncSocket, syncOs, syncIs, Service.instance());

                // Set the session ID in the worker
                worker.setSessionId(sessionId);  // Set the session ID in the worker

                // Accept asynchronous connection (ASYNC)
                Socket asyncSocket = ss.accept();
                System.out.println("ASYNC Connection made...");

                // Create separate input/output streams for the async socket
                ObjectInputStream asyncIs = new ObjectInputStream(asyncSocket.getInputStream());
                ObjectOutputStream asyncOs = new ObjectOutputStream(asyncSocket.getOutputStream());

                // Set the async socket in the worker
                worker.setAsyncSocket(asyncSocket, asyncOs, asyncIs); // Set async socket and streams

                // Add the worker to the list of workers
                workers.add(worker);
                System.out.println("Remaining workers: " + workers.size());

                // Start the worker to handle the connections
                worker.start();

                // Send the session ID back to the client via the sync socket
                syncOs.writeObject(worker.getSessionId());  // Send session ID back
                syncOs.flush();

            } catch (IOException | ClassNotFoundException ex) {
                System.err.println("Error: " + ex.getMessage());
                ex.printStackTrace();
                continueRunning = false;
            }
        }
    }

    public void remove(Worker w) {
        workers.remove(w);
        System.out.println("Remaining: " + workers.size());
    }
}