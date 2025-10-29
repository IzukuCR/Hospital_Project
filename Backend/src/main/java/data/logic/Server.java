package data.logic;

import java.io.IOException;
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

        boolean continuar = true;
        while (continuar) {
            try {
                Socket s = ss.accept();
                System.out.println("Connection made...");
                Worker worker = new Worker(this, s, Service.instance());
                workers.add(worker);
                System.out.println("Remaining: " + workers.size());
                worker.start();
            } catch (IOException ex) {
                System.err.println("I/O error accepting connection: " + ex.getMessage());
                ex.printStackTrace();
                // Likely ss closed or network issue -> stop accepting
                continuar = false;
            } catch (Exception ex) {
                System.err.println("Unexpected error accepting connection:");
                ex.printStackTrace();
            }
        }
    }

    public void remove(Worker w) {
        workers.remove(w);
        System.out.println("Remaining: " + workers.size());
    }
}