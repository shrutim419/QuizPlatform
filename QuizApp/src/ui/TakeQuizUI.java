package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

public class TakeQuizUI extends Application {

    private String username;
    private int quizId;
    private List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private Map<Integer, String> userAnswers = new HashMap<>();

    public TakeQuizUI(String username, int quizId) {
        this.username = username;
        this.quizId = quizId;
    }

    private VBox container = new VBox(15);
    private ToggleGroup answerGroup = new ToggleGroup();
    private Label questionLabel = new Label();
    private List<RadioButton> options = new ArrayList<>();
    private Button nextBtn = new Button("Next");

    @Override
    public void start(Stage stage) {
        loadQuestions();

        if (questions.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "No questions found.");
            return;
        }

        questionLabel.setWrapText(true);
        container.setPadding(new Insets(20));
        container.getChildren().add(questionLabel);

        for (int i = 0; i < 4; i++) {
            RadioButton rb = new RadioButton();
            rb.setToggleGroup(answerGroup);
            options.add(rb);
            container.getChildren().add(rb);
        }

        nextBtn.setOnAction(e -> {
            recordAnswer();
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                showQuestion();
            } else {
                submitQuiz();
                stage.close();
            }
        });

        container.getChildren().add(nextBtn);
        showQuestion();

        Scene scene = new Scene(container, 600, 300);
        stage.setTitle("Take Quiz");
        stage.setScene(scene);
        stage.show();
    }

    private void loadQuestions() {
        try (Connection conn = DB.DBConnection.getConnection()) {
            String sql = "SELECT * FROM questions WHERE quiz_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                questions.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_answer")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showQuestion() {
        Question q = questions.get(currentIndex);
        questionLabel.setText((currentIndex + 1) + ". " + q.text);

        options.get(0).setText(q.optionA);
        options.get(1).setText(q.optionB);
        options.get(2).setText(q.optionC);
        options.get(3).setText(q.optionD);

        answerGroup.selectToggle(null); // clear previous selection
    }

    private void recordAnswer() {
        Toggle selected = answerGroup.getSelectedToggle();
        if (selected != null) {
            int selectedIdx = options.indexOf(selected);
            String answer = switch (selectedIdx) {
                case 0 -> "A";
                case 1 -> "B";
                case 2 -> "C";
                case 3 -> "D";
                default -> null;
            };
            userAnswers.put(questions.get(currentIndex).id, answer);
        }
    }

    private void submitQuiz() {
        int score = 0;
        for (Question q : questions) {
            String selected = userAnswers.get(q.id);
            if (selected != null && selected.equals(q.correctAnswer)) {
                score++;
            }
        }

        try (Connection conn = DB.DBConnection.getConnection()) {
            String sql = "INSERT INTO scores (username, quiz_id, score, total) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setInt(2, quizId);
            stmt.setInt(3, score);
            stmt.setInt(4, questions.size());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        showAlert(Alert.AlertType.INFORMATION, "Quiz Submitted! Your score: " + score + "/" + questions.size());
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }

    static class Question {
        int id;
        String text, optionA, optionB, optionC, optionD, correctAnswer;

        public Question(int id, String text, String a, String b, String c, String d, String correctAnswer) {
            this.id = id;
            this.text = text;
            this.optionA = a;
            this.optionB = b;
            this.optionC = c;
            this.optionD = d;
            this.correctAnswer = correctAnswer;
        }
    }
}
