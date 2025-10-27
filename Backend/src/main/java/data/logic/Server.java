package data.logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.Protocol;

public class Server {
    ServerSocket ss;
    List<Worker> workers;
    public Server() {
        try {
            ss = new ServerSocket(Protocol.PORT);
            workers = Collections.synchronizedList(new ArrayList<Worker>());
            System.out.println("Server started...");
        } catch (IOException ex) { System.out.println(ex);}
    }
    public void run() {

        boolean continuar = true;
        Socket s;
        Worker worker;
        while (continuar) {
            try {
                s = ss.accept();
                System.out.println("Connection made...");
                worker = new Worker(this, s, Service.instance());
                workers.add(worker);
                System.out.println("Remaining: " + workers.size());
                worker.start();
            } catch (Exception ex) {
                System.out.println("Error accepting connection: " + ex.getMessage());
            }
        }
    }

    public void remove(Worker w) {
        workers.remove(w);
        System.out.println("Remaining: " +workers.size());
    }
}