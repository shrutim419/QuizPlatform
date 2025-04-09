package dao;

import model.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public static List<Question> getQuestionsByQuizCode(String quizCode) {
        List<Question> questions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizee", "root", "P@ssw0rd");
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT q.* FROM questions q JOIN quizzes z ON q.quiz_id = z.quiz_id WHERE z.code = ?")) {
            ps.setString(1, quizCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	Question question = new Question(
            		    rs.getInt("question_id"),
            		    rs.getString("question_text"),
            		    rs.getString("option_a"),
            		    rs.getString("option_b"),
            		    rs.getString("option_c"),
            		    rs.getString("option_d"),
            		    rs.getString("correct_option")
            		);

                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
}
