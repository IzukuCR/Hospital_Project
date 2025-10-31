package front.presentation.messages;

import front.logic.Service;
import front.logic.SocketListener;
import front.presentation.AsyncListener;
import logic.Message;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class ControllerMessages implements AsyncListener {
    private ModelMessages model;
    private ViewMessages view;
    private Service service;
    private SocketListener socketListener;

    public ControllerMessages(ModelMessages model, ViewMessages view) {
        this.model = model;
        this.view = view;
        this.service = Service.instance();
        this.view.setController(this);
    }

    public void setUser(String userId) {
        model.setCurrentUserId(userId);
    }

    @Override
    public void onAsyncNotification(Object obj) {
        // Ensure any UI updates run on EDT
        if (obj instanceof Message msg) {
            SwingUtilities.invokeLater(() -> onMessageReceived(msg));
        }
    }

    public void attachAsyncSocket(Socket asyncSocket) throws IOException {
        if (socketListener != null) {
            socketListener.stopListening();
        }

        socketListener = new SocketListener(asyncSocket, new AsyncListener() {
            @Override
            public void onAsyncNotification(Object obj) {
                if (obj instanceof Message msg) {
                    // Mostrar mensaje en la vista
                    view.appendMessage(msg);
                    System.out.println("[ASYNC] New message from " + msg.getSender() +
                            " → " + msg.getReceiver() + ": " + msg.getContent());
                } else {
                    System.out.println("[ASYNC] Unknown async object: " + obj);
                }
            }
        });

        socketListener.start(); // arranca el hilo que escucha notificaciones
    }
    public void onMessageReceived(Message msg) {
        model.addMessage(msg);              // se agrega al modelo
        view.refreshMessages();             // se actualiza la vista
        System.out.println("Message received from " + msg.getSender());
    }


    public void sendMessage(String receiverId, String content) {
        Message msg = new Message(model.getCurrentUserId(), receiverId, content);

        //model.getService().sendMessage(msg);
        model.addMessage(msg);              // opcional: mostrar también los mensajes enviados
        view.refreshMessages();
    }
}
