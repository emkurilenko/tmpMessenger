package chatWindow;

import chatWindow.CellRenderDialog;
import chatWindow.CellRenderUser;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import until.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
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
import until.bubble.BubbleSpec;
import until.bubble.BubbledLabel;
import userAction.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private ListView<Dialog> listViewDialog;
    @FXML
    private ListView<User> listViewUsers;
    @FXML
    public JFXSpinner spinner;
    @FXML
    private ListView chatPane;
    @FXML
    private VBox SearchPanel;
    @FXML
    private TextArea messageBox;
    @FXML
    private JFXButton buttonSend;
    @FXML
    private JFXButton btnRecord;
    @FXML
    private ImageView microphoneImageView;

    private ObservableList<User> usersList;
    private ObservableList<Dialog> dialogsList;

    private String reciver;
    Image userSmallImage = SwingFXUtils.toFXImage(GetUserInformation.getPictureSmallSize(CookiesWork.cookie), null);
    Image reciverSmallImage;
    Image microphoneActiveImage = new Image(getClass().getClassLoader().getResource("images/microphone-active.png").toString());
    Image microphoneInactiveImage = new Image(getClass().getClassLoader().getResource("images/microphone.png").toString());


    public void recordVoiceMessage(MouseEvent event) throws IOException {
        if (VoiceUtil.isRecording()) {
            Platform.runLater(() -> {
                        microphoneImageView.setImage(microphoneInactiveImage);
                    }
            );
            VoiceUtil.setRecording(false);
        } else {
            Platform.runLater(() -> {
                        microphoneImageView.setImage(microphoneActiveImage);

                    }
            );
            VoiceRecorder.captureAudio();
        }
    }

    public synchronized void addToChat(Message msg) {
        Text dateText = new Text();
        Date data = new Date(msg.getDate());
        String hours = String.valueOf(data.getHours());
        String minutes = String.valueOf(data.getMinutes());
        if (data.getHours() < 10) hours = "0" + hours;
        if (data.getMinutes() < 10) minutes = "0" + minutes;
        dateText.setText(hours + ":" + minutes);
        dateText.setFont(Font.font("SansSerif Regular", 12));

        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                ImageView profileImage = new ImageView(reciverSmallImage);
                profileImage.setFitHeight(32);
                profileImage.setFitWidth(32);
                BubbledLabel bl6 = new BubbledLabel();
                if (msg.getType().equals("text")) {
                    bl6.setText(msg.getReceiver() + ": " + msg.getMessage());

                } else {
                    ImageView imageview = new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toString()));
                    bl6.setGraphic(imageview);
                    bl6.setText("Sent a voice message!");
                    // InputStream is = Listener.getSound(msg);
                    System.out.println("rec sound");
                    // VoicePlayback.playAudio(Consts.toByteArray(is));
                }
                bl6.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                HBox x = new HBox();
                bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                x.getChildren().addAll(dateText,profileImage, bl6);
                // x.getChildren().addAll(bl6);
                return x;
            }
        };

        othersMessages.setOnSucceeded(event -> {
            chatPane.getItems().add(othersMessages.getValue());
        });

        Task<HBox> yourMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                ImageView profileImage = new ImageView(userSmallImage);
                profileImage.setFitHeight(32);
                profileImage.setFitWidth(32);

                BubbledLabel bl6 = new BubbledLabel();
                if (msg.getType().equals("text")) {
                    bl6.setText(msg.getMessage());
                } else {
                    bl6.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toString())));
                    bl6.setText("Sent a voice message!");
                    //InputStream is = Listener.getSound(msg);
                    System.out.println("sound my");
                    //VoicePlayback.playAudio(Consts.toByteArray(is));
                }
                bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                        null, null)));
                HBox x = new HBox();
                x.setMaxWidth(chatPane.getWidth() - 20);
                x.setAlignment(Pos.TOP_RIGHT);
                bl6.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                x.getChildren().addAll(bl6, profileImage,dateText);

                return x;
            }
        };
        yourMessages.setOnSucceeded(event -> chatPane.getItems().add(yourMessages.getValue()));

        if (msg.getSender().equals(textLogin.getText())) {
            Thread t2 = new Thread(yourMessages);
            t2.setDaemon(true);
            t2.start();
        } else {
            Thread t = new Thread(othersMessages);
            t.setDaemon(true);
            t.start();
        }
    }

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
    void onClickSearch(MouseEvent event) {
        String text = textFieldSearch.getText();
        if (!text.isEmpty()) {
            Platform.runLater(() -> {
                SearchUser.allUser(text);
                setListSearchList(SearchUser.getList());
            });
        }
    }

    @FXML
    void sendButtonAction() throws IOException {
        String msg = messageBox.getText();
        if (!messageBox.getText().isEmpty()) {
            Message message = new Message(CookiesWork.cookie, reciver, msg, "text", Calendar.getInstance().getTimeInMillis());
            Listener.send(message);
            messageBox.clear();
            addToChat(message);
        }
    }

    @FXML
    void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    @FXML
    void btnSendMessage(MouseEvent event) {

    }

    @FXML
    void clickBtnDialog(MouseEvent event) {
        GetAllDialog.refreshDialog();
        setDialogsList(GetAllDialog.getAllDialog());

        listViewDialog.setVisible(true);
        SearchPanel.setVisible(false);
    }

    @FXML
    void btnClickContacts(MouseEvent event) {
        SearchPanel.setVisible(true);
        listViewDialog.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewDialog.setVisible(true);
        SearchPanel.setVisible(false);

        messageBox.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                try {
                    sendButtonAction();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ke.consume();
            }
        });
    }

    @FXML
    void listViewDialogListener(MouseEvent event) {
        reciver = listViewDialog.getSelectionModel().getSelectedItem().getSecond();
        chatPane.getItems().clear();
        reciverSmallImage = SwingFXUtils.toFXImage(GetUserInformation.getPictureSmallSize(reciver), null);
        ArrayList<Message> buf = Listener.getMessage(reciver);
        for (Message msg :
                buf) {
            System.out.println(msg);
            addToChat(msg);
        }
    }

    @FXML
    void listViewUsersListener(MouseEvent event) {

    }

    public void setListSearchList(ArrayList<User> list) {
        Platform.runLater(() -> {
            usersList = FXCollections.observableList(list);
            listViewUsers.setItems(usersList);
            listViewUsers.setCellFactory(new CellRenderUser());
            listViewUsers.refresh();
        });
    }

    public void setDialogsList(ArrayList<Dialog> list) {
        Platform.runLater(() -> {
            System.out.println("secondViewDialog SIZE: " + list.size());
            dialogsList = FXCollections.observableList(list);
            listViewDialog.setItems(dialogsList);
            listViewDialog.setCellFactory(new CellRenderDialog());
        });
    }
}
