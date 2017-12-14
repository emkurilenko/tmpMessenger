package chatWindow;

import userAction.AllUser;
import userAction.CookiesWork;
import userAction.GetUserInformation;
import userAction.User;

import javax.imageio.ImageIO;

public class Listener implements Runnable {

    private String coockie;
    public ChatController controller;
    private User user;

    public Listener(ChatController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        System.out.println("run");
        initializeUser();
        AllUser.allUser();
        controller.setListView(AllUser.getList());
    }

    private void initializeUser(){
        User user = GetUserInformation.getInformation(CookiesWork.cookie);
        controller.setImageUser(GetUserInformation.getPictureFullSize(CookiesWork.cookie));
        controller.setTextLogin(user.getLogin());
        controller.setTextName(user.getName() + " " + user.getSurname());
    }


}
