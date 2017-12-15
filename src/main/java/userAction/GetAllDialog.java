package userAction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetAllDialog {
    public static ArrayList<Dialog> allDialog = new ArrayList<>();

    public static ArrayList<Dialog> getAllDialog() {
        return allDialog;
    }

    public static void setAllDialog(ArrayList<Dialog> all) {
        allDialog = all;
    }

    public static void refreshDialog() {
            System.out.println("refresh");
            HttpURLConnection connection = null;
            URL url;
            int code;
            try {
                url = new URL(Consts.URL + "?operation=dialogs");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "windows-1251"));
                String json = in.readLine();
                in.close();
                connection.disconnect();
                allDialog = new Gson().fromJson(json, new TypeToken<ArrayList<Dialog>>() {
                }.getType());
            } catch (Exception e) {
            } finally {
                if (connection != null)
                    connection.disconnect();
            }

            for (Dialog dlg :
                    allDialog) {
                dlg.setPicture(GetUserInformation.getPictureSmallSize(dlg.getSecond()));
            }
    }

}
