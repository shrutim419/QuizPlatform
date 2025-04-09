package dao;

import DB.DBConnection;

import java.sql.*;
import java.util.List;

public class QuizDAO {

    public static int saveQuiz(String title) {
        String insertQuiz = "INSERT INTO Quiz (title) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuiz, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, title);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // return generated quiz ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean saveQuestion(int quizId, String question, List<String> options, int correctIndex) {
        String sql = "INSERT INTO Questions (quiz_id, question, option1, option2, option3, option4, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quizId);
            stmt.setString(2, question);
            stmt.setString(3, options.get(0));
            stmt.setString(4, options.get(1));
            stmt.setString(5, options.get(2));
            stmt.setString(6, options.get(3));
            stmt.setInt(7, correctIndex + 1); // +1 to make it 1-based (like option1)

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static String getQuizTitleByCode(String code) {
        String sql = "SELECT title FROM quizzes WHERE code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("title");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Quiz";
    }
}
