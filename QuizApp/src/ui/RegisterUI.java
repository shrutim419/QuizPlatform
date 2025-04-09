package ui;

import DB.DBConnection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterUI extends Application {

    @Override
    public void start(Stage stage) {
        Label usernameLabel = new Label("Choose a Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Choose a Password:");
        PasswordField passwordField = new PasswordField();

        Label roleLabel = new Label("Select Role:");
        ChoiceBox<String> roleChoiceBox = new ChoiceBox<>();
        roleChoiceBox.getItems().addAll("admin", "player");
        roleChoiceBox.setValue("player"); // default role

        Button registerBtn = new Button("Register");

        VBox vbox = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, roleLabel, roleChoiceBox, registerBtn);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        registerBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleChoiceBox.getValue();

            if (username.isEmpty() || password.isEmpty() || role == null) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields.").show();
                return;
            }

            if (registerUser(username, password, role)) {
                new Alert(Alert.AlertType.INFORMATION, "Registration Successful! Please login.").show();
                stage.close(); // Optional: Open login form here
            } else {
                new Alert(Alert.AlertType.ERROR, "Username already taken or database error!").show();
            }
        });

        Scene scene = new Scene(vbox, 300, 300);
        stage.setTitle("User Registration");
        stage.setScene(scene);
        stage.show();
    }

    public boolean registerUser(String username, String password, String role) {
        String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);  // Consider hashing passwords in real projects
            stmt.setString(3, role);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();  // likely a duplicate username or DB issue
        }

        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
