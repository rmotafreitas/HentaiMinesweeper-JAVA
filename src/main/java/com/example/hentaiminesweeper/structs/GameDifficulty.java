package com.example.hentaiminesweeper.structs;

public enum GameDifficulty {
    
    EASY(8, 10),
    NORMAL(16, 40),
    HARD(30, 99),
    LUNATIC(50, 1000),
    CUSTOM(5,2);

    public int size = 5;
    public int mines = 2;

    private GameDifficulty(int size, int mines){

        this.size = size;
        this.mines = mines;
    }

    public static GameDifficulty convert(String id){

        switch(id){
            case "easy": return EASY;
            case "normal": return NORMAL;
            case "hard": return HARD;
            case "lunatic": return LUNATIC;
            default: return EASY;
        }
    }
}
