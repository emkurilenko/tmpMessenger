package chatWindow;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class CellRenderSticker implements Callback<ListView<Image>, ListCell<Image>> {
    @Override
    public ListCell<Image> call(ListView<Image> p) {

        ListCell<Image> cell = new ListCell<Image>() {

            @Override
            protected void updateItem(Image img, boolean bln) {
                super.updateItem(img, bln);
                setGraphic(null);
                setText(null);
                if (img != null) {
                    try {
                        HBox hBox = new HBox();
                        ImageView image = new ImageView(img);
                        image.setFitHeight(64);
                        image.setFitWidth(64);
                        hBox.getChildren().add(image);
                        hBox.setAlignment(Pos.CENTER);
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
