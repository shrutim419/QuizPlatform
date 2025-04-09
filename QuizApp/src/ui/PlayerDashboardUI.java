package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import DB.DBConnection;

public class PlayerDashboardUI extends Application {

    private static String username;

    public static void setUsername(String user) {
        username = user;
    }

    @Override
    public void start(Stage stage) {
        Button takeQuizBtn = new Button("Take Quiz");
        Button myScoresBtn = new Button("View My Scores");

        takeQuizBtn.setOnAction(e -> {
            TextInputDialog quizCodeDialog = new TextInputDialog();
            quizCodeDialog.setTitle("Enter Quiz Code");
            quizCodeDialog.setHeaderText(null);
            quizCodeDialog.setContentText("Please enter the quiz code:");

            quizCodeDialog.showAndWait().ifPresent(quizCode -> {
                if (quizCode.trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Quiz code cannot be empty.");
                } else if (isQuizCodeValid(quizCode.trim())) {
                    try {
                        QuizListUI quizListUI = new QuizListUI(username, quizCode.trim());
                        Stage quizStage = new Stage();
                        quizListUI.start(quizStage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Failed to start the quiz.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Quiz Code!");
                }
            });
        });

        myScoresBtn.setOnAction(e -> {
            try {
                MyScoresUI myScoresUI = new MyScoresUI(username);
                Stage scoreStage = new Stage();
                myScoresUI.start(scoreStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox vbox = new VBox(15, takeQuizBtn, myScoresBtn);
        vbox.setStyle("-fx-padding: 30; -fx-alignment: center;");
        Scene scene = new Scene(vbox, 300, 200);

        stage.setTitle("Player Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private boolean isQuizCodeValid(String quizCode) {
        String sql = "SELECT * FROM quizzes WHERE code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, quizCode);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
