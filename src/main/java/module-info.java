module com.example.hentaiminesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    opens com.example.hentaiminesweeper to javafx.fxml;

    exports com.example.hentaiminesweeper;
}