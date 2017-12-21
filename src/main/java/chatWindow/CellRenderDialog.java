package chatWindow;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;
import userAction.Dialog;

import java.util.Date;

public class CellRenderDialog implements Callback<ListView<Dialog>, ListCell<Dialog>> {
    Image statusImage = new Image(getClass().getClassLoader().getResource("images/online.png").toString(), 16, 16, true, true);
    @Override
    public ListCell<Dialog> call(ListView<Dialog> p) {

        ListCell<Dialog> cell = new ListCell<Dialog>() {

            @Override
            protected void updateItem(Dialog dlg, boolean bln) {
                super.updateItem(dlg, bln);
                setGraphic(null);
                setText(null);
                if (dlg != null) {
                    try {
                        HBox hBox = new HBox();

                        ImageView statusImageView = new ImageView();
                        statusImageView.setImage(statusImage);

                        Text second = new Text(dlg.getName());
                        second.setFont(Font.font("SansSerif Regular", 16));
                        second.setFill(Color.valueOf("#01579B"));

                        Image image = SwingFXUtils.toFXImage(dlg.getPicture(), null);
                        ImageView imageView = new ImageView(image);


                        Text dateText = new Text();
                        Date data = new Date(dlg.getDate());
                        String hours = String.valueOf(data.getHours());
                        String minutes = String.valueOf(data.getMinutes());
                        if (data.getHours() < 10) hours = "0" + hours;
                        if (data.getMinutes() < 10) minutes = "0" + minutes;
                        dateText.setText(hours + ":" + minutes);
                        dateText.setFont(Font.font("SansSerif Regular", 12));

                        Text lastMessage = null;
                        if (dlg.getType().equals("text")) {
                            String message = dlg.getLastMessage();
                            if(message.length()>14){
                                message = message.substring(0,14);
                                message += "...";
                            }
                            lastMessage = new Text(message);
                            lastMessage.setFont(Font.font("SansSerif Regular", 14));
                        }
                        if (dlg.getType().equals("sound")) {
                            lastMessage = new Text("sound message");
                            lastMessage.setFont(Font.font("SansSerif Regular", 14));
                        }
                        if(dlg.getType().equals("sticker")){
                            lastMessage = new Text("sticker");
                            lastMessage.setFont(Font.font("SansSerif Regular", 14));
                        }

                        if (dlg.isUnread()) {
                            hBox.setStyle("-fx-background-color: #fff2f2;");
                        }


                        VBox vBox = new VBox();

                        vBox.getChildren().addAll(second, lastMessage);
                        vBox.setAlignment(Pos.CENTER);
                        if(dlg.isOnline()){
                            hBox.getChildren().addAll(statusImageView,imageView, vBox, dateText);
                        }else {
                            hBox.getChildren().addAll(imageView, vBox, dateText);
                        }
                        //hBox.getChildren().addAll(vBox, dateText);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        setGraphic(hBox);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return cell;
    }
}
