package com.example.hentaiminesweeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.example.hentaiminesweeper.structs.Record;
import com.example.hentaiminesweeper.structs.User;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

            Window.sendMessage("No credentials file found","Your credentials file is missing, you either don't have access to this database or have placed it in the wrong folder", true);
            e.printStackTrace();
        } catch (IOException e) {

            Window.sendMessage("Corrupted credentials","Your credentials file has a syntax error or is simply corrupted", true);
            e.printStackTrace();
        }
    }

    private static boolean isInitialized() {
        return !(application == null || database == null);
    }

    public static void addUserRecord(long time, int boardSize, String image) {

        if (!isInitialized()) return;
        if(Main.account == null) {

            Window.sendMessage("Not logged in", "You are not logged in so you can't submit scores!", true);
            return;
        }

        try {

            String childID = UUID.randomUUID().toString();

            DatabaseReference recordsRef = database.getReference("records").child(childID);
            recordsRef.setValue(
                new Record(image, time, boardSize, Main.account), 
                null
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createUserAccount(String username, String password){

        if (!isInitialized()) return;

        String encryptedPassword = Utils.encryptPasswordValue(password);
        User user = new User(username, encryptedPassword);

        user.joinedAt = new Date().toLocaleString();
        user.id = UUID.randomUUID().toString();

        user.coins = 500;

        try {
            
            DatabaseReference userRef = database.getReference("users").child(user.id);
            userRef.setValue(
                user,
                null
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getUserAccountOfName(String username, SynchronousQueryCompletionListener listener){

        if (!isInitialized()) return;

        DatabaseReference usersRef = database.getReference("users/");
        Query query = usersRef.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot issue : snapshot.getChildren()) {
                    
                    listener.queryFinished(new User[]{issue.getValue(User.class)});
                    return;
                }

                listener.queryFinished(null);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
            
        });
    }

    public interface SynchronousQueryCompletionListener{

        public void queryFinished(Object[] returnValue);
    }

    public static void getUserTimes(String id, SynchronousQueryCompletionListener listener){

        if (!isInitialized()) return;

        DatabaseReference usersRef = database.getReference("records/");
        Query query = usersRef.orderByChild("user").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List<Record> l = StreamSupport.stream(snapshot.getChildren().spliterator(), false)
                    .map(i -> i.getValue(Record.class))
                    .collect(Collectors.toList());

                Collections.sort(l, Comparator.comparingLong(Record::getTime));
                listener.queryFinished(l.toArray());
            }

            @Override
            public void onCancelled(DatabaseError error) {}
            
        });
    }

    public static void giveCoinsToUser(long amount){

        User userCopy = Main.account;
        userCopy.coins += amount;

        DatabaseReference myRef = database.getReference("users").child(Main.account.id);
        myRef.setValue(userCopy, new CompletionListener() {

            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                
                if(error == null){

                    //Overwrite current data
                    Main.account = userCopy;
                }
            }
            
        });
    }
}
