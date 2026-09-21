package com.snakegame.core;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * RunSnake sets up the Swing window, layout panels, score displays,
 * and user control buttons (Instructions, Reset, and AI Bot toggle).
 */
public class RunSnake implements Runnable {

    private int bestScore = 0;

    @Override
    public void run() {
        // Read best score from local storage
        File bestScoreFile = new File("files/bestScore.txt");
        if (bestScoreFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(bestScoreFile))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    bestScore = Integer.parseInt(line.trim());
                }
            } catch (Exception ignored) {
                bestScore = 0;
            }
        }

        final JFrame frame = new JFrame("Snake Game - Intelligent AI Autopilot");
        frame.setLocation(250, 150);
        frame.setMinimumSize(new Dimension(680, 520));

        // Top Status HUD Panel
        final JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statusPanel.setBackground(new Color(15, 23, 42)); // Dark slate header

        final JLabel status = new JLabel("SCORE: 0 / BEST: " + bestScore + "  |  DIFFICULTY: MEDIUM");
        status.setFont(new Font("SansSerif", Font.BOLD, 13));
        status.setForeground(new Color(241, 245, 249));
        statusPanel.add(status);
        frame.add(statusPanel, BorderLayout.NORTH);

        // Center Game Court Canvas
        final SnakeGameBoard board = new SnakeGameBoard(status);
        frame.add(board, BorderLayout.CENTER);

        // Bottom Controls Toolbar
        final JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        controlsPanel.setBackground(new Color(15, 23, 42)); // Matching dark footer

        final JButton instructionsBtn = createStyledButton("INSTRUCTIONS", new Color(71, 85, 105));
        instructionsBtn.addActionListener(e -> {
            board.instructions();
            board.requestFocusInWindow();
        });

        final JButton resetBtn = createStyledButton("RESET", new Color(225, 29, 72));
        resetBtn.addActionListener(e -> {
            board.reset();
            board.requestFocusInWindow();
        });

        final JButton pauseBtn = createStyledButton("PAUSE", new Color(217, 119, 6));
        pauseBtn.addActionListener(e -> {
            board.togglePause();
            pauseBtn.setText(board.isPaused() ? "RESUME" : "PAUSE");
            pauseBtn.setBackground(board.isPaused() ? new Color(234, 88, 12) : new Color(217, 119, 6));
            board.requestFocusInWindow();
        });

        final JButton diffBtn = createStyledButton("DIFFICULTY: MEDIUM", new Color(79, 70, 229));
        diffBtn.addActionListener(e -> {
            board.cycleDifficulty();
            diffBtn.setText("DIFFICULTY: " + board.getDifficulty().getLabel());
            board.requestFocusInWindow();
        });

        final JButton aiToggleBtn = createStyledButton("AI BOT: OFF", new Color(15, 118, 110));
        aiToggleBtn.addActionListener(e -> {
            board.toggleAiMode();
            boolean active = board.isAiMode();
            aiToggleBtn.setText(active ? "AI BOT: ON" : "AI BOT: OFF");
            aiToggleBtn.setBackground(active ? new Color(16, 185, 129) : new Color(15, 118, 110));
            board.requestFocusInWindow();
        });

        controlsPanel.add(instructionsBtn);
        controlsPanel.add(resetBtn);
        controlsPanel.add(pauseBtn);
        controlsPanel.add(diffBtn);
        controlsPanel.add(aiToggleBtn);

        frame.add(controlsPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        board.reset();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
