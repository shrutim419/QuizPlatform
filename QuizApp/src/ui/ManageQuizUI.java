package ui;

import DB.DBConnection;
import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import model.Question;

public class ManageQuizUI extends Application {

    private final int quizId;
    private final String quizTitle;
    private TableView<Question> table;
    private ObservableList<Question> questions;

    public ManageQuizUI(int quizId, String quizTitle) {
        this.quizId = quizId;
        this.quizTitle = quizTitle;
    }

    @Override
    public void start(Stage stage) {
        questions = FXCollections.observableArrayList();
        table = new TableView<>();

        Label titleLabel = new Label("Managing Questions for: " + quizTitle);

        TableColumn<Question, String> qCol = new TableColumn<>("Question");
        qCol.setCellValueFactory(cell -> cell.getValue().questionTextProperty());

        TableColumn<Question, String> aCol = new TableColumn<>("A");
        aCol.setCellValueFactory(cell -> cell.getValue().optionAProperty());

        TableColumn<Question, String> bCol = new TableColumn<>("B");
        bCol.setCellValueFactory(cell -> cell.getValue().optionBProperty());

        TableColumn<Question, String> cCol = new TableColumn<>("C");
        cCol.setCellValueFactory(cell -> cell.getValue().optionCProperty());

        TableColumn<Question, String> dCol = new TableColumn<>("D");
        dCol.setCellValueFactory(cell -> cell.getValue().optionDProperty());

        TableColumn<Question, String> correctCol = new TableColumn<>("Correct");
        correctCol.setCellValueFactory(cell -> cell.getValue().correctOptionProperty());

        table.getColumns().addAll(qCol, aCol, bCol, cCol, dCol, correctCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load Questions
        loadQuestions();

        Button addBtn = new Button("Add New Question");
        Button deleteBtn = new Button("Delete Question");

        addBtn.setOnAction(e -> openAddQuestionDialog());
        deleteBtn.setOnAction(e -> deleteSelectedQuestion());

        VBox layout = new VBox(10, titleLabel, table, new HBox(10, addBtn, deleteBtn));
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout, 800, 500);
        stage.setTitle("Manage Questions");
        stage.setScene(scene);
        stage.show();
    }

    private void loadQuestions() {
        questions.clear();
        String sql = "SELECT * FROM questions WHERE quiz_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(new Question(
                        rs.getInt("question_id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option")
                ));
            }
            table.setItems(questions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openAddQuestionDialog() {
        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle("Add New Question");

        // Form fields
        TextField qField = new TextField();
        TextField aField = new TextField();
        TextField bField = new TextField();
        TextField cField = new TextField();
        TextField dField = new TextField();
        ComboBox<String> correctOptionBox = new ComboBox<>(FXCollections.observableArrayList("A", "B", "C", "D"));
        correctOptionBox.setValue("A");

        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(10);
        grid.addRow(0, new Label("Question:"), qField);
        grid.addRow(1, new Label("Option A:"), aField);
        grid.addRow(2, new Label("Option B:"), bField);
        grid.addRow(3, new Label("Option C:"), cField);
        grid.addRow(4, new Label("Option D:"), dField);
        grid.addRow(5, new Label("Correct Option:"), correctOptionBox);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new Question(0, qField.getText(), aField.getText(), bField.getText(), cField.getText(), dField.getText(), correctOptionBox.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(question -> {
            String sql = "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, quizId);
                stmt.setString(2, question.getQuestionText());
                stmt.setString(3, question.getOptionA());
                stmt.setString(4, question.getOptionB());
                stmt.setString(5, question.getOptionC());
                stmt.setString(6, question.getOptionD());
                stmt.setString(7, question.getCorrectOption());
                stmt.executeUpdate();
                loadQuestions();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void deleteSelectedQuestion() {
        Question selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this question?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(type -> {
            if (type == ButtonType.YES) {
                String sql = "DELETE FROM questions WHERE question_id = ?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, selected.getQuestionId());
                    stmt.executeUpdate();
                    loadQuestions();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
