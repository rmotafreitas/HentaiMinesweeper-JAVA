package com.example.hentaiminesweeper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.hentaiminesweeper.structs.ApiImage;
import com.example.hentaiminesweeper.structs.GameDifficulty;
import com.example.hentaiminesweeper.structs.User;
import com.example.hentaiminesweeper.structs.ApiImage.ApiImageCollection;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Timer;

public class Window extends Application {

    public static int size, tilesize = 32, tiles = 16;
    public static int bombCount = 0;

    private static int windowMax = 30, windowMin = 14;

    public static boolean gamer = false;
    public static ApiImage image = null;

    @Override
    public void start(Stage stage) throws IOException {

        Window.size = tiles * tilesize;
        Window.bombCount = (Window.size / Window.tilesize) * 3;

        FXMLLoader fxmlLoader = new FXMLLoader(Window.class.getResource("main-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hentai MineSweeper");
        stage.setScene(scene);
        stage.show();
    }

    public static void changeScene(Scene scene, String page) throws IOException{

        FXMLLoader loader = new FXMLLoader(Window.class.getResource(page));
        Stage stage = (Stage) scene.getWindow();
        
        Scene s;
        if(page.equals("game.fxml")){
            
            s = new Scene(loader.load(), Window.size, Window.size + 40);
        }else s = new Scene(loader.load());

        stage.setScene(s);
    }

    public static void launchApp(){
        
        launch();
    }

    public static String getIMagemUwU() {

        try {

            URL url = new URL("https://nekos.moe/api/v1/random/image");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json");

            connection.connect();
            int code = connection.getResponseCode();

            if (code == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                connection.disconnect();
                String jsonResponse = response.toString();

                Gson g = new Gson();
                ApiImageCollection imageObject = g.fromJson(jsonResponse, ApiImageCollection.class);

                ApiImage image = imageObject.images[0];
                Window.image = image;

                return "https://nekos.moe/image/" + image.id;

            } else {
                System.out.println("Error: Response code: " + code);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @FXML
    public static void sendMessage(String header, String content, boolean error){

        Platform.runLater(() -> {
    
            Alert alert = new Alert((error ? AlertType.ERROR : AlertType.INFORMATION));
            alert.setTitle("SYSTEM");
            alert.setHeaderText(header);
            alert.setContentText(content);
    
            alert.showAndWait();
        });
    }

    public static String prompt(String prompt)
    {
        TextInputDialog td = new TextInputDialog("");
        td.setTitle("SYSTEM");
        td.setHeaderText(prompt);
        td.showAndWait();

        return td.getEditor().getText();
    }

    public static String promptRestricted(String prompt, int charCount, boolean strict)
    {
        TextInputDialog td = new TextInputDialog("");
        td.setTitle("SYSTEM");
        td.setHeaderText(prompt);

        td.showAndWait();
        String text = td.getEditor().getText();

        String alphanumREGEX = "[a-zA-Z0-9]+";
        if((strict && text.length() != charCount) || (!strict && text.length() > charCount) || !text.matches(alphanumREGEX)){
            return promptRestricted(prompt, charCount, strict);
        }

        return text.toUpperCase();
    }

    public static void updateDifficulty(GameDifficulty dif){

        Window.tiles = dif.size;
        Window.size = tiles * tilesize;

        Window.bombCount = dif.mines;
    }
}