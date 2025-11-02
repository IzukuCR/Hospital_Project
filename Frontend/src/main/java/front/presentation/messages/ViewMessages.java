package front.presentation.messages;

import logic.Message;
import javax.swing.*;
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

    ControllerMessages controllerMessages;
    ModelMessages modelMessages;

    public ViewMessages() {
        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);

        tableModel = new TableModelMessages(new ArrayList<>());
        UsersActives.setModel(tableModel);
    }

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
        chatArea.append(msg.getSender() + ": " + msg.getContent() + "\n");
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
