package chatWindow;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import userAction.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class EditController implements Initializable {
    private File fileImage;
    @FXML
    private ImageView userImage;

    @FXML
    private JFXTextField nameUser;

    @FXML
    private JFXTextField surnameUser;
    @FXML
    private JFXButton btnCancel;

    private ChatController chatController;

    @FXML
    void btnCancelListener(MouseEvent event) {
        (((Node) event.getSource()).getScene()).getWindow().hide();
    }

    @FXML
    void btnChangeProfile(MouseEvent event) {
        Platform.runLater(() -> {
            if (!Consts.checkConnection()) {
                Consts.showErrorDialog("Error connection!", "No connection to the server.");
                return;
            }
            String name = CheckInput.deleteSpace(nameUser.getText());
            String surname = CheckInput.deleteSpace(surnameUser.getText());

            if (name.isEmpty() || surname.isEmpty()) {
                Consts.showErrorDialog(null, "One of the fields is empty.");
                return;
            }

            if (!CheckInput.checkForInputName(name) || !CheckInput.checkForInputName(surname)) {
                Consts.showErrorDialog("Error Input", "Check input!");
                return;
            }

            if (fileImage != null) {
                Thread thread = new Thread(() -> {
                    HttpURLConnection connection = null;
                    URL url;
                    try {
                        byte[] imageInByte;
                        BufferedImage original = Compression.compress(ImageIO.read(fileImage), 0.5f);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        ImageIO.write(original, "jpg", baos);
                        baos.flush();
                        imageInByte = baos.toByteArray();

                        baos.close();
                        url = new URL(Consts.URL + "?operation=editProfileImage");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Cookie", CookiesWork.cookie);
                        connection.setRequestProperty("Content-Type", "application/octet-stream");
                        connection.setRequestProperty("charset", "utf-8");
                        connection.setRequestProperty("Content-Length", Integer.toString(imageInByte.length));
                        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                        dos.write(imageInByte);
                        dos.flush();
                        dos.close();
                        int code = connection.getResponseCode();
                        System.out.println(code);
                        if (code != HttpURLConnection.HTTP_OK) {
                            Consts.showErrorDialog("Error connection.", "Ошибка загрузки изображения");
                            return;
                        }
                    } catch (Exception e) {
                    } finally {
                        if (connection != null)
                            connection.disconnect();
                    }
                });
                thread.start();
            }


            HttpURLConnection connection = null;
            URL url;
            try {
                String str = Consts.URL + "?operation=editProfile&name=" +
                        name + "&surname=" + surname;
                url = new URL(str);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                connection.setRequestProperty("Cache-Control", "no-cache");
                int code = connection.getResponseCode();
                connection.disconnect();
                if (code != HttpURLConnection.HTTP_OK) {
                    Consts.showErrorDialog("Error connection.", "Ошибка изменения профиля.");
                    return;
                }
                Thread.sleep(100);
                Listener.initializeUser();
                (((Node) event.getSource()).getScene()).getWindow().hide();
            } catch (Exception e) {
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
        });


    }

    @FXML
    void listenerImage(MouseEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choice picture");
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter extFilterJPEG = new FileChooser.ExtensionFilter("JPEG files (*.jpeg)", "*.jpeg");
        fc.getExtensionFilters().addAll(extFilterJPG, extFilterJPEG);
        fileImage = fc.showOpenDialog(null);
        if (fileImage != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(fileImage);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                userImage.setImage(image);
            } catch (IOException ex) {
                //ex.printStackTrace();
                fileImage = null;
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            nameUser.setText(Listener.getUser().getName());
            surnameUser.setText(Listener.getUser().getSurname());
            BufferedImage bufferedImage = Compression.createResizedCopy(GetUserInformation.getPictureFullSize(CookiesWork.cookie), 100, 100, true);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            userImage.setImage(image);
        });
    }


}
