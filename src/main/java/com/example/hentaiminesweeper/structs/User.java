package com.example.hentaiminesweeper.structs;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements Serializable{
    
    public String id = null;
    public String username = null;
    public String password = null;

    public long imagesFound = 0;
    public long bestTime = -1;

    public long onlineGames = 0;
    public long onlineWins = 0;

    public String joinedAt = null;
    public long globalRank = -1;
    public long localRank = -1;

    public User(String username, String password){

        this.username = username;
        this.password = password;
    }

    public User(){}

    public void writeMyData(){

        try {
            
            FileOutputStream fout = new FileOutputStream(System.getProperty("user.dir") + "/login.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
