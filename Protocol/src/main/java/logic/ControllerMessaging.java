package logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

public class ControllerMessaging implements Serializable {
    private MessagingService messagingService;

    public ControllerMessaging() {
        this.messagingService = MessagingService.getInstance();
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

    public List<Message> getMessages(String userId, String fromUser) {
        return messagingService.getMessagesFor(userId, fromUser);
    }

    public void markAsRead(String userId, String fromUser) {
        messagingService.markMessagesAsRead(userId, fromUser);
    }

    public Set<String> getActiveUsers() {
        return messagingService.getActiveUsers();
    }

    public void addListener(MessagingService.MessagingListener listener) {
        messagingService.addListener(listener);
    }

    public void removeListener(MessagingService.MessagingListener listener) {
        messagingService.removeListener(listener);
    }

    // Clase interna para el servicio de mensajería
    public static class MessagingService {
        private static MessagingService instance;
        private Set<String> activeUsers;
        private Map<String, List<Message>> messages;
        private List<MessagingListener> listeners;

        private MessagingService() {
            activeUsers = new HashSet<>();
            messages = new HashMap<>();
            listeners = new ArrayList<>();
        }

        public static MessagingService getInstance() {
            if (instance == null) {
                instance = new MessagingService();
            }
            return instance;
        }

        public void userLoggedIn(String userId) {
            activeUsers.add(userId);
            notifyUserStatusChanged(userId, true);
        }

        public void userLoggedOut(String userId) {
            activeUsers.remove(userId);
            notifyUserStatusChanged(userId, false);
        }

        public void sendMessage(String sender, String recipient, String content) {
            Message message = new Message(sender, recipient, content, System.currentTimeMillis(), false);
            String key = getConversationKey(sender, recipient);

            messages.computeIfAbsent(key, k -> new ArrayList<>()).add(message);
            notifyNewMessage(message);
        }

        public List<Message> getMessagesFor(String userId, String fromUser) {
            String key = getConversationKey(userId, fromUser);
            return messages.getOrDefault(key, new ArrayList<>());
        }

        public void markMessagesAsRead(String userId, String fromUser) {
            String key = getConversationKey(userId, fromUser);
            List<Message> conversation = messages.get(key);
            if (conversation != null) {
                for (Message message : conversation) {
                    if (message.recipient.equals(userId)) {
                        message.read = true;
                    }
                }
            }
        }

        public Set<String> getActiveUsers() {
            return new HashSet<>(activeUsers);
        }

        private String getConversationKey(String user1, String user2) {
            return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
        }

        public void addListener(MessagingListener listener) {
            listeners.add(listener);
        }

        public void removeListener(MessagingListener listener) {
            listeners.remove(listener);
        }

        private void notifyNewMessage(Message message) {
            for (MessagingListener listener : listeners) {
                listener.onNewMessage(message);
            }
        }

        private void notifyUserStatusChanged(String userId, boolean online) {
            for (MessagingListener listener : listeners) {
                listener.onUserStatusChanged(userId, online);
            }
        }

        public interface MessagingListener {
            void onNewMessage(Message message);
            void onUserStatusChanged(String userId, boolean online);
        }
    }

    // Clase para representar un mensaje
    public static class Message implements Serializable {
        public String sender;
        public String recipient;
        public String content;
        public long timestamp;
        public boolean read;

        public Message(String sender, String recipient, String content, long timestamp, boolean read) {
            this.sender = sender;
            this.recipient = recipient;
            this.content = content;
            this.timestamp = timestamp;
            this.read = read;
        }
    }
}