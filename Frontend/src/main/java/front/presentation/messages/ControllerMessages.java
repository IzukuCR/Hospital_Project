package front.presentation.messages;

import front.logic.Service;
import front.logic.SocketListener;
import front.presentation.AsyncListener;
import logic.Message;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ControllerMessages implements AsyncListener {
    private ModelMessages model;
    private ViewMessages view;
    private Service service;

    public ControllerMessages(ModelMessages model, ViewMessages view) {
        this.model = model;
        this.view = view;
        this.service = Service.instance();
        this.view.setController(this);
        this.model.addPropertyChangeListener(view);

        this.service.setAsyncListener(this);
    }

    public void setUser(String userId) {
        model.setCurrentUserId(userId);
    }

    @Override
    public void onAsyncNotification(Object obj) {
        if (obj instanceof Message msg) {
            System.out.println("[ASYNC CLIENT] Message received: " + msg.getSender() + " → " + msg.getReceiver());
            SwingUtilities.invokeLater(() -> model.addMessage(msg));
            model.addMessage(msg);
            view.appendMessage(msg);

        } else if (obj instanceof List<?> users) {
            List<String> connectedUsers = new ArrayList<>();
            for (Object u : users) {
                if (u instanceof String id && !id.equals(model.getCurrentUserId())) {
                    connectedUsers.add(id);
                }
            }
            SwingUtilities.invokeLater(() -> model.setActiveUsers(connectedUsers));
            System.out.println("[ASYNC CLIENT] Active users updated: " + connectedUsers);

        } else {
            System.out.println("[ASYNC CLIENT] Unknown async object: " + obj);
        }
    }

    public void onMessageReceived(Message msg) {
        model.addMessage(msg);
        view.refreshMessages();
        System.out.println("Message received from " + msg.getSender());
    }

    public void sendMessage(String receiverId, String content) {
        try {
            Message msg = new Message(content, model.getCurrentUserId(), receiverId);
            service.sendMessage(msg);
            model.addMessage(msg);
            view.appendMessage(msg);
            System.out.println("[ControllerMessages] Message sent to " + receiverId);
        } catch (IOException e) {
            System.err.println("[ControllerMessages] Error sending message: " + e.getMessage());
        }
    }
}
