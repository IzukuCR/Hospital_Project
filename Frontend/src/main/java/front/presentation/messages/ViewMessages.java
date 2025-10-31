package front.presentation.messages;

import logic.Message;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

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
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        tableModel = new TableModelMessages(new ArrayList<>());
        UsersActives = new JTable(tableModel);
        JScrollPane leftScroll = new JScrollPane(UsersActives);
        leftScroll.setPreferredSize(new Dimension(200, 0));
        add(leftScroll, BorderLayout.WEST);
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

    }
}
