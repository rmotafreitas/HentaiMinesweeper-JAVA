module com.example.hentaiminesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires com.google.gson;
    requires firebase.admin;
    requires com.google.auth.oauth2;

    opens com.example.hentaiminesweeper to javafx.fxml;
    opens com.example.hentaiminesweeper.structs;

    exports com.example.hentaiminesweeper;
}