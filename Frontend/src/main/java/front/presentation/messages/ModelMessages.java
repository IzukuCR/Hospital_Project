package front.presentation.messages;

import front.logic.Service;
import logic.Message;

import java.util.ArrayList;
import java.util.List;

public class ModelMessages {
    private List<Message> messages = new ArrayList<>();
    private Service service;
    private String currentUserId;

    public ModelMessages() {
        this.service = Service.instance();
        this.currentUserId = "";
    }

    public void addMessage(Message msg) {
        messages.add(msg);
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
}
