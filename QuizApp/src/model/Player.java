package model;

import java.sql.Timestamp;

public class Player {
    private int id;
    private int userId;
    private int quizId;
    private int score;
    private Timestamp playedOn;

    public Player() {
        // default constructor
    }

    public Player(int id, int userId, int quizId, int score, Timestamp playedOn) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.playedOn = playedOn;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getQuizId() {
        return quizId;
    }

    public int getScore() {
        return score;
    }

    public Timestamp getPlayedOn() {
        return playedOn;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setPlayedOn(Timestamp playedOn) {
        this.playedOn = playedOn;
    }
}
