package userAction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AllUser {
    private static ArrayList<User> list = new ArrayList<>();

    public static ArrayList<User> getList() {
        return list;
    }

    public static void setList(ArrayList<User> lst) {
        list = lst;
    }

    public static void allUser() {
        ArrayList<User> buf = new ArrayList();
        HttpURLConnection connection = null;
        URL url;
        int code;
        try {
            url = new URL(Consts.URL + "?operation=search");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            code = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "windows-1251"));
            String json = in.readLine();
            in.close();
            connection.disconnect();
            buf = new Gson().fromJson(json, new TypeToken<ArrayList<User>>() {
            }.getType());
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
                us.setPicture(GetUserInformation.getPictureSmallSize(us.getLogin()));
                list.add(us);
            }
        }
    }
}
