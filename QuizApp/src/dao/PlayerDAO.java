package dao;

import DB.DBConnection;
import model.Player;

import java.sql.*;

public class PlayerDAO {

    // Create a new player record (linked to a user and a quiz)
    public boolean createPlayer(int userId, int quizId) {
        String sql = "INSERT INTO players (user_id, quiz_id, score) VALUES (?, ?, 0)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, quizId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Player creation failed: " + e.getMessage());
            return false;
        }
    }

    // Get a player's score record by userId and quizId
    public Player getPlayer(int userId, int quizId) {
        String sql = "SELECT * FROM players WHERE user_id = ? AND quiz_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, quizId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Player(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("quiz_id"), rs.getInt("score"), rs.getTimestamp("played_on"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update a player's score
    public void updateScore(int userId, int quizId, int newScore) {
        String sql = "UPDATE players SET score = ? WHERE user_id = ? AND quiz_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newScore);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, quizId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
