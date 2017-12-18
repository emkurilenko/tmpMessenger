package chatWindow;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import userAction.User;

public class CellRenderUser implements Callback<ListView<User>, ListCell<User>> {
    @Override
    public ListCell<User> call(ListView<User> p) {

        ListCell<User> cell = new ListCell<User>() {

            @Override
            protected void updateItem(User user, boolean bln) {
                super.updateItem(user, bln);
                setGraphic(null);
                setText(null);
                if (user != null) {
                    try {
                        HBox vBox = new HBox();

                        Text name = new Text(user.getName() + " " + user.getSurname());
                        ImageView imageView = new ImageView();
//                        imageView.setFitHeight(40);
//                        imageView.setFitWidth(40);
//                        imageView.setSmooth(true);
//                        imageView.setPreserveRatio(true);
                            ImageView statusImageView = new ImageView();
                            Image statusImage = new Image(getClass().getClassLoader().getResource("images/online.png").toString(), 16, 16, true, true);
                            statusImageView.setImage(statusImage);

                        Image image = SwingFXUtils.toFXImage(user.getPicture(), null);
                        imageView.setImage(image);
                        if(user.isOnline()){
                            vBox.getChildren().addAll(imageView, name,statusImageView);
                        }else {
                            vBox.getChildren().addAll(imageView, name);
                        }
                        vBox.setAlignment(Pos.CENTER_LEFT);
                        setGraphic(vBox);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return cell;
    }
}
