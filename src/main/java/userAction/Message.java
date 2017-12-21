package userAction;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Message {
    private String sender;
    private String receiver;
    private long date;
    private String message;
    private String type;
    private InputStream sound;
    private InputStream file;

    public Message() {
    }

    public Message(String sender, String receiver, String type, long date) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.date = date;
    }

    public Message(String sender, String receiver, String message, String type, long date) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InputStream getSound() {
        return sound;
    }

    public void setSound(InputStream sound) {
        this.sound = sound;
    }

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (date != message1.date) return false;
        if (sender != null ? !sender.equals(message1.sender) : message1.sender != null) return false;
        if (receiver != null ? !receiver.equals(message1.receiver) : message1.receiver != null) return false;
        if (message != null ? !message.equals(message1.message) : message1.message != null) return false;
        if (type != null ? !type.equals(message1.type) : message1.type != null) return false;
        if (sound != null ? !sound.equals(message1.sound) : message1.sound != null) return false;
        return file != null ? file.equals(message1.file) : message1.file == null;
    }

    @Override
    public int hashCode() {
        int result = sender != null ? sender.hashCode() : 0;
        result = 31 * result + (receiver != null ? receiver.hashCode() : 0);
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (sound != null ? sound.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
