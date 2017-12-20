package login;

import chatWindow.ChatController;
import chatWindow.Listener;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import until.SHA;
import userAction.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


public class RegisterController {
    private HttpURLConnection connection = null;
    private File fileImage;
    private boolean check;
    private String login;
    private String password;
    private String name;
    private String secondName;

    @FXML
    private JFXTextField idLogin;
    @FXML
    private JFXPasswordField idPass;
    @FXML
    private JFXTextField idName;
    @FXML
    private JFXTextField idSecondName;
    @FXML
    private JFXButton btnImageChoice;
    @FXML
    private ImageView img;
    @FXML
    private JFXButton btnRegister;

    @FXML
    void btnCancel(MouseEvent event) throws IOException {
        newScene(event, "views/LoginView.fxml", "Login");
    }

    @FXML
    void btnRegister(MouseEvent event) throws Exception {
        if (!Consts.checkConnection()) {
            Consts.showErrorDialog("Error connection!", "Нет соеденения с сервером.");
            return;
        }
        name = idName.getText();
        secondName = idSecondName.getText();
        login = idLogin.getText();
        password = idPass.getText();

        if (name.isEmpty() || secondName.isEmpty() || login.isEmpty() || password.isEmpty()) {
            Consts.showErrorDialog(null, "One of the fields is empty.");
            return;
        }

        name = CheckInput.deleteSpace(name);
        secondName = CheckInput.deleteSpace(secondName);
        login = CheckInput.deleteSpace(login);
        password = CheckInput.deleteSpace(password);
        name = CheckInput.firstUpperCase(name);
        secondName = CheckInput.firstUpperCase(secondName);

        if (check && (!CheckInput.checkForInputName(name) || !CheckInput.checkForInputName(secondName) ||
                !CheckInput.checkForLogin(login) || !CheckInput.checkPassword(password))) {
            Consts.showErrorDialog("Error Input", "Ошибка ввода");
            return;
        }
        if (fileImage != null) {
            try {
                byte[] imageInByte;
                BufferedImage original = Compression.compress(ImageIO.read(fileImage), 0.6f);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                ImageIO.write(original, "jpg", baos);
                baos.flush();
                imageInByte = baos.toByteArray();
                baos.close();

                String url = Consts.URL + "?operation=register";
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDefaultUseCaches(false);
                connection.setConnectTimeout(250);
                connection.setReadTimeout(250);
                connection.setRequestProperty("Content-Type", "application/octet-stream");
                connection.setRequestProperty("charset", "utf-8");
                connection.connect();


                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(imageInByte);
                wr.flush();
                wr.close();
                int code = connection.getResponseCode();
                System.out.println("register picture: " + code);
            } catch (Throwable cause) {
                cause.getStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        try {
            String url = Consts.URL + "?operation=register&login=" + login + "&password=" + SHA.encrypt(password) +
                    "&name=" + URLEncoder.encode(name, "UTF-8") + "&surname=" + URLEncoder.encode(secondName, "UTF-8");
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            System.out.println(SHA.encrypt(password));
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                List<String> cookies = connection.getHeaderFields().get(CookiesWork.COOKIES_HEADER);
                String[] vals = cookies.get(0).split("=");
                CookiesWork.cookie = vals[1];
                connection.disconnect();
                System.out.println(CookiesWork.cookie);
                newScene(event, "views/chatScene.fxml", "Мессенджер");
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                Consts.showErrorDialog(null, "The login may already be busy.");
                return;
            }
        } catch (Throwable cause) {
            cause.getStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    @FXML
    void btnChoice(MouseEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Выбор картинки");
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
        fc.getExtensionFilters().addAll(extFilterJPG);
        fileImage = fc.showOpenDialog(null);
        if (fileImage != null) {
            try {
                BufferedImage buffered = Compression.createResizedCopy(ImageIO.read(fileImage), 100, 100, true);
                Image image = SwingFXUtils.toFXImage(buffered, null);
                img.setImage(image);
            } catch (IOException ex) {
                fileImage = null;
            }
        }
    }

    private void newScene(MouseEvent event, String fxml, String name) throws IOException {
        (((Node) event.getSource()).getScene()).getWindow().hide();
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
        Parent window = (Pane) fmxlLoader.load();
        if (fxml.equals("views/chatScene.fxml")) {
            ChatController con = fmxlLoader.<ChatController>getController();
            Listener listener = new Listener(con);
            Thread thread = new Thread(listener);
            thread.start();
        }
        Scene scene = new Scene(window);
        Stage stage = new Stage();
        stage.setTitle(name);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/iconMessanger.png").toString()));
        stage.setResizable(false);
        stage.show();
    }

}

