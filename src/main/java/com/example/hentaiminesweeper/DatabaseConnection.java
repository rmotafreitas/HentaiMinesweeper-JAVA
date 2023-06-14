package com.example.hentaiminesweeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.hentaiminesweeper.structs.Record;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference.CompletionListener;

public class DatabaseConnection {

    private static FirebaseApp application = null;
    private static FirebaseDatabase database = null;

    public static void connectToFirebase() {

        try {

            File refreshToken = new File(System.getProperty("user.dir")).getParentFile();
            FileInputStream token = new FileInputStream(refreshToken.getAbsolutePath() + "/refreshToken.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(token))
                    .setDatabaseUrl("https://hentaims-5320c-default-rtdb.europe-west1.firebasedatabase.app/").build();

            application = FirebaseApp.initializeApp(options);
            database = FirebaseDatabase.getInstance(application);

        } catch (FileNotFoundException e) {

            HelloApplication.sendMessage("No credentials file found","Your credentials file is missing, you either don't have access to this database or have placed it in the wrong folder", true);
            e.printStackTrace();
        } catch (IOException e) {

            HelloApplication.sendMessage("Corrupted credentials","Your credentials file has a syntax error or is simply corrupted", true);
            e.printStackTrace();
        }
    }

    private static boolean isInitialized() {
        return !(application == null || database == null);
    }

    public static void addUserRecord(long time, int boardSize, String image) {

        if (!isInitialized())
            return;
        // Get user ip
        try {

            InetAddress address = InetAddress.getLocalHost();

            String ip = address.getHostAddress();
            String device = address.getHostName();

            String childID = UUID.randomUUID().toString();

            DatabaseReference usersRef = database.getReference("records").child(childID);
            usersRef.setValue(
                new Record(image, ip, device, time, boardSize), 
                null
            );

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
