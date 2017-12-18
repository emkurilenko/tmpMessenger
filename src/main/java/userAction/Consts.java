package userAction;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
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

    public static BufferedImage createResizedCopy(java.awt.Image originalImage,
                                                  int scaledWidth, int scaledHeight,
                                                  boolean preserveAlpha) {
        System.out.println("resizing...");
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = inputStream.read(buffer);
            if (read != -1)
                out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
    }

}
