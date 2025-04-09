package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MyScoresUI extends Application {

    private String username;

    public MyScoresUI(String username) {
        this.username = username;
    }

    @Override
    public void start(Stage stage) {
        Label label = new Label("Hi " + username + ", here are your scores (UI coming soon)");

        VBox vbox = new VBox(20, label);
        vbox.setStyle("-fx-padding: 30; -fx-alignment: center;");

        Scene scene = new Scene(vbox, 400, 200);
        stage.setTitle("My Scores");
        stage.setScene(scene);
        stage.show();
    }
}
