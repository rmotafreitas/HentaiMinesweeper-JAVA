package com.example.hentaiminesweeper.structs;

public class Record {

    public String image, name;

    public long time;
    public int boardSize;

    public Record(String image, long time, int boardSize, String name) {

        this.time = time;
        this.boardSize = boardSize;
        this.image = image;
        this.name = name;
    }
}
