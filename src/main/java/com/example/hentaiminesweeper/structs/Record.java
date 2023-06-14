package com.example.hentaiminesweeper.structs;

public class Record {

    public String image;
    public String ip, device;

    public long time;
    public int boardSize;

    public Record(String image, String ip, String device, long time, int boardSize) {

        this.time = time;
        this.boardSize = boardSize;
        this.device = device;
        this.ip = ip;
        this.image = image;
    }
}
