package com.example.hentaiminesweeper.structs;

public class ApiImage {
    
    public boolean nsfw;
    public String[] tags;
    public int likes;
    public int favorites;
    public String id;

    public String artist;
    public String createdAt;

    public class ApiImageCollection {
    
        public ApiImage[] images;
    }
}
