package data.logic;

import logic.Protocol;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    ServerSocket ss;
    List<Worker> workers;

    public Server() {
        try {
            ss = new ServerSocket(Protocol.PORT);
            workers = Collections.synchronizedList(new ArrayList<>());
            System.out.println("Server started on port " + Protocol.PORT + "...");
        } catch (IOException ex) {
            System.out.println("Error starting server: " + ex.getMessage());
        }
    }

    public void run() {
        Service service = new Service();
        boolean continuar = true;
        Socket s;
        Worker worker;
        while (continuar) {
            try {
                s = ss.accept();
                System.out.println("Connection established from: " + s.getInetAddress());
                worker = new Worker(this, s, service);
                workers.add(worker);
                System.out.println("Active workers: " + workers.size());
                worker.start();
            } catch (Exception ex) {
                System.out.println("Error accepting connection: " + ex.getMessage());
            }
        }
    }

    public void remove(Worker w) {
        workers.remove(w);
        System.out.println("Active workers: " + workers.size());
    }
}
