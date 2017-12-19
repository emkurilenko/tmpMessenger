package userAction;

import java.awt.image.BufferedImage;

public class User {
    private int id;
    private String login;
    private String password;
    private String name;
    private String surname;
    private BufferedImage picture;
    private boolean online;
    private boolean friend;

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public User(int id, String login, String password, String name, String surname, BufferedImage picture) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.picture = picture;
    }

    public User(String login, String name, String surname) {

        this.login = login;
        this.name = name;
        this.surname = surname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public BufferedImage getPicture() {
        return picture;
    }

    public void setPicture(BufferedImage picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
