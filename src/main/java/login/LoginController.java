package login;

import chatWindow.ChatController;
import chatWindow.Listener;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import until.SHA;
import userAction.Consts;
import userAction.CookiesWork;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import static login.Main.primaryStageObj;

public class LoginController {
    private HttpURLConnection connection = null;
    private Scene scene;

    @FXML
    private JFXTextField loginUser;
    @FXML
    private JFXPasswordField passLogin;
    @FXML
    private JFXButton btnLogin;
    @FXML
    private JFXButton btnRegister;
    private static ChatController con;

    @FXML
    void bntClickLogin(MouseEvent event) {
        if (!Consts.checkConnection()) {
            Consts.showErrorDialog("Error connection!", "No connection to the server.");
            return;
        }

        String login = loginUser.getText();
        String password = passLogin.getText();
        System.out.println(login);
        System.out.println(password);

        if (login.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Введите данные! ");
            alert.showAndWait();
            return;
        }

       /* if(!CheckInput.checkForLogin(login) || !CheckInput.checkPassword(password)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Ошибка ввода!");
            alert.setContentText("Логин: Символов>2. Только буквы и цифры");
            alert.showAndWait();
            return;
        }*/

        try {
            String url = Consts.URL + "?operation=login&login=" + URLEncoder.encode(login, "UTF-8") + "&password=" + SHA.encrypt(password);
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setDefaultUseCaches(false);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                List<String> cookies = connection.getHeaderFields().get(CookiesWork.COOKIES_HEADER);
                String[] vals = cookies.get(0).split("=");
                CookiesWork.cookie = vals[1];
                connection.disconnect();

                (((Node) event.getSource()).getScene()).getWindow().hide();
                FXMLLoader fmxlLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/chatScene.fxml"));
                Parent window = (Pane) fmxlLoader.load();
                con = fmxlLoader.<ChatController>getController();
                Listener listener = new Listener(con);
                Thread thread = new Thread(listener);
                thread.start();

                Scene scene = new Scene(window);
                primaryStageObj.setScene(scene);
                primaryStageObj.show();
                //Смена Активити
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                Consts.showErrorDialog(null, "User not found/password field.");
                return;
            }
            System.out.println(connection.getResponseCode());
            System.out.println(CookiesWork.cookie);
        } catch (Throwable cause) {
            cause.getStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @FXML
    void btnClickRegister(MouseEvent event) throws IOException {
        if (!Consts.checkConnection()) {
            Consts.showErrorDialog("Error connection!", "No connection to the server.");
            return;
        }
        (((Node) event.getSource()).getScene()).getWindow().hide();
        Parent parent = FXMLLoader.load(getClass().getClassLoader().getResource("views/registerScene.fxml"));
        Scene scene = new Scene(parent);
        primaryStageObj.setScene(scene);
        primaryStageObj.show();
    }

}
