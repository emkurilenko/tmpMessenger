package chatWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import userAction.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class Listener implements Runnable {

    private String coockie;
    public static ChatController controller;
    private User user;

    public Listener(ChatController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        System.out.println("run");
        initializeUser();

        GetAllDialog.refreshDialog();
        controller.setDialogsList(GetAllDialog.getAllDialog());
        System.out.println(GetAllDialog.getAllDialog().size());


    }

    private void initializeUser() {
        User user = GetUserInformation.getInformation(CookiesWork.cookie);
        controller.setImageUser(GetUserInformation.getPictureFullSize(CookiesWork.cookie));
        controller.setTextLogin(user.getLogin());
        controller.setTextName(user.getName() + " " + user.getSurname());
    }


    public static void sendVoiceMessage(byte[] audio) {
        HttpURLConnection connection = null;
        URL url;
        int code = 0;
        try {
            url = new URL(Consts.URL + "?operation=sendSound");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "video/3gpp");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            connection.setRequestProperty("Content-Length", Integer.toString(audio.length));
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.write(audio);
            dos.flush();
            dos.close();
            code = connection.getResponseCode();
        } catch (Exception e) {
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        if(code == HttpURLConnection.HTTP_OK){
            send(new Message(CookiesWork.cookie, controller.getReciver(),"sound", Calendar.getInstance().getTimeInMillis()));
        }else{
            Consts.showErrorDialog("Ошибка отправления! Возможно проблемы с сетью.", "Звук не отправлен.");
        }
    }

    public static void send(Message msg){

        HttpURLConnection connection = null;
        URL url;
        int code;
        try {
            url = new URL(Consts.URL + "?operation=sendmessage");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            OutputStreamWriter wr= new OutputStreamWriter(connection.getOutputStream(),"windows-1251");
            String json = new Gson().toJson(msg);
            wr.write(json);
            wr.flush();
            wr.close();
            code = connection.getResponseCode();
        }
        catch (Exception e){
        }
        finally {
            if(connection!=null)
                connection.disconnect();
        }
    }

    public static ArrayList<Message> getMessage(String second){
        ArrayList<Message> bufMessages = null;
        HttpURLConnection connection = null;
        URL url;
        int code;
        try {
            url = new URL(Consts.URL + "?operation=messages&receiver=" + second);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            code = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "windows-1251"));
            String json = in.readLine();
            in.close();
            connection.disconnect();
            bufMessages = new Gson().fromJson(json, new TypeToken<ArrayList<Message>>(){}.getType());
        }
        catch (Exception e){
        }
        finally {
            if(connection!=null)
                connection.disconnect();
        }
        return bufMessages;
    }


    public static InputStream getSound(Message msg){
        InputStream is = null;
        HttpURLConnection connection = null;
        URL url;
        try {
            String str = Consts.URL + "?operation=getSound&sender=" +
                    msg.getSender() + "&receiver=" + msg.getReceiver() + "&date=" + msg.getDate();
            url = new URL(str);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            is = connection.getInputStream();
            connection.disconnect();
        }
        catch (Exception e){
        }
        finally {
            if(connection!=null)
                connection.disconnect();
        }
        return is;
    }
}
