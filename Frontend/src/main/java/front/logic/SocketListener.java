package front.logic;

import front.presentation.AsyncListener;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class SocketListener extends Thread {

    private final Socket asyncSocket;
    private final ObjectInputStream input;
    private volatile AsyncListener listener;
    private volatile boolean running = false;

    public SocketListener(Socket asyncSocket, ObjectInputStream input, AsyncListener listener) {
        super("SocketListener-" + asyncSocket.getPort());
        this.asyncSocket = asyncSocket;
        this.input = input;
        this.listener = listener;
    }

    @Override
    public void run() {
        running = true;
        System.out.println("[SocketListener] Listening on " + asyncSocket.getPort());

        try {
            while (running && !asyncSocket.isClosed()) {
                Object obj = input.readObject();
                AsyncListener l = this.listener;
                if (l != null) {
                    SwingUtilities.invokeLater(() -> l.onAsyncNotification(obj));
                }
            }
        } catch (EOFException e) {
            System.err.println("[SocketListener] EOF reached, socket closed.");
        } catch (IOException e) {
            if (running) System.err.println("[SocketListener] I/O error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("[SocketListener] Unknown object: " + e.getMessage());
        } finally {

            System.out.println("[SocketListener] stopped.");
        }
    }

    public void stopListening() {
        running = false;

    }

    public void setListener(AsyncListener listener) {
        this.listener = listener;
    }
}
