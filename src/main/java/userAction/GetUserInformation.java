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

    public static BufferedImage getPicture(String login) {
        URL url;
        HttpURLConnection connection = null;
        int code;
        int codeImage;
        BufferedImage bi = null;
        try {
            InputStream isa = new FileInputStream("src/main/resources/images/default40x40.png");
            String str = Consts.URL + "?operation=profile&type=image&login=" + login;
            url = new URL(str);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            codeImage = connection.getResponseCode();
            InputStream is = connection.getInputStream();
            if (codeImage == HttpURLConnection.HTTP_OK) {
                bi = ImageIO.read(is);
                if (bi == null) {
                    // Const.createResizedCopy(bi,40, 40, true);
                    bi = ImageIO.read(isa);
                } else {
                    bi = Consts.createResizedCopy(Compression.compress(bi, 0.5f),
                            40, 40, true);
               /*     user.setPicture(Consts.createResizedCopy(Compression.compress(bi, 0.5f),
                            40, 40, true));*/
                }
            } else {
                System.out.println("ololo");
                bi = ImageIO.read(isa);
                // user.setPicture(ImageIO.read(isa));
            }
            is.close();
            isa.close();
            connection.disconnect();
        }catch (FileNotFoundException e){
            System.out.println("not found file");
        }
        catch (Exception e) {
            System.out.println("error get picture size");
        }
        finally {
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
        try {
            String str = Consts.URL + "?operation=profile&type=image&login=" + login;
            url = new URL(str);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", CookiesWork.cookie);
            codeImage = connection.getResponseCode();
            if (codeImage == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                bi = ImageIO.read(is);
                if (bi == null) {
                    InputStream isa = new FileInputStream("src/main/resources/images/default.png");
                    System.out.println("file found");
                    bi = ImageIO.read(isa);
                }
            } else {
                InputStream isa = new FileInputStream("src/main/resources/images/default.png");
                System.out.println("file found");
                bi = ImageIO.read(isa);
            }
            connection.disconnect();
        } catch (Exception e) {
            System.out.println("error get picture full size");
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return bi;
    }
}
