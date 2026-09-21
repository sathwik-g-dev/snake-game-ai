package com.snakegame;

import com.snakegame.core.RunSnake;
import javax.swing.SwingUtilities;

/**
 * Main application launcher for the Snake Game.
 * Initializes and schedules the Swing Graphical User Interface onto the Event Dispatch Thread (EDT).
 */
public class Game {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new RunSnake());
    }
}
