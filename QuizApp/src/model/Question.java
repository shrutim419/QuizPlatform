package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.List;

public class Question {
    private int questionId;
    private StringProperty questionText, optionA, optionB, optionC, optionD, correctOption;
    private int id;
    private int quizId;
    private String content;
    private List<Answer> answers; // For DAO use

    // Full Constructor (all fields)
    public Question(int questionId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption, int id, int quizId, String content) {
        this.questionId = questionId;
        this.questionText = new SimpleStringProperty(questionText);
        this.optionA = new SimpleStringProperty(optionA);
        this.optionB = new SimpleStringProperty(optionB);
        this.optionC = new SimpleStringProperty(optionC);
        this.optionD = new SimpleStringProperty(optionD);
        this.correctOption = new SimpleStringProperty(correctOption);
        this.id = id;
        this.quizId = quizId;
        this.content = content;
    }

    // Constructor used in DAO (simplified)
    public Question(int questionId, int quizId, String questionText) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionText = new SimpleStringProperty(questionText);
    }

    // Constructor for UI with options and correct answer
    public Question(int questionId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption) {
        this.questionId = questionId;
        this.questionText = new SimpleStringProperty(questionText);
        this.optionA = new SimpleStringProperty(optionA);
        this.optionB = new SimpleStringProperty(optionB);
        this.optionC = new SimpleStringProperty(optionC);
        this.optionD = new SimpleStringProperty(optionD);
        this.correctOption = new SimpleStringProperty(correctOption);
    }

    // Getters
    public int getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText.get();
    }

    public String getOptionA() {
        return optionA != null ? optionA.get() : null;
    }

    public String getOptionB() {
        return optionB != null ? optionB.get() : null;
    }

    public String getOptionC() {
        return optionC != null ? optionC.get() : null;
    }

    public String getOptionD() {
        return optionD != null ? optionD.get() : null;
    }

    public String getCorrectOption() {
        return correctOption != null ? correctOption.get() : null;
    }

    public int getId() {
        return id;
    }

    public int getQuizId() {
        return quizId;
    }

    public String getContent() {
        return content;
    }

    // JavaFX Property Getters
    public StringProperty questionTextProperty() {
        return questionText;
    }

    public StringProperty optionAProperty() {
        return optionA;
    }

    public StringProperty optionBProperty() {
        return optionB;
    }

    public StringProperty optionCProperty() {
        return optionC;
    }

    public StringProperty optionDProperty() {
        return optionD;
    }

    public StringProperty correctOptionProperty() {
        return correctOption;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // For DAO: Set and Get Answers
    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Answer> getAnswers() {
        return answers;
    }
}
