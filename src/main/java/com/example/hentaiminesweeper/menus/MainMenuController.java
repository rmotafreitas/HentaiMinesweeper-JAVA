package com.example.hentaiminesweeper.menus;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.example.hentaiminesweeper.DatabaseConnection;
import com.example.hentaiminesweeper.Main;
import com.example.hentaiminesweeper.Utils;
import com.example.hentaiminesweeper.Window;
import com.example.hentaiminesweeper.DatabaseConnection.SynchronousQueryCompletionListener;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class MainMenuController implements Initializable{

    @FXML
    private Pane profile;

    @FXML
    private Label profile_label;

    @FXML
    private Button login;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
        profile.setStyle("-fx-border-color: black");
        profile_label.setText("USER ACCOUNT OF: " + (Main.account == null ? "Guest" : Main.account.username));
    }

    @FXML
    protected void onLinkClick() {

    }

    @FXML
    protected void classicStart(){

        try {
            Window.changeScene(profile.getScene(), "hello-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void loginActivity(){

        String user = Window.prompt("Account username?");
        if(user == null) return;
        
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("SYSTEM");
        dialog.setHeaderText("Account password?");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        PasswordField pwd = new PasswordField();
        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_RIGHT);
        content.getChildren().addAll(pwd);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {

                return pwd.getText();
            }
            return null;
        });

        dialog.getDialogPane().setContent(content);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {

            Utils.login(user, result.get());
        }
    }

    @FXML
    protected void registerActivity(){

        //Register
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("SYSTEM");
        dialog.setHeaderText("Creating an account");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField usr = new TextField();

        PasswordField pwd = new PasswordField();
        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(10);
        
        // Add username box
        content.getChildren().addAll(new Label("Your username:"), usr);
        // Add password box
        content.getChildren().addAll(new Label("Your password:"), pwd);

        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {

                String username = usr.getText();
                String password = pwd.getText();

                // Data validation

                if(username.contains("\n") || password.contains("\n")) return null;
                if(username.trim().length() == 0 || password.trim().length() <= 2) return null;

                String usernameValidRegex = "^[A-Za-z][A-Za-z0-9_]{2,29}$";
                if(!username.matches(usernameValidRegex)) return null;

                return username + "\n" + password;
            }
            
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {

            String username = result.get().split("\n")[0];
            String password = result.get().split("\n")[1];

            DatabaseConnection.getUserAccountOfName(username, new SynchronousQueryCompletionListener() {

                @Override
                public void queryFinished(Object[] returnValue) {

                    if(returnValue == null){

                        DatabaseConnection.createUserAccount(username, password);
                        Window.sendMessage("Account created", "Welcome to the Hentai MineSweeper community " + username + "!", false);
                    }
                    
                    Window.sendMessage("Account creation failed", "The username " + username + " is already in use!", false);
                }
            });


        }else {

            Window.sendMessage("Format error", "It seems the data you inserted is not accepted by the system. \nKeep in mind the following account creation rules:\n* Username and password can't be empty\n* Password needs to be at least 3 characters long\n* Usernames can have from 3 to 30 characters, it can only contain alphanumeric characters and underscores, a username can't start with a number", true);
        }
    }
}