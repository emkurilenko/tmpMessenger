package chatWindow;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import login.Main;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.controlsfx.control.cell.ColorGridCell;
import until.Recorder;
import until.bubble.BubbleSpec;
import until.bubble.BubbledLabel;
import userAction.*;
import userAction.Dialog;

import javax.sound.sampled.LineUnavailableException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static login.Main.deleteAllFilesFolder;

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
    private JFXListView chatPane;
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
    @FXML
    private Text loginReciver;
    @FXML
    private ImageView imgReciver;
    @FXML
    private JFXButton btnAddFriend;
    @FXML
    private ImageView btnImageFriend;
    @FXML
    private Label labelNotFoundDialog;
    @FXML
    private JFXListView stikersListView;

    @FXML
    private Pane paneStickers;

    private ObservableList<User> usersList;
    private ObservableList<Dialog> dialogsList;
    private ObservableList<Image> stickers;
    private ObservableList<Message> messagesList;
    private ArrayList<Message> listMessage;


    private String reciver;
    private long lastUpdate;
    private ArrayList<Message> bufMessage;
    private Timer timer;
    private boolean firstTime = true;
    private Recorder recorder;
    private File bufferRecord;
    private User chooseUser;

    private int DISPLAYED_MESSAGES = 8;
    private Thread threadAddMessage;

    private Image microphoneActiveImage = new Image(getClass().getClassLoader().getResource("images/microphone-active.png").toString());
    private Image microphoneInactiveImage = new Image(getClass().getClassLoader().getResource("images/microphone.png").toString());
    private Image addFriend = new Image(getClass().getClassLoader().getResource("images/if_user_half_add.png").toString());
    private Image removeFriend = new Image(getClass().getClassLoader().getResource("images/if_user_half_remove.png").toString());

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
        if (!recorder.isRecord()) {
            Platform.runLater(() -> {
                try {
                    bufferRecord = new File("buffer.wave");
                    microphoneImageView.setImage(microphoneActiveImage);
                    recorder.beginRecording(bufferRecord);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
            });
            recorder.setRecord(true);
        } else {
            Platform.runLater(() -> {
                try {
                    microphoneImageView.setImage(microphoneInactiveImage);
                    recorder.endRecording();
                    Listener.sendVoiceMessage(org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(bufferRecord)));
                    bufferRecord.delete();
                    recorder.setRecord(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
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
                    bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
                }
                if (msg.getType().equals("sound")) {
                    bl6.setText(msg.getSender() + ": " + "Голосовое сообщение!");
                    ImageView imageview = new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toString()));

                    bl6.setGraphic(imageview);
                    bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
                }
                if (msg.getType().equals("sticker")) {
                    ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResource("images/sticker/sticker" + msg.getMessage() + ".png").toString()));
                    bl6.setGraphic(imageView);
                }

                HBox x = new HBox();
                bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                x.getChildren().addAll(dateText, bl6);
                x.setAlignment(Pos.CENTER_LEFT);
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
                    bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                            null, null)));
                }
                if (msg.getType().equals("sound")) {
                    bl6.setText("Голосовое сообщение!");
                    bl6.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toString())));
                    bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                            null, null)));
                }
                if (msg.getType().equals("sticker")) {
                    ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResource("images/sticker/sticker" + msg.getMessage() + ".png").toString()));
                    bl6.setGraphic(imageView);
                }

                HBox x = new HBox();
                x.setMaxWidth(chatPane.getWidth() - 20);
                x.setAlignment(Pos.TOP_RIGHT);
                bl6.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                x.getChildren().addAll(bl6, dateText);
                x.setAlignment(Pos.CENTER_RIGHT);
                return x;
            }
        };
        yourMessages.setOnSucceeded(event -> chatPane.getItems().add(yourMessages.getValue()));

        if (msg.getSender().equals(textLogin.getText())) {
            threadAddMessage = new Thread(yourMessages);
        } else {
            threadAddMessage = new Thread(othersMessages);
        }
        threadAddMessage.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            chatPane.scrollTo(bufMessage.size() - 1);
        });
    }


    @FXML
    void chooseStickers(MouseEvent event) {
        Platform.runLater(() -> {
            if (!stikersListView.getItems().isEmpty()) {
                if (stikersListView.getSelectionModel().getSelectedItem() == null) {
                    return;
                }
                int index = stikersListView.getSelectionModel().getSelectedIndex();
                String str = String.valueOf(index + 1);
                Message message = new Message(CookiesWork.cookie, reciver, str, "sticker", Calendar.getInstance().getTimeInMillis());
                Listener.send(message);
            }
            stikersListView.getSelectionModel().clearSelection();
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
            resetTimer();
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
        Platform.runLater(() -> {
            String msg = messageBox.getText();
            if (messageBox.getText().length() > 199) {
                Consts.showErrorDialog("Ошибка!", "Превышен лимит длины сообщения. Лимит 200 символов!");
            }
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
        });
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
        vBoxCenter.setVisible(false);
        Platform.runLater(() -> {
            if (!listViewUsers.getItems().isEmpty()) {
                listViewUsers.getItems().removeAll();
            }
            chatPane.getItems().clear();
            SearchPanel.setVisible(true);
            listViewDialog.setVisible(false);
            ArrayList<User> friends = Listener.getFriendsUser();
            if (!friends.isEmpty()) {
                labelNotFoundDialog.setVisible(false);
                usersList = FXCollections.observableList(friends);
                listViewUsers.setItems(usersList);
                listViewUsers.setCellFactory(new CellRenderUser());
                listViewUsers.refresh();
            }
        });
    }

    @FXML
    void btnFriendListener(MouseEvent event) {
        Platform.runLater(() -> {
            Listener.setFriend(chooseUser.getLogin(), String.valueOf(!chooseUser.isFriend()));
            setReciverInformation(chooseUser.getLogin());
        });

    }

    @FXML
    void clickBtnDialog(MouseEvent event) {
        Platform.runLater(() -> {
            if(!listViewDialog.getItems().isEmpty()){
                labelNotFoundDialog.setVisible(false);
            }
            vBoxCenter.setVisible(false);
            setDialogsList(Listener.getDialog());
            chatPane.getItems().clear();
            listViewDialog.setVisible(true);
            SearchPanel.setVisible(false);
        });
    }

    @FXML
    void chooseMessage(MouseEvent event) {
        Platform.runLater(() -> {
            if (!chatPane.getItems().isEmpty()) {
                if (chatPane.getSelectionModel().getSelectedItem() == null) {
                    return;
                }
                int index = chatPane.getSelectionModel().getSelectedIndex();
                System.out.println(index);
                Message msg = bufMessage.get(index);
                if (msg.getType().equals("sound")) {
                    try {
                        String musicFile = "src/main/resources/tmpsound/record-" + msg.getSender() + "-" + msg.getReceiver() + "-" + String.valueOf(msg.getDate() / 1000) + ".wave";
                        File file = new File(musicFile);
                        if (!file.exists()) {
                            String filestring = Listener.getSound(msg);
                            Media media = new Media(new File(filestring).toURI().toString());
                            MediaPlayer mediaPlayer = new MediaPlayer(media);
                            mediaPlayer.play();
                        } else {
                            Media media = new Media(new File(musicFile).toURI().toString());
                            MediaPlayer mediaPlayer = new MediaPlayer(media);
                            mediaPlayer.play();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                chatPane.getSelectionModel().clearSelection();
            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelNotFoundDialog.setVisible(false);
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
        recorder = new Recorder();
        loadStickers();
    }

    @FXML
    void listViewDialogListener() {
        Platform.runLater(() -> {
            if (!listViewDialog.getItems().isEmpty()) {
                labelNotFoundDialog.setVisible(false);
                if (listViewDialog.getSelectionModel().getSelectedItem() == null) {
                    return;
                }
                vBoxCenter.setVisible(true);
                reciver = listViewDialog.getSelectionModel().getSelectedItem().getSecond();
                setReciverInformation(reciver);
                resetTimer();
                messagesList = chatPane.getItems();
                timer = new Timer();
                try {
                    Thread thread = new Thread(() -> {
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                for (Message msg :
                                        Listener.getMessage(reciver, String.valueOf(true))) {
                                    if (!bufMessage.contains(msg)) {
                                        bufMessage.add(msg);
                                        addToChat(msg);
                                    }
                                }
                                firstTime = false;
                            }
                        };
                        timer.schedule(timerTask, 0, 2000);
                    });
                    thread.start();
                } catch (Exception e) {
                    resetTimer();
                    listViewDialogListener();
                }

            }
        });
    }

    private void endThreadAddMessage() {
        if (threadAddMessage != null) {
            try {
                threadAddMessage.join();
            } catch (InterruptedException e) {
            }
            threadAddMessage = null;
        }
    }

    @FXML
    void listViewUsersListener(MouseEvent event) {
        Platform.runLater(() -> {
            if (!listViewUsers.getItems().isEmpty()) {
                if (listViewUsers.getSelectionModel().getSelectedItem() == null) {
                    return;
                }
                vBoxCenter.setVisible(true);
                reciver = listViewUsers.getSelectionModel().getSelectedItem().getLogin();
                setReciverInformation(reciver);
                resetTimer();
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        for (Message msg :
                                Listener.getMessage(reciver, String.valueOf(true))) {
                            if (!bufMessage.contains(msg)) {
                                addToChat(msg);
                                bufMessage.add(msg);
                            }
                        }
                        firstTime = false;
                    }
                };
                timer.schedule(timerTask, 0, 2000);
            }
        });
    }

    private void setReciverInformation(String login) {
        Platform.runLater(() -> {
            chooseUser = Listener.getInformation(login);
            loginReciver.setText(chooseUser.getLogin());
            imgReciver.setImage(SwingFXUtils.toFXImage(Listener.getPictureFullSize(reciver), null));
            if (chooseUser.isFriend()) {
                btnImageFriend.setImage(removeFriend);
                btnAddFriend.setText("Удалить из друзей");
            } else if (!chooseUser.isFriend()) {
                btnImageFriend.setImage(addFriend);
                btnAddFriend.setText("Добавить в друзья");
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
            if (listMessage != null) {
                if (!listMessage.isEmpty()) {
                    listMessage.clear();
                }
            }
            if (!bufMessage.isEmpty()) {
                bufMessage.clear();
            }
        }
        endThreadAddMessage();
    }

    @FXML
    void pressedImageUser(MouseEvent event) {
        btnEditProfileListener(event);
    }

    public void setListSearchList(ArrayList<User> list) {
        Platform.runLater(() -> {
            if (!listViewUsers.getItems().isEmpty()) {
                listViewUsers.getItems().removeAll();
            }
            labelNotFoundDialog.setVisible(false);
            usersList = FXCollections.observableList(list);
            listViewUsers.setItems(usersList);
            listViewUsers.setCellFactory(new CellRenderUser());
            listViewUsers.refresh();
        });
    }

    public void setDialogsList(ArrayList<Dialog> list) {
        Platform.runLater(() -> {
            if (list.isEmpty()) {
                labelNotFoundDialog.setVisible(true);
            }
            dialogsList = FXCollections.observableList(list);
            listViewDialog.setItems(dialogsList);
            listViewDialog.setCellFactory(new CellRenderDialog());
        });
    }

    public void setStikersList(ArrayList<Image> list) {
        Platform.runLater(() -> {
            stickers = FXCollections.observableList(list);
            stikersListView.setItems(stickers);
            stikersListView.setCellFactory(new CellRenderSticker());
        });
    }


    public void loadStickers() {
        ArrayList<Image> arrayList = new ArrayList<>();
        for (int i = 1; i < 29; i++) {
            arrayList.add(new Image(getClass().getClassLoader().getResource("images/sticker/sticker" + i + ".png").toString()));
        }
        setStikersList(arrayList);
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
