package model;

import javafx.beans.property.*;

public class Quiz {
    private final IntegerProperty quizId;
    private final StringProperty title;
    private final StringProperty code;

    public Quiz(int quizId, String title, String code) {
        this.quizId = new SimpleIntegerProperty(quizId);
        this.title = new SimpleStringProperty(title);   // ✅ Correct binding
        this.code = new SimpleStringProperty(code);     // ✅ Correct binding
    }

    public int getQuizId() {
        return quizId.get();
    }

    public IntegerProperty quizIdProperty() {
        return quizId;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }
}
