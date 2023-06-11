package com.example.hentaiminesweeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class HelloApplication extends Application {

    public static int size, tilesize = 32;

    public static boolean gamer = false;

    @Override
    public void start(Stage stage) throws IOException {

        HelloApplication.size = (new Random().nextInt((30 - 14) + 1) + 14) * tilesize;

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), size, size + 40);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
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

                Pattern pattern = Pattern.compile("\"id\":\"([^\"]+)\"");
                Matcher matcher = pattern.matcher(jsonResponse);

                if (matcher.find())
                    return "https://nekos.moe/image/" + matcher.group(1);

            } else {
                System.out.println("Error: Response code: " + code);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}