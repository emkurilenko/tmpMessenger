package chatWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import until.Compression;
import userAction.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Listener implements Runnable {
    private static boolean isActive;
    private String coockie;
    public static ChatController controller;
    private static User user;
    private static Timer timer;

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Listener(ChatController controller) {
        this.controller = controller;
        isActive = true;
    }

    public static void disable() {
        isActive = false;
        timer.cancel();
        timer.purge();
    }

    @Override
    public void run() {
        initializeUser();
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                controller.setDialogsList(getDialog());
            }
        };
        timer.schedule(timerTask, 0, 5000);
    }

    public static User getUser() {
        return user;
    }

    public static void initializeUser() {
        user = getInformation(CookiesWork.cookie);
        controller.setImageUser(getPictureFullSize(CookiesWork.cookie));
        controller.setTextLogin(user.getLogin());
        controller.setTextName(user.getName() + " " + user.getSurname());
        System.out.println(user.getName());
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
            connection.setRequestProperty("Content-Type", "audio/mp3");
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
        if (code == HttpURLConnection.HTTP_OK) {
            send(new Message(CookiesWork.cookie, controller.getReciver(), "sound", Calendar.getInstance().getTimeInMillis()));
        } else {
            Consts.showErrorDialog("Ошибка отправления! Возможно проблемы с сетью.", "Звук не отправлен.");
        }
    }

    public static void send(Message msg) {

        HttpURLConnection connection = null;
        URL url;
        int code;
        try {
            url = new URL(Consts.URL + "?operation=sendmessage");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream(), "windows-1251");
            String json = new Gson().toJson(msg);
            wr.write(json);
            wr.flush();
            wr.close();
            code = connection.getResponseCode();
        } catch (Exception e) {
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public static ArrayList<Message> getMessage(String second, String bool) {
        ArrayList<Message> bufMessages = null;
        HttpURLConnection connection = null;
        URL url;
        int code;
        try {
            url = new URL(Consts.URL + "?operation=messages&receiver=" + second + "&all=" + bool);
            controller.setLastUpdate(System.currentTimeMillis());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            code = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "windows-1251"));
            String json = in.readLine();
            in.close();
            connection.disconnect();
            bufMessages = new Gson().fromJson(json, new TypeToken<ArrayList<Message>>() {
            }.getType());
        } catch (Exception e) {
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return bufMessages;
    }

    public synchronized static String getSound(Message msg) {
        InputStream is = null;
        HttpURLConnection connection = null;
        URL url;
        String fileName = "";
        try {
            String str = Consts.URL + "?operation=getSound&sender=" +
                    msg.getSender() + "&receiver=" + msg.getReceiver() + "&date=" + msg.getDate();
            url = new URL(str);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            int code = connection.getResponseCode();
            is = connection.getInputStream();

            fileName = "src/main/resources/tmpsound/record-" + msg.getSender() + "-" + msg.getReceiver() + "-" + String.valueOf(msg.getDate() / 1000) + ".wave";
            System.out.println(fileName);
            File path = new File(fileName);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(path);
                fos.write(Compression.toByteArray(is));
                fos.close();
            } catch (Exception e) {
                System.out.println("exception write sound");
            }
            // is.close();
            System.out.println("GET SOUND " + code);
            connection.disconnect();
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND!");
        } catch (Exception e) {
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return fileName;
    }

    public static ArrayList<Dialog> getDialog() {
        HttpURLConnection connection = null;
        URL url;
        ArrayList<Dialog> allDialog = null;
        try {
            url = new URL(Consts.URL + "?operation=dialogs");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "windows-1251"));
            String json = in.readLine();
            in.close();
            connection.disconnect();
            allDialog = new Gson().fromJson(json, new TypeToken<ArrayList<Dialog>>() {
            }.getType());
            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                Consts.showErrorDialog("Error Connection", "Ошибка загрузки диалогов!");
            }
        } catch (Exception e) {
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        for (Dialog dlg :
                allDialog) {
            dlg.setPicture(getPictureSmallSize(dlg.getSecond()));
        }
        return allDialog;
    }

    public static ArrayList<User> getAllSearchUser(String search) {
        ArrayList<User> list = new ArrayList<>();
        ArrayList<User> buf = new ArrayList();
        HttpURLConnection connection = null;
        URL url;
        try {
            url = new URL(Consts.URL + "?operation=search&value=" + search);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "windows-1251"));
            String json = in.readLine();
            in.close();
            connection.disconnect();
            buf = new Gson().fromJson(json, new TypeToken<ArrayList<User>>() {
            }.getType());
            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                Consts.showErrorDialog("Error connection", "Ошибка поиска пользователей.");
                return null;
            }
        } catch (Exception e) {
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        for (User us :
                buf) {
            if (us.getLogin().equals(CookiesWork.cookie)) {
                continue;
            } else {
                us.setPicture(getPictureSmallSize(us.getLogin()));
                list.add(us);
            }
        }
        return list;
    }

    public static ArrayList<User> getFriendsUser() {
        ArrayList<User> list = new ArrayList<>();
        HttpURLConnection connection = null;
        URL url;
        try {
            url = new URL(Consts.URL + "?operation=friends");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            int code = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "windows-1251"));
            String json = in.readLine();
            in.close();
            connection.disconnect();
            list = new Gson().fromJson(json, new TypeToken<ArrayList<User>>() {
            }.getType());
        } catch (Exception e) {
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        for (User us :
                list) {
            us.setPicture(getPictureSmallSize(us.getLogin()));
        }
        return list;
    }

    public static void setFriend(String login,String param){
        HttpURLConnection connection = null;
        URL url;
        try {
            url = new URL(Consts.URL+ "?operation=addFriend&second=" + login + "&add=" + param);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            connection.setRequestProperty("Cache-Control", "no-cache");
            int code = connection.getResponseCode();
            connection.disconnect();
            if(code != HttpURLConnection.HTTP_OK){
                Consts.showErrorDialog("Error","Ошибка добавления в друзья.");
            }
        }
        catch (Exception e){
        }
        finally {
            if(connection!=null)
                connection.disconnect();
        }
    }

    public static User getInformation(String login) {
        User user = null;
        URL url;
        HttpURLConnection connection = null;
        int code;
        try {
            String str = Consts.URL + "?operation=profile&type=info&login=" + login;
            url = new URL(str);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            connection.setRequestProperty("Cache-Control", "no-cache");
            code = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "windows-1251"));
            String inputLine = in.readLine();
            user = new Gson().fromJson(inputLine, User.class);
            in.close();
            connection.disconnect();
            if(code!=HttpURLConnection.HTTP_OK){
                Consts.showErrorDialog("Error","");
            }
        } catch (Exception e) {
            System.out.println("err getInformation");
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return user;
    }

    public static BufferedImage getPictureSmallSize(String login) {
        URL url;
        HttpURLConnection connection = null;
        int codeImage;
        BufferedImage bi = null;
        try {
            InputStream isa = new FileInputStream("src/main/resources/images/default40x40.png");
            String str = Consts.URL + "?operation=profile&type=image&login=" + login + "&size=small";
            url = new URL(str);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            codeImage = connection.getResponseCode();

            if (codeImage == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                bi = ImageIO.read(is);
                if (bi == null) {
                    bi = ImageIO.read(isa);
                    is.close();
                    isa.close();
                    connection.disconnect();
                    return bi;
                } else {
                    bi = Compression.createResizedCopy(Compression.compress(bi, 1.0f),
                            40, 40, true);
                }
            } else {
                bi = ImageIO.read(isa);
            }
            isa.close();
            connection.disconnect();
        } catch (FileNotFoundException e) {
            System.out.println("not found file");
        } catch (IOException e) {
            System.out.println("error get picture size");
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return bi;
    }

    public static BufferedImage getPictureFullSize(String login) {
        URL url;
        HttpURLConnection connection = null;
        int code;
        int codeImage;
        BufferedImage bi = null;
        System.out.println(login);
        try {
            String str = Consts.URL + "?operation=profile&type=image&login=" + login + "&size=full";
            url = new URL(str);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            codeImage = connection.getResponseCode();
            System.out.println(codeImage);
            if (codeImage == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                bi = ImageIO.read(is);
                if (bi == null) {
                    InputStream isa = new FileInputStream("src/main/resources/images/default.png");
                    connection.disconnect();
                    bi = ImageIO.read(isa);
                } else {
                    connection.disconnect();
                    return Compression.createResizedCopy(bi,100,100,true);
                }
            } else {
                InputStream isa = new FileInputStream("src/main/resources/images/default.png");
                connection.disconnect();
                bi = ImageIO.read(isa);
            }
        } catch (Exception e) {
            System.out.println("error get picture full size");
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return bi;
    }
}
