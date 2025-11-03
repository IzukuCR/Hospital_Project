package logic;

import java.io.Serializable;

public class Message implements Serializable{
    private static final long serialVersionUID = 1L;
    private String content;
    private String sender;
    private String receiver;

    public Message(String content, String sender, String receiver) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
    @Override
    public String toString() {
        return "Message from " + sender + " to " + receiver + ": " + content;
    }
}
