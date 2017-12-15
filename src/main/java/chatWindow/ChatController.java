package chatWindow;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import userAction.*;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    private ImageView imageUser;
    @FXML
    private Text textLogin;
    @FXML
    private Text textName;
    @FXML
    private Button btnDialog;
    @FXML
    private TextField textFieldSearch;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private JFXButton btnContacts;
    @FXML
    private ImageView imgSobes;
    @FXML
    private Text textLoginSobes;
    @FXML
    private VBox vBoxCenter;
    @FXML
    private ListView<User> firstListViewUser;
    @FXML
    private ListView<Dialog> secondListViewDialog;
    @FXML
    private ListView<?> chatPane;
    @FXML
    private TextArea messageBox;
    @FXML
    private JFXButton buttonSend;

    private ObservableList<User> usersList;
    private ObservableList<Dialog> dialogsList;

    public void setImageUser(BufferedImage imageUser) {
        this.imageUser.setImage(SwingFXUtils.toFXImage(imageUser, null));
    }

    public void setTextLogin(String textLogin) {
        this.textLogin.setText(textLogin);
    }

    public void setTextName(String textName) {
        this.textName.setText(textName);
    }

    @FXML
    void sendButtonAction(ActionEvent event) {

    }

    @FXML
    void sendMethod(KeyEvent event) {

    }

    @FXML
    void btnSendMessage(MouseEvent event) {

    }

    @FXML
    void clickBtnDialog(MouseEvent event) {
        secondListViewDialog.setVisible(true);
        firstListViewUser.setVisible(false);
        GetAllDialog.refreshDialog();
    }

    @FXML
    void btnClickContacts(MouseEvent event) {
        firstListViewUser.setVisible(true);
        secondListViewDialog.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        firstListViewUser.setVisible(false);
        secondListViewDialog.setVisible(true);
    }

    @FXML
    void firstListViewUserListener(MouseEvent event) {

    }

    @FXML
    void secondListViewDialogListener(MouseEvent event) {

    }

    public void setListView(ArrayList<User> list) {
        Platform.runLater(() -> {
            usersList = FXCollections.observableList(list);
            firstListViewUser.setItems(usersList);
            firstListViewUser.setCellFactory(new CellRenderUser());
        });
    }

    public void setSecondListViewDialog(ArrayList<Dialog> list) {
        Platform.runLater(() -> {

            for (Dialog dlg:
                 list) {
                System.out.println(dlg);
            }
            System.out.println("secondViewDialog SIZE: " + list.size());
            dialogsList = FXCollections.observableList(list);
            secondListViewDialog.setItems(dialogsList);
            secondListViewDialog.setCellFactory(new CellRenderDialog());
        });
    }
}
