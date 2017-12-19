package chatWindow;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import login.Main;
import until.VoicePlayback;
import until.VoiceRecorder;
import until.VoiceUtil;
import until.bubble.BubbleSpec;
import until.bubble.BubbledLabel;
import userAction.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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
    @FXML
    private JFXButton btnEditProfile;

    private ObservableList<User> usersList;
    private ObservableList<Dialog> dialogsList;
    private ArrayList<Message> listMessage;

    private String reciver;
    private long lastUpdate;
    private ArrayList<Message> bufMessage;
    private Timer timer;
    private boolean firstTime = true;

    Image microphoneActiveImage = new Image(getClass().getClassLoader().getResource("images/microphone-active.png").toString());
    Image microphoneInactiveImage = new Image(getClass().getClassLoader().getResource("images/microphone.png").toString());

    public String getReciver() {
        return reciver;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

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

    @FXML
    void btnEditProfileListener(MouseEvent event) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("views/editScene.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Edit Window");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());
        stage.showAndWait();
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
                BubbledLabel bl6 = new BubbledLabel();
                if (msg.getType().equals("text")) {
                    bl6.setText(msg.getSender() + ": " + msg.getMessage());

                }
                if (msg.getType().equals("sound")) {
                    bl6.setText("Sent a voice message!");
                    ImageView imageview = new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toString()));
                    bl6.setGraphic(imageview);

                    System.out.println("rec sound");
                    // VoicePlayback.playAudio(Consts.toByteArray(Listener.getSound(msg)));
                }
                bl6.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                HBox x = new HBox();
                bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                x.getChildren().addAll(dateText, bl6);
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
                BubbledLabel bl6 = new BubbledLabel();
                if (msg.getType().equals("text")) {
                    bl6.setText(msg.getMessage());
                }
                if (msg.getType().equals("sound")) {
                    bl6.setText("Sent a voice message!");
                    bl6.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toString())));
                    System.out.println("sound my");
                    // VoicePlayback.playAudio(Consts.toByteArray(Listener.getSound(msg)));
                }
                bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                        null, null)));
                HBox x = new HBox();
                x.setMaxWidth(chatPane.getWidth() - 20);
                x.setAlignment(Pos.TOP_RIGHT);
                bl6.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                x.getChildren().addAll(bl6, dateText);

                return x;
            }
        };
        yourMessages.setOnSucceeded(event -> chatPane.getItems().add(yourMessages.getValue()));
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread t2;
        if (msg.getSender().equals(textLogin.getText())) {
            t2 = new Thread(yourMessages);
        } else {
            t2 = new Thread(othersMessages);
        }
        t2.setDaemon(true);
        t2.start();
        Platform.runLater(() -> {
            chatPane.scrollTo(bufMessage.size() - 1);
        });
    }

    public void setImageUser(BufferedImage imageUser) {
        this.imageUser.setImage(SwingFXUtils.toFXImage(imageUser, null));
    }

    @FXML
    void btnRefreshListener(MouseEvent event) {
        Platform.runLater(() -> {
            listMessage = Listener.getMessage(reciver, String.valueOf(firstTime));
        });
    }

    @FXML
    void btnHideDialogListener(MouseEvent event) {
        Platform.runLater(() -> {
            vBoxCenter.setVisible(false);
        });
    }

    public void setTextLogin(String textLogin) {
        this.textLogin.setText(textLogin);
    }

    public void setTextName(String textName) {
        this.textName.setText(textName);
    }

    public ImageView getImageUser() {
        return imageUser;
    }

    public Text getTextName() {
        return textName;
    }

    @FXML
    void onClickSearch(MouseEvent event) {
        String text = textFieldSearch.getText();
        if (!text.isEmpty()) {
            Platform.runLater(() -> {
                setListSearchList(Listener.getAllSearchUser(text));
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
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("задержка отправки сообщения");
            }
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
    void btnClickContacts(MouseEvent event) {
        chatPane.getItems().clear();
        SearchPanel.setVisible(true);
        listViewDialog.setVisible(false);
    }

    @FXML
    void clickBtnDialog(MouseEvent event) {
        setDialogsList(Listener.getDialog());
        chatPane.getItems().clear();
        listViewDialog.setVisible(true);
        SearchPanel.setVisible(false);
    }

    @FXML
    void chooseMessage(MouseEvent event){
        Platform.runLater(() -> {
            System.out.println("CHECK +++++++++++++++++++++++");
            int index = chatPane.getSelectionModel().getSelectedIndex();
            System.out.println(index);
            Message message = bufMessage.get(index);
            System.out.println("THIS MESSAGE: " + message);
            if (message.getType().equals("sound")) {
                try {
                    VoicePlayback.playAudio(Compression.toByteArray(Listener.getSound(message)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBoxCenter.setVisible(false);
        listViewDialog.setVisible(true);
        SearchPanel.setVisible(false);
        bufMessage = new ArrayList<Message>();
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
    void listViewDialogListener() {
        vBoxCenter.setVisible(true);
        Platform.runLater(() -> {
            if (!listViewDialog.getItems().isEmpty()) {
                if (listViewDialog.getSelectionModel().getSelectedItem() == null) {
                    return;
                }
                reciver = listViewDialog.getSelectionModel().getSelectedItem().getSecond();
                resetTimer();
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        listMessage = Listener.getMessage(reciver, String.valueOf(firstTime));
                        for (Message msg :
                                listMessage) {
                            addToChat(msg);
                            System.out.println(msg);
                            bufMessage.add(msg);
                        }
                        firstTime = false;
                    }
                };
                timer.schedule(timerTask, 0, 1500);
            }

        });
    }

    @FXML
    void listViewUsersListener(MouseEvent event) {
        vBoxCenter.setVisible(true);
        Platform.runLater(() -> {
            if (!listViewUsers.getItems().isEmpty()) {
                if (listViewDialog.getSelectionModel().getSelectedItem() == null) {
                    return;
                }
                resetTimer();
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        for (Message msg :
                                Listener.getMessage(reciver, String.valueOf(firstTime))) {
                            addToChat(msg);
                            bufMessage.add(msg);
                        }
                        firstTime = false;
                    }
                };
                timer.schedule(timerTask, 0, 1500);
            }
        });
    }

    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            chatPane.getItems().clear();
            lastUpdate = 0;
            firstTime = true;
            bufMessage.clear();
        }
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

    @FXML
    public void closeApplication() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void btnLogOutListener(MouseEvent event) {
        Platform.runLater(() -> {
            (((Node) event.getSource()).getScene()).getWindow().hide();
            CookiesWork.cookie = "";
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/views/loginScene.fxml"));
            Parent window = null;
            try {
                window = (Pane) fmxlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = Main.getPrimaryStage();
            Scene scene = new Scene(window);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.centerOnScreen();
            Listener.disable();
            stage.show();

        });
    }
}
