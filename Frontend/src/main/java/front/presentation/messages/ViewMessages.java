package front.presentation.messages;

import logic.Message;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class ViewMessages extends JPanel implements PropertyChangeListener {
    private JTextArea chatArea;
    private JScrollPane scrollPane;
    private JPanel panel1;
    private JButton sendButton;
    private JButton receiveButton;
    private JTable UsersActives;

    private TableModelMessages tableModel;
    private ControllerMessages controllerMessages;
    private ModelMessages modelMessages;

    private String activeUserId; // 🔹 Usuario activo por checkmark

    public ViewMessages() {
        setLayout(new BorderLayout());

        tableModel = new TableModelMessages(new ArrayList<>());
        UsersActives.setModel(tableModel);

        // 🔹 Configuración visual básica
        UsersActives.setRowSelectionAllowed(false);
        UsersActives.setColumnSelectionAllowed(false);
        UsersActives.setCellSelectionEnabled(true);

        add(panel1, BorderLayout.CENTER);

        // 🔹 Detectar cambios en el modelo (cuando el usuario marca el check)
        tableModel.addTableModelListener(e -> updateActiveUser());

        // ---- Botón Send ----
        sendButton.addActionListener(e -> {
            if (activeUserId == null || activeUserId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a user with the checkmark first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            openSendMessageDialog(activeUserId);
        });

        // ---- Botón Receive ----
        receiveButton.addActionListener(e -> {
            if (activeUserId == null || activeUserId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a user with the checkmark first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            openReceiveMessageDialog(activeUserId);
        });
    }

    /**
     * 🔹 Actualiza el usuario activo basado en el check marcado
     */
    private void updateActiveUser() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean checked = (Boolean) tableModel.getValueAt(i, 1);
            if (checked) {
                activeUserId = (String) tableModel.getValueAt(i, 0);
                System.out.println("[ViewMessages] Active user set: " + activeUserId);
                return;
            }
        }
        activeUserId = null; // ninguno marcado
    }

    private void openSendMessageDialog(String receiverId) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Send message to " + receiverId, true);
        dialog.setLayout(new BorderLayout(10, 10));

        JTextArea messageArea = new JTextArea(5, 25);
        JScrollPane scroll = new JScrollPane(messageArea);
        JButton sendBtn = new JButton("Send");
        JButton closeBtn = new JButton("Close");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(sendBtn);
        buttonPanel.add(closeBtn);

        dialog.add(new JLabel("Write message:"), BorderLayout.NORTH);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        sendBtn.addActionListener(ev -> {
            String content = messageArea.getText().trim();
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Empty message.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (controllerMessages != null) {
                controllerMessages.sendMessage(receiverId, content);
            }

            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Message sent successfully to " + receiverId + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        closeBtn.addActionListener(ev -> dialog.dispose());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void openReceiveMessageDialog(String userId) {
        List<Message> messages = modelMessages != null ? modelMessages.getMessages() : new ArrayList<>();
        Message lastMessage = null;

        for (int i = messages.size() - 1; i >= 0; i--) {
            Message m = messages.get(i);
            if (m.getSender().equals(userId)) {
                lastMessage = m;
                break;
            }
        }

        if (lastMessage == null) {
            JOptionPane.showMessageDialog(this, "No recent messages from " + userId + ".", "No messages", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 🔹 Cuadro pequeño con el mensaje
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Last message from " + userId, true);
        dialog.setLayout(new BorderLayout(10, 10));

        JTextArea messageView = new JTextArea(lastMessage.getContent());
        messageView.setEditable(false);
        messageView.setLineWrap(true);
        messageView.setWrapStyleWord(true);
        messageView.setBackground(new Color(245, 245, 245));
        messageView.setForeground(Color.BLACK);
        messageView.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        dialog.add(new JScrollPane(messageView), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(ev -> dialog.dispose());
        dialog.add(closeBtn, BorderLayout.SOUTH);

        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ---- Integración con Controller y Model ----
    public void setController(ControllerMessages controller) {
        this.controllerMessages = controller;
    }

    public void setModel(ModelMessages model) {
        this.modelMessages = model;
    }

    public void refreshMessages() {
        tableModel.fireTableDataChanged();
    }

    public void appendMessage(Message msg) {
        if (chatArea != null) {
            chatArea.append(msg.getSender() + ": " + msg.getContent() + "\n");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "messages" -> refreshMessages();
            case "activeUsers" -> {
                List<String> users = (List<String>) evt.getNewValue();
                tableModel.setUsers(users);
            }
        }
    }
}