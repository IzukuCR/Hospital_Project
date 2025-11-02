package front.presentation.messages;

import front.logic.Service;
import logic.Message;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;


public class ModelMessages {
    private List<Message> messages = new ArrayList<>();
    private Service service;
    private String currentUserId;
    private List<String> activeUsers = new ArrayList<>();
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public ModelMessages() {
        this.service = Service.instance();
        this.currentUserId = "";
    }

    public void addMessage(Message msg) {
        messages.add(msg);
        support.firePropertyChange("messages", null, msg);
    }

    public void setActiveUsers(List<String> newUsers) {
        List<String> oldUsers = new ArrayList<>(this.activeUsers);
        this.activeUsers = new ArrayList<>(newUsers);
        support.firePropertyChange("activeUsers", oldUsers, this.activeUsers);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Service getService() {
        return service;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public List<String> getActiveUsers() { return activeUsers; }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
