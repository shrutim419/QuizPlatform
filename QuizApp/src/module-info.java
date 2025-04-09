module QuizApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens ui to javafx.fxml;
    opens model to javafx.base;
    opens dao to javafx.base,java.sql,javafx.fxml;
    
    exports ui;
    exports model;
    exports dao;
    
    
    // Allow JavaFX to access the Server package
    exports Server;
    opens Server to javafx.graphics, javafx.fxml;
}
