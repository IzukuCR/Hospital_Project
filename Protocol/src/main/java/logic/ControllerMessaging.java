package main.java.logic;

import java.util.List;
import java.util.Set;

public class ControllerMessaging {
    private Service.MessagingService messagingService;

    public ControllerMessaging() {
        this.messagingService = Service.MessagingService.getInstance();
    }

    public void loginUser(String userId) {
        messagingService.userLoggedIn(userId);
    }

    public void logoutUser(String userId) {
        messagingService.userLoggedOut(userId);
    }

    public void sendMessage(String sender, String recipient, String message) {
        messagingService.sendMessage(sender, recipient, message);
    }

    public List<Service.MessagingService.Message> getMessages(String userId, String fromUser) {
        return messagingService.getMessagesFor(userId, fromUser);
    }

    public void markAsRead(String userId, String fromUser) {
        messagingService.markMessagesAsRead(userId, fromUser);
    }

    public Set<String> getActiveUsers() {
        return messagingService.getActiveUsers();
    }

    public void addListener(Service.MessagingService.MessagingListener listener) {
        messagingService.addListener(listener);
    }

    public void removeListener(Service.MessagingService.MessagingListener listener) {
        messagingService.removeListener(listener);
    }
}
