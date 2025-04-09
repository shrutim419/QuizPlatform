package Server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Helloworld extends Application {
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button("Hello, World!");
        btn.setOnAction(e -> System.out.println("Hello, JavaFX!"));

        Scene scene = new Scene(btn, 200, 100);
        primaryStage.setTitle("JavaFX Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
