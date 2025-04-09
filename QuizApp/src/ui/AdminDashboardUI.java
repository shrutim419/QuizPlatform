package ui;

import DB.DBConnection;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Quiz;
import model.LoginSession;

import java.sql.*;

public class AdminDashboardUI extends Application {

    private TableView<Quiz> quizTable;
    private ObservableList<Quiz> quizzes;

    @Override
    public void start(Stage stage) {
        quizzes = FXCollections.observableArrayList();
        quizTable = new TableView<>();

        // Corrected Column Definitions
        TableColumn<Quiz, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cell -> cell.getValue().titleProperty()); // ✅ Correct

        TableColumn<Quiz, String> codeCol = new TableColumn<>("Code");
        
        codeCol.setCellValueFactory(cell -> cell.getValue().codeProperty());   // ✅ Correct

        TableColumn<Quiz, Integer> idCol = new TableColumn<>("Quiz ID");
        idCol.setCellValueFactory(cell -> cell.getValue().quizIdProperty().asObject());

        quizTable.getColumns().addAll(idCol, titleCol, codeCol);  // Correct order
        quizTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Buttons
        Button createBtn = new Button("Create New Quiz");
        Button renameBtn = new Button("Rename Quiz");
        Button deleteBtn = new Button("Delete Quiz");
        Button manageQBtn = new Button("Manage Questions");

        HBox btnBox = new HBox(10, createBtn, renameBtn, deleteBtn, manageQBtn);
        btnBox.setPadding(new Insets(10));

        VBox layout = new VBox(10, quizTable, btnBox);
        layout.setPadding(new Insets(15));

        // Load quizzes
        loadQuizzes();

        // Button Actions
        createBtn.setOnAction(e -> createQuizDialog());
        renameBtn.setOnAction(e -> renameQuizDialog());
        deleteBtn.setOnAction(e -> deleteQuiz());
        manageQBtn.setOnAction(e -> manageQuestions());

        Scene scene = new Scene(layout, 600, 400);
        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private void loadQuizzes() {
        quizzes.clear();
        int adminId = LoginSession.getLoggedInUserId();

        String sql = "SELECT * FROM quizzes WHERE created_by = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("quiz_id"),
                        rs.getString("title"),
                        rs.getString("code")
                ));
            }
            quizTable.setItems(quizzes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createQuizDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Quiz");
        dialog.setHeaderText("Enter quiz title:");
        dialog.setContentText("Title:");

        dialog.showAndWait().ifPresent(title -> {
            String code = generateQuizCode();
            int adminId = LoginSession.getLoggedInUserId();

            String sql = "INSERT INTO quizzes (title, code, created_by) VALUES (?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, title);
                stmt.setString(2, code);
                stmt.setInt(3, adminId);
                stmt.executeUpdate();
                loadQuizzes();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private String generateQuizCode() {
        return "QZ" + (int)(Math.random() * 10000);
    }

    private void renameQuizDialog() {
        Quiz selected = quizTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected.getTitle());
        dialog.setTitle("Rename Quiz");
        dialog.setHeaderText("Enter new title for quiz:");
        dialog.setContentText("Title:");

        dialog.showAndWait().ifPresent(newTitle -> {
            String sql = "UPDATE quizzes SET title = ? WHERE quiz_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newTitle);
                stmt.setInt(2, selected.getQuizId());
                stmt.executeUpdate();
                loadQuizzes();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void deleteQuiz() {
        Quiz selected = quizTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete quiz and all questions?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(type -> {
            if (type == ButtonType.YES) {
                String sql = "DELETE FROM quizzes WHERE quiz_id = ?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, selected.getQuizId());
                    stmt.executeUpdate();
                    loadQuizzes();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void manageQuestions() {
        Quiz selected = quizTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        new ManageQuizUI(selected.getQuizId(), selected.getTitle()).start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
