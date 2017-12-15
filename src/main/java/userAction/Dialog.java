package userAction;

import java.awt.image.BufferedImage;

public class Dialog {
    private String second;
    private String name;
    private String lastMessage;
    private long date;
    private boolean unread;
    private BufferedImage picture;

    public BufferedImage getPicture() {
        return picture;
    }

    public void setPicture(BufferedImage picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Dialog() {
    }

    public Dialog(String second, String lastMessage, long date, boolean unread) {
        this.second = second;
        this.lastMessage = lastMessage;
        this.date = date;
        this.unread = unread;
    }

    @Override
    public String toString() {
        return "Dialog{" +
                "second='" + second + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", date=" + date +
                ", unread=" + unread +
                '}';
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}
