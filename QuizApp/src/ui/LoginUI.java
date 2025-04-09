package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DB.DBConnection;
import model.LoginSession; // ✅ Add this import

public class LoginUI extends Application {

    @Override
    public void start(Stage stage) {
        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Label roleLabel = new Label("Select Role:");
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("admin", "player");
        roleBox.setValue("player"); // Default

        Button loginBtn = new Button("Login");
        Button signupBtn = new Button("Sign Up");

        VBox vbox = new VBox(10, userLabel, usernameField, passLabel, passwordField, roleLabel, roleBox, loginBtn, signupBtn);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Scene scene = new Scene(vbox, 300, 300);

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String pass = passwordField.getText().trim();
            String selectedRole = roleBox.getValue();

            if (username.isEmpty() || pass.isEmpty() || selectedRole == null) {
                showAlert(Alert.AlertType.WARNING, "Please enter all fields.");
                return;
            }

            int result = validateUserAndSetSession(username, pass, selectedRole);
            if (result == 1) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful as Admin");
                stage.close();
                new AdminDashboardUI().start(new Stage());
            } else if (result == 2) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful as Player");
                PlayerDashboardUI.setUsername(username);
                stage.close();
                new PlayerDashboardUI().start(new Stage());
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid credentials or role mismatch!");
            }
        });

        signupBtn.setOnAction(e -> {
            try {
                new RegisterUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Failed to open registration form.");
            }
        });

        stage.setTitle("Quiz Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ✅ Validate with role match
    public static int validateUserAndSetSession(String username, String password, String selectedRole) {
        String sql = "SELECT user_id, role FROM users WHERE username = ? AND password = ? AND role = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, selectedRole);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String role = rs.getString("role");
                LoginSession.setLoggedInUserId(userId); // ✅ Save to session
                return role.equalsIgnoreCase("admin") ? 1 : 2;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ✅ Simple alert
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
