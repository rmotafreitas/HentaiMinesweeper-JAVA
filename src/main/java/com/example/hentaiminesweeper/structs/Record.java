package com.example.hentaiminesweeper.structs;

public class Record {

    public String image;
    public String user;

    public long time;
    public int boardSize;

    public Record(String image, long time, int boardSize, User player) {

        this.time = time;
        this.boardSize = boardSize;
        this.image = image;
        this.user = player.id;
    }
}
