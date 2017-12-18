package userAction;

import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUserInformation {
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
                    System.out.println("error code");
                    bi = ImageIO.read(isa);
                    is.close();
                    isa.close();
                    connection.disconnect();
                    return bi;
                } else {
                    bi = Consts.createResizedCopy(Compression.compress(bi, 1.0f),
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
                    System.out.println("file found 1");
                    connection.disconnect();
                    bi = ImageIO.read(isa);
                } else {
                    System.out.println("else");
                    connection.disconnect();
                    return bi;
                }
            } else {
                InputStream isa = new FileInputStream("src/main/resources/images/default.png");
                System.out.println("file found 2");
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
