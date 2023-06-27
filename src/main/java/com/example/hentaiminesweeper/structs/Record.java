package com.example.hentaiminesweeper.structs;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Record {

    public String image;
    public String user;

    public long time;
    public int boardSize;

    public Record(){}

    public Record(String image, long time, int boardSize, User player) {

        this.time = time;
        this.boardSize = boardSize;
        this.image = image;
        this.user = player.id;
    }

    public long getTime() {return time;}
    public String getTimeFormated(){

        long SS = time;
        long MM = time / 60;

        return String.format("%02d:%02d", MM, SS);
    }
}
