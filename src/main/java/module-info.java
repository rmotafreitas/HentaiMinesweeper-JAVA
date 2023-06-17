module com.example.hentaiminesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires com.google.gson;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires jasypt;

    opens com.example.hentaiminesweeper to javafx.fxml;
    opens com.example.hentaiminesweeper.structs;
    opens com.example.hentaiminesweeper.menus;

    exports com.example.hentaiminesweeper;
}