package front.logic;

import front.presentation.AsyncListener;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class SocketListener extends Thread{

    private final Socket asyncSocket;
    private final AsyncListener listener;
    private volatile boolean running = false;
    private ObjectInputStream input;

    public SocketListener(Socket asyncSocket, AsyncListener listener) {
        super("SocketListener-");
        this.asyncSocket = asyncSocket;
        this.listener = listener;
        // do NOT create ObjectInputStream here (can block). Create it in run().
    }

    @Override
    public void run() {
        running = true;
        try (Socket s = asyncSocket) {
            try {
                input = new ObjectInputStream(s.getInputStream()); // may block, but off the EDT / init thread
            } catch (IOException e) {
                System.err.println("Failed to create async input stream: " + e.getMessage());
                return;
            }

            while (running) {
                try {
                    Object obj = input.readObject();
                    if (obj != null && listener != null) {
                        listener.onAsyncNotification(obj);
                    }
                } catch (IOException e) {
                    if (running) {
                        System.err.println("SocketListener I/O error: " + e.getMessage());
                    }
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("SocketListener received unknown object: " + e.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("SocketListener terminated: " + ex.getMessage());
        } finally {
            stopListening();
        }
    }

    public void stopListening() {
        running = false;
        try {
            if (input != null) input.close();
        } catch (IOException e) {
            System.err.println("Error closing async input stream: " + e.getMessage());
        }
        try {
            if (asyncSocket != null && !asyncSocket.isClosed()) asyncSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing async socket: " + e.getMessage());
        }
        System.out.println("SocketListener stopped.");
    }

}
