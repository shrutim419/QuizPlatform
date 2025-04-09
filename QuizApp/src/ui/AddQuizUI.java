package ui;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.util.ArrayList;
import java.util.List;

public class AddQuizUI extends Application {

    private TextField quizTitleField = new TextField();
    private List<QuestionInput> questions = new ArrayList<>();
    private VBox questionContainer = new VBox(10);

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Quiz Title:");
        quizTitleField.setPromptText("Enter quiz title");

        Button addQuestionBtn = new Button("Add Question");
        addQuestionBtn.setOnAction(e -> addQuestion());

        Button saveBtn = new Button("Save Quiz");
        saveBtn.setOnAction(e -> saveQuiz());

        VBox root = new VBox(15, titleLabel, quizTitleField, addQuestionBtn, questionContainer, saveBtn);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("Add Quiz");
        stage.setScene(scene);
        stage.show();
    }

    private void addQuestion() {
        QuestionInput q = new QuestionInput();
        questions.add(q);
        questionContainer.getChildren().add(q.getNode());
    }

    private void saveQuiz() {
        String quizTitle = quizTitleField.getText();
        if (quizTitle.isEmpty() || questions.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Quiz Title and at least 1 question are required.");
            return;
        }

        try (Connection conn = DB.DBConnection.getConnection()) {
            // Step 1: Insert into quizzes table
            String quizInsert = "INSERT INTO quizzes (title, created_by) VALUES (?, ?)";
            PreparedStatement quizStmt = conn.prepareStatement(quizInsert, PreparedStatement.RETURN_GENERATED_KEYS);
            quizStmt.setString(1, quizTitle);
            quizStmt.setInt(2, 1); // assuming admin user_id = 1 (you can make it dynamic)
            quizStmt.executeUpdate();

            ResultSet generatedKeys = quizStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Failed to get quiz ID.");
            }
            int quizId = generatedKeys.getInt(1);

            // Step 2: Insert each question
            String questionInsert = "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement questionStmt = conn.prepareStatement(questionInsert);

            for (AddQuizUI.QuestionInput q : questions) {
                List<String> opts = q.getOptions();
                int correctIndex = q.getCorrectAnswerIndex();

                if (opts.size() != 4 || correctIndex == -1) {
                    showAlert(Alert.AlertType.WARNING, "Each question must have 4 options and one selected answer.");
                    return;
                }

                questionStmt.setInt(1, quizId);
                questionStmt.setString(2, q.getQuestionText());
                questionStmt.setString(3, opts.get(0));
                questionStmt.setString(4, opts.get(1));
                questionStmt.setString(5, opts.get(2));
                questionStmt.setString(6, opts.get(3));
                questionStmt.setString(7, getOptionLetter(correctIndex)); // 'A', 'B', 'C', 'D'
                questionStmt.addBatch();
            }

            questionStmt.executeBatch();

            showAlert(Alert.AlertType.INFORMATION, "Quiz saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error saving quiz: " + e.getMessage());
        }
    }

    private String getOptionLetter(int index) {
        return switch (index) {
            case 0 -> "A";
            case 1 -> "B";
            case 2 -> "C";
            case 3 -> "D";
            default -> null;
        };
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }

    class QuestionInput {
        private TextField questionField = new TextField();
        private List<TextField> optionFields = new ArrayList<>();
        private ToggleGroup toggleGroup = new ToggleGroup();
        private VBox container = new VBox(5);

        public QuestionInput() {
            questionField.setPromptText("Enter your question");
            container.getChildren().add(new Label("Question:"));
            container.getChildren().add(questionField);

            for (int i = 0; i < 4; i++) {
                HBox optionBox = new HBox(5);
                RadioButton radio = new RadioButton();
                radio.setToggleGroup(toggleGroup);
                TextField optionText = new TextField();
                optionText.setPromptText("Option " + (i + 1));
                optionFields.add(optionText);
                optionBox.getChildren().addAll(radio, optionText);
                container.getChildren().add(optionBox);
            }

            container.setStyle("-fx-border-color: #999; -fx-padding: 10; -fx-background-color: #f9f9f9;");
        }

        public VBox getNode() {
            return container;
        }

        public String getQuestionText() {
            return questionField.getText();
        }

        public List<String> getOptions() {
            List<String> opts = new ArrayList<>();
            for (TextField field : optionFields) {
                opts.add(field.getText());
            }
            return opts;
        }

        public int getCorrectAnswerIndex() {
            for (int i = 0; i < toggleGroup.getToggles().size(); i++) {
                if (toggleGroup.getToggles().get(i).isSelected()) {
                    return i;
                }
            }
            return -1; // not selected
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
