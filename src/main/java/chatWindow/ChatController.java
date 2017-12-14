package chatWindow;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
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
    private ListView<?> firstListViewUser;
    @FXML
    private ListView<?> secondListViewDialog;
    @FXML
    private ListView<?> chatPane;
    @FXML
    private TextArea messageBox;
    @FXML
    private JFXButton buttonSend;



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

    }


    @FXML
    void btnClickContacts(MouseEvent event) {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void firstListViewUserListener(MouseEvent event) {

    }

    @FXML
    void secondListViewDialogListener(MouseEvent event) {

    }
}
