package ui;

import dao.QuestionDAO;
import dao.QuizDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Question;

import java.util.*;

public class QuizListUI extends Application {
    private static String staticUsername;
    private static String staticQuizCode;

    private String username;
    private String quizCode;
    private String quizTitle;

    public QuizListUI() {
        // Default constructor for JavaFX launch()
        this.username = staticUsername;
        this.quizCode = staticQuizCode;
    }

    public QuizListUI(String username, String quizCode) {
        this.username = username;
        this.quizCode = quizCode;
        
    }

    @Override
    public void start(Stage stage) {
        List<Question> questions = QuestionDAO.getQuestionsByQuizCode(quizCode);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        String quizTitle = QuizDAO.getQuizTitleByCode(quizCode);
        Label welcome = new Label("Welcome " + username + " to Quiz: " + quizTitle);
        welcome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        root.getChildren().add(welcome);

        Map<Question, ToggleGroup> toggleGroupsMap = new LinkedHashMap<>();
        Map<Question, List<RadioButton>> optionButtonsMap = new HashMap<>();

        for (Question q : questions) {
            VBox qBox = new VBox(8);
            qBox.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-color: #f9f9f9;");
            Label qLabel = new Label("Q: " + q.getQuestionText());
            qLabel.setStyle("-fx-font-weight: bold;");

            ToggleGroup group = new ToggleGroup();
            RadioButton a = new RadioButton("A. " + q.getOptionA());
            RadioButton b = new RadioButton("B. " + q.getOptionB());
            RadioButton c = new RadioButton("C. " + q.getOptionC());
            RadioButton d = new RadioButton("D. " + q.getOptionD());

            a.setToggleGroup(group);
            b.setToggleGroup(group);
            c.setToggleGroup(group);
            d.setToggleGroup(group);

            List<RadioButton> buttons = Arrays.asList(a, b, c, d);
            optionButtonsMap.put(q, buttons);

            qBox.getChildren().addAll(qLabel, a, b, c, d);
            toggleGroupsMap.put(q, group);
            root.getChildren().add(qBox);
        }

        Label scoreLabel = new Label();
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button submitBtn = new Button("Submit Quiz");
        submitBtn.setOnAction(e -> {
            for (Map.Entry<Question, ToggleGroup> entry : toggleGroupsMap.entrySet()) {
                if (entry.getValue().getSelectedToggle() == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Incomplete Quiz");
                    alert.setHeaderText(null);
                    alert.setContentText("Please answer all questions before submitting!");
                    alert.show();
                    return;
                }
            }

            int correct = 0;
            int total = questions.size();

            for (Map.Entry<Question, ToggleGroup> entry : toggleGroupsMap.entrySet()) {
                Question q = entry.getKey();
                ToggleGroup group = entry.getValue();
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                String selectedOption = selected.getText().substring(0, 1);

                List<RadioButton> buttons = optionButtonsMap.get(q);
                for (RadioButton btn : buttons) {
                    btn.setDisable(true);
                    String option = btn.getText().substring(0, 1);
                    if (option.equalsIgnoreCase(q.getCorrectOption())) {
                        btn.setStyle("-fx-text-fill: green;");
                    } else if (btn.equals(selected)) {
                        btn.setStyle("-fx-text-fill: red;");
                    }
                }

                if (selectedOption.equalsIgnoreCase(q.getCorrectOption())) {
                    correct++;
                }
            }

            scoreLabel.setText("You scored: " + correct + " / " + total);
        });

        Button resetBtn = new Button("Reset Quiz");
        resetBtn.setOnAction(e -> {
            for (Map.Entry<Question, ToggleGroup> entry : toggleGroupsMap.entrySet()) {
                entry.getValue().getToggles().forEach(toggle -> ((RadioButton) toggle).setSelected(false));
                optionButtonsMap.get(entry.getKey()).forEach(btn -> {
                    btn.setDisable(false);
                    btn.setStyle("-fx-text-fill: black;");
                });
            }
            scoreLabel.setText("");
        });

        HBox buttonBox = new HBox(10, submitBtn, resetBtn);
        root.getChildren().addAll(buttonBox, scoreLabel);

        Scene scene = new Scene(root, 700, 750);
        stage.setTitle("Quiz: " + quizCode);
        stage.setScene(scene);
        stage.show();
    }

    public static void launchQuiz(String username, String quizCode) {
        staticUsername = username;
        staticQuizCode = quizCode;
        launch(); // Calls default constructor -> uses static fields
    }
}
