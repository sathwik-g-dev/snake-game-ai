package com.snakegame.core;

/**
 * Difficulty levels for the Snake Game.
 *
 * Each level defines the game loop timer delay in milliseconds.
 * Lower delay = faster snake movement = higher difficulty.
 */
public enum Difficulty {
    EASY(48, "EASY"),
    MEDIUM(30, "MEDIUM"),
    HARD(18, "HARD");

    private final int delayMs;
    private final String label;

    Difficulty(int delayMs, String label) {
        this.delayMs = delayMs;
        this.label = label;
    }

    public int getDelayMs() {
        return delayMs;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Cycles to the next difficulty level: EASY -> MEDIUM -> HARD -> EASY
     */
    public Difficulty next() {
        switch (this) {
            case EASY:   return MEDIUM;
            case MEDIUM: return HARD;
            case HARD:   return EASY;
            default:     return MEDIUM;
        }
    }
}
