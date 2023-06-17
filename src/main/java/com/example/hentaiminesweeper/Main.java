package com.example.hentaiminesweeper;

import com.example.hentaiminesweeper.structs.User;

public class Main {

    public static User account = null;

    public static void main(String[] args) {

        //Initialize application
        DatabaseConnection.connectToFirebase();
        Utils.getEncryptionKey();
        Utils.getUserAccount();

        Window.launchApp();
    }
}
