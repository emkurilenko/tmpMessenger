package login;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;


public class Main extends Application {
    public static Stage primaryStageObj;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStageObj = primaryStage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("views/LoginView.fxml"));
        primaryStage.setTitle("Мессенджер");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/iconMessanger.png").toString()));
        Scene mainScene = new Scene(root);
        primaryStage.setResizable(false);
        primaryStage.setScene(mainScene);

        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            deleteAllFilesFolder("src/main/resources/tmpsound/");
            Platform.exit();
            System.exit(0);
        });
    }

    public static Stage getPrimaryStage() {
        return primaryStageObj;
    }

    public static void main(String[] args) throws IOException {
        launch(args);

    }

    @Override
    public void stop() {

    }



    public static void deleteAllFilesFolder(String path) {
        for (File myFile : new File(path).listFiles()) {
            if (myFile.isFile()) myFile.delete();
        }
    }
}

