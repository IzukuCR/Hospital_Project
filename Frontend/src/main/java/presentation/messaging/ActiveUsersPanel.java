package presentation.messaging;

import logic.ControllerMessaging;
import logic.Service;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ActiveUsersPanel extends JPanel implements Service.MessagingService.MessagingListener {
    private DefaultListModel<String> userListModel;
    private JList<String> userList;
    private JButton sendButton;
    private JButton receiveButton;
    private Map<String, Integer> unreadCounts;
    private String currentUserId;
    private ControllerMessaging controller;

    public ActiveUsersPanel(String currentUserId) {
        this.currentUserId = currentUserId;
        this.unreadCounts = new HashMap<>();
        this.controller = new ControllerMessaging();

        initializeComponents();
        setupLayout();

        Service.MessagingService.getInstance().addListener(this);
        refreshActiveUsers();
    }

    private void initializeComponents() {
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new UserListCellRenderer());

        sendButton = new JButton("Send");
        receiveButton = new JButton("Receive");

        setupListeners();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Active Users"));
        setPreferredSize(new Dimension(200, 0));

        JScrollPane scrollPane = new JScrollPane(userList);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.add(sendButton);
        buttonPanel.add(receiveButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        sendButton.addActionListener(e -> handleSendMessage());
        receiveButton.addActionListener(e -> handleReceiveMessage());
    }

    private void handleSendMessage() {
        String selectedUser = userList.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cleanUserId = selectedUser.split(" \\(")[0];

        if (cleanUserId.equals(currentUserId)) {
            JOptionPane.showMessageDialog(this,
                    "You cannot send messages to yourself",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = JOptionPane.showInputDialog(this,
                "Enter message for " + cleanUserId + ":",
                "Send Message",
                JOptionPane.PLAIN_MESSAGE);

        if (message != null && !message.trim().isEmpty()) {
            try {
                controller.sendMessage(currentUserId, cleanUserId, message);
                JOptionPane.showMessageDialog(this,
                        "Message sent to " + cleanUserId,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error sending message: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleReceiveMessage() {
        String selectedUser = userList.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a sender user",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cleanUserId = selectedUser.split(" \\(")[0];

        List<Service.MessagingService.Message> messages = controller.getMessages(currentUserId, cleanUserId);

        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No messages from " + cleanUserId,
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder messagesText = new StringBuilder();
            for (Service.MessagingService.Message msg : messages) {
                messagesText.append("From: ").append(msg.getSender()).append("\n");
                messagesText.append("Time: ").append(new Date(msg.getTimestamp())).append("\n");
                messagesText.append(msg.getContent()).append("\n\n");
            }

            JOptionPane.showMessageDialog(this,
                    messagesText.toString(),
                    "Messages from " + cleanUserId,
                    JOptionPane.INFORMATION_MESSAGE);

            controller.markAsRead(currentUserId, cleanUserId);

            unreadCounts.put(cleanUserId, 0);
            refreshUserList();
        }
    }

    private void refreshActiveUsers() {
        SwingUtilities.invokeLater(() -> {
            Set<String> activeUsers = controller.getActiveUsers();
            userListModel.clear();
            for (String userId : activeUsers) {
                if (!userId.equals(currentUserId)) {
                    int unread = unreadCounts.getOrDefault(userId, 0);
                    String display = unread > 0 ? userId + " (" + unread + ")" : userId;
                    userListModel.addElement(display);
                }
            }
        });
    }
    public void cleanup() {
        controller.removeListener(this);
    }

    private void refreshUserList() {
        SwingUtilities.invokeLater(() -> {
            String selectedValue = userList.getSelectedValue();
            userListModel.clear();

            Set<String> activeUsers = Service.MessagingService.getInstance().getActiveUsers();
            for (String userId : activeUsers) {
                if (!userId.equals(currentUserId)) {
                    int unread = unreadCounts.getOrDefault(userId, 0);
                    String display = unread > 0 ? userId + " (" + unread + ")" : userId;
                    userListModel.addElement(display);
                }
            }

            if (selectedValue != null && userListModel.contains(selectedValue)) {
                userList.setSelectedValue(selectedValue, true);
            }
        });
    }

    @Override
    public void onUserLoggedIn(String userId) {
        refreshActiveUsers();
    }

    @Override
    public void onUserLoggedOut(String userId) {
        unreadCounts.remove(userId);
        refreshActiveUsers();
    }

    @Override
    public void onMessageReceived(String recipient, Service.MessagingService.Message message) {
        if (recipient.equals(currentUserId)) {
            String sender = message.getSender();
            unreadCounts.put(sender, unreadCounts.getOrDefault(sender, 0) + 1);
            refreshUserList();

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "New message from " + sender,
                        "Message Received",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }

    private class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            String text = value.toString();
            if (text.contains("(") && text.contains(")")) {
                setFont(getFont().deriveFont(Font.BOLD));
                setForeground(isSelected ? list.getSelectionForeground() : Color.BLUE);
            } else {
                setFont(getFont().deriveFont(Font.PLAIN));
            }

            return this;
        }
    }
}
