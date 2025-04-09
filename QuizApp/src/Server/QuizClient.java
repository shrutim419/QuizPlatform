package Server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class QuizClient extends Application {

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        Button btn = new Button("Join Quiz");
        btn.setOnAction(e -> joinQuiz());

        root.getChildren().add(btn);
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Interactive Quiz!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void joinQuiz() {
        try {
            socket = new Socket("localhost", 12345); // Connect to server
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            // Send a message to the server (game code)
            outputStream.writeObject("JOIN_GAME");
            String message = (String) inputStream.readObject();
            System.out.println(message);

            // Additional game code to handle answers, leaderboards, etc.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
