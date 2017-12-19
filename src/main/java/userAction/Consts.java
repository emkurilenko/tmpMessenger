package userAction;

import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Consts {
    final public static String URL = "http://192.168.137.1:8080/mess/";

    public static Image byteToImage(byte[] arr) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new ByteArrayInputStream(arr));
            return SwingFXUtils.toFXImage(img, null);
        } catch (IOException e) {
            System.out.println("Err convert byte array to Image");
        }
        return null;
    }

    public static boolean checkConnection() {
        try {
            java.net.URL url = new URL(Consts.URL);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(100);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void showErrorDialog(String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
