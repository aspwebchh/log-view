package logView;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Scene mainScene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("logView.fxml"));
        primaryStage.setTitle("SLF4J日志分析工具");

        Scene scene = new Scene(root, 1280, 1024);

        scene.getStylesheets().add(
                getClass().getResource("logView.css")
                        .toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

        mainScene = scene;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
