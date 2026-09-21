package com.snakegame.core;

import com.snakegame.ai.SnakeAI;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * SnakeGameBoard manages the primary rendering surface, game loop timing,
 * user keyboard events, collision detection, and score persistence.
 *
 * It seamlessly supports both manual player controls and the intelligent
 * AI Autopilot pathfinding agent.
 */
public class SnakeGameBoard extends JPanel {

    public static final int BOARD_WIDTH = 600;
    public static final int BOARD_HEIGHT = 400;

    private Snake snake;
    private Apple apple;
    private GoldenApple goldenApple;
    private final JLabel status;

    private int score = 0;
    private int bestScore = 0;

    private Difficulty difficulty = Difficulty.MEDIUM;
    private boolean paused = false;
    private boolean instructionsClicked;
    private boolean playing = false;
    private boolean aiMode = false;

    private static BufferedImage gameOverImg;
    private static BufferedImage instructionsImg;

    private final Timer timer;

    /**
     * Initializes the game court, assets, key listeners, and timer.
     *
     * @param statusInit Status label component in the parent window
     */
    public SnakeGameBoard(JLabel statusInit) {
        this.status = statusInit;

        setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));
        setBackground(new Color(169, 209, 167)); // Natural grass meadow tone

        loadAssets();
        loadBestScore();

        ActionListener gameTick = e -> begin();
        timer = new Timer(difficulty.getDelayMs(), gameTick);
        timer.start();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    private void loadAssets() {
        try {
            File goFile = new File("files/gameover.jpg");
            if (goFile.exists()) {
                gameOverImg = ImageIO.read(goFile);
            }
            File instFile = new File("files/instructions.jpg");
            if (instFile.exists()) {
                instructionsImg = ImageIO.read(instFile);
            }
        } catch (IOException e) {
            // Graceful fallback to vector rendering if images are missing
        }
    }

    private void loadBestScore() {
        File bestScoreFile = new File("files/bestScore.txt");
        if (bestScoreFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(bestScoreFile))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    bestScore = Integer.parseInt(line.trim());
                }
            } catch (Exception e) {
                bestScore = 0;
            }
        }
    }

    private void handleKeyPress(KeyEvent e) {
        if (instructionsClicked) {
            instructionsClicked = false;
            playing = true;
            repaint();
        }
        if (!playing) {
            // Snake is dead; reset is required to start a new round
            return;
        }

        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            togglePause();
            return;
        }

        if (paused) {
            return;
        }

        if (!timer.isRunning()) {
            timer.start();
        }

        boolean hasBody = snake != null && snake.getGameObjects().size() > 1;

        if (key == KeyEvent.VK_LEFT) {
            if (!hasBody || snake.getVx() <= 0) {
                snake.setVy(0);
                snake.setVx(-snake.getSnakeVX());
            }
        } else if (key == KeyEvent.VK_RIGHT) {
            if (!hasBody || snake.getVx() >= 0) {
                snake.setVy(0);
                snake.setVx(snake.getSnakeVX());
            }
        } else if (key == KeyEvent.VK_DOWN) {
            if (!hasBody || snake.getVy() >= 0) {
                snake.setVx(0);
                snake.setVy(snake.getSnakeVY());
            }
        } else if (key == KeyEvent.VK_UP) {
            if (!hasBody || snake.getVy() <= 0) {
                snake.setVx(0);
                snake.setVy(-snake.getSnakeVY());
            }
        }
    }

    public int getBoardWidth() {
        return getWidth() > 0 ? getWidth() : BOARD_WIDTH;
    }

    public int getBoardHeight() {
        return getHeight() > 0 ? getHeight() : BOARD_HEIGHT;
    }

    /**
     * Resets the court to an active new game state.
     */
    public void reset() {
        paused = false;
        if (!timer.isRunning()) {
            timer.start();
        }
        timer.setDelay(difficulty.getDelayMs());

        int bw = getBoardWidth();
        int bh = getBoardHeight();

        snake = new Snake(bw, bh);
        apple = new Apple(bw, bh);
        goldenApple = new GoldenApple(bw, bh);

        snake.setSnakeVX(3);
        snake.setSnakeVY(3);
        score = 0;
        updateScoreStatus();

        instructionsClicked = false;
        playing = true;
        repaint();
        requestFocusInWindow();
    }

    /**
     * Core game loop invoked on each timer tick.
     */
    public void begin() {
        if (playing && !paused) {
            int bw = getBoardWidth();
            int bh = getBoardHeight();

            if (snake != null) {
                snake.updateBounds(bw, bh);
            }
            if (apple != null) {
                apple.updateBounds(bw, bh);
            }
            if (goldenApple != null) {
                goldenApple.updateBounds(bw, bh);
            }

            // AI Autopilot Decision Hook
            if (aiMode && snake != null) {
                Direction aiDecision = SnakeAI.calculateNextMove(snake, apple, goldenApple, bw, bh);
                if (aiDecision != null) {
                    SnakeAI.applyDirection(snake, aiDecision);
                }
            }

            snake.move();

            // Collision with standard Apple (+10 points)
            if (apple != null && apple.intersects(snake)) {
                apple.powerUp(snake);
                score += 10;
                updateScoreStatus();

                if (apple.getGameObjects().size() < 4) {
                    apple.add();
                }
                if (Math.random() <= 0.2 && goldenApple.getGameObjects().size() < 2) {
                    goldenApple.add();
                }
            }

            // Collision with Golden Apple (+30 points)
            if (goldenApple != null && goldenApple.intersects(snake)) {
                goldenApple.powerUp(snake);
                score += 30;
                updateScoreStatus();

                if (apple.getGameObjects().size() < 4) {
                    apple.add();
                }
                if (Math.random() <= 0.2 && goldenApple.getGameObjects().size() < 2) {
                    goldenApple.add();
                }
            }

            // High Score Check
            if (score > bestScore) {
                bestScore = score;
                updateScoreStatus();
                saveBestScore();
            }

            // Terminal Collision Check (self or wall)
            if (snake != null && (snake.hasHitItself() || snake.hasHitWall())) {
                playing = false;
            }

            repaint();
        }
    }

    private void updateScoreStatus() {
        if (status != null) {
            String modeBadge = aiMode ? " [BOT AUTO-PLAY]" : "";
            String pauseBadge = paused ? " [PAUSED]" : "";
            status.setText("SCORE: " + score + " / BEST: " + bestScore + "  |  DIFFICULTY: " + difficulty.getLabel() + modeBadge + pauseBadge);
        }
    }

    private void saveBestScore() {
        new File("files").mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("files/bestScore.txt"))) {
            writer.write(Integer.toString(bestScore));
        } catch (IOException ignored) {
        }
    }

    public void instructions() {
        instructionsClicked = !instructionsClicked;
        playing = !instructionsClicked;
        repaint();
        requestFocusInWindow();
    }

    public boolean isAiMode() {
        return aiMode;
    }

    public void setAiMode(boolean aiMode) {
        this.aiMode = aiMode;
        updateScoreStatus();
        repaint();
    }

    public void toggleAiMode() {
        setAiMode(!this.aiMode);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) return;
        this.difficulty = difficulty;
        this.timer.setDelay(difficulty.getDelayMs());
        updateScoreStatus();
        repaint();
    }

    public void cycleDifficulty() {
        setDifficulty(this.difficulty.next());
    }

    public boolean isPaused() {
        return paused;
    }

    public void togglePause() {
        if (!playing) return;
        paused = !paused;
        if (paused) {
            timer.stop();
        } else {
            timer.start();
        }
        updateScoreStatus();
        repaint();
        requestFocusInWindow();
    }

    public boolean save() {
        if (!playing) {
            return false;
        }
        new File("files").mkdirs();
        try (BufferedWriter stateWriter = new BufferedWriter(new FileWriter("files/gameState.txt", false))) {
            stateWriter.write("snake," + snake.getPx() + "," + snake.getPy());
            stateWriter.newLine();
            stateWriter.write("apple," + apple.getPx() + "," + apple.getPy());
            stateWriter.newLine();
            stateWriter.write("golden," + goldenApple.getPx() + "," + goldenApple.getPy());
            stateWriter.newLine();
            stateWriter.write(snake.getSnakeVX() + "," + snake.getSnakeVY());
            stateWriter.newLine();
            stateWriter.write(Integer.toString(score));
        } catch (IOException e) {
            return false;
        }

        try (BufferedWriter snakeWriter = new BufferedWriter(new FileWriter("files/snakeObjs.txt", false))) {
            for (Point p : snake.getGameObjects()) {
                snakeWriter.write(p.x + "," + p.y);
                snakeWriter.newLine();
            }
        } catch (IOException e) {
            return false;
        }

        try (BufferedWriter appleWriter = new BufferedWriter(new FileWriter("files/appleObjs.txt", false))) {
            for (Point p : apple.getGameObjects()) {
                appleWriter.write(p.x + "," + p.y);
                appleWriter.newLine();
            }
        } catch (IOException e) {
            return false;
        }

        try (BufferedWriter goldenWriter = new BufferedWriter(new FileWriter("files/goldenAppleObjs.txt", false))) {
            for (Point p : goldenApple.getGameObjects()) {
                goldenWriter.write(p.x + "," + p.y);
                goldenWriter.newLine();
            }
        } catch (IOException e) {
            return false;
        }

        instructionsClicked = false;
        timer.stop();
        playing = true;
        repaint();
        requestFocusInWindow();
        return true;
    }

    public void reload() {
        File stateFile = new File("files/gameState.txt");
        File snakeFile = new File("files/snakeObjs.txt");
        File appleFile = new File("files/appleObjs.txt");
        File goldenFile = new File("files/goldenAppleObjs.txt");

        if (!stateFile.exists() || stateFile.length() == 0 || !snakeFile.exists() || snakeFile.length() == 0) {
            reset();
            return;
        }

        int snakePx = 20, snakePy = 20;
        int applePx = 100, applePy = 100;
        int goldenPx = 200, goldenPy = 200;
        int snakeVX = 3, snakeVY = 3;
        int localScore = 0;

        try (BufferedReader stateReader = new BufferedReader(new FileReader(stateFile))) {
            String snakePosLine = stateReader.readLine();
            if (snakePosLine != null && !snakePosLine.trim().isEmpty()) {
                String[] parts = snakePosLine.trim().split(",");
                if (parts.length >= 3) {
                    snakePx = Integer.parseInt(parts[1]);
                    snakePy = Integer.parseInt(parts[2]);
                }
            }

            String applePosLine = stateReader.readLine();
            if (applePosLine != null && !applePosLine.trim().isEmpty()) {
                String[] parts = applePosLine.trim().split(",");
                if (parts.length >= 3) {
                    applePx = Integer.parseInt(parts[1]);
                    applePy = Integer.parseInt(parts[2]);
                }
            }

            String goldenPosLine = stateReader.readLine();
            if (goldenPosLine != null && !goldenPosLine.trim().isEmpty()) {
                String[] parts = goldenPosLine.trim().split(",");
                if (parts.length >= 3) {
                    goldenPx = Integer.parseInt(parts[1]);
                    goldenPy = Integer.parseInt(parts[2]);
                }
            }

            String velocityLine = stateReader.readLine();
            if (velocityLine != null && !velocityLine.trim().isEmpty()) {
                String[] parts = velocityLine.trim().split(",");
                if (parts.length >= 2) {
                    snakeVX = Integer.parseInt(parts[0]);
                    snakeVY = Integer.parseInt(parts[1]);
                }
            }

            String scoreLine = stateReader.readLine();
            if (scoreLine != null && !scoreLine.trim().isEmpty()) {
                localScore = Integer.parseInt(scoreLine.trim());
            }
        } catch (Exception e) {
            reset();
            return;
        }

        LinkedList<Point> snakeObjs = new LinkedList<>();
        try {
            FileLineIterator snakeIterator = new FileLineIterator(snakeFile.getPath());
            while (snakeIterator.hasNext()) {
                String line = snakeIterator.next().trim();
                if (!line.isEmpty()) {
                    String[] coords = line.split(",");
                    if (coords.length >= 2) {
                        snakeObjs.add(new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
                    }
                }
            }
        } catch (Exception e) {
            reset();
            return;
        }

        if (snakeObjs.isEmpty()) {
            reset();
            return;
        }

        LinkedList<Point> appleObjs = new LinkedList<>();
        if (appleFile.exists() && appleFile.length() > 0) {
            try {
                FileLineIterator appleIterator = new FileLineIterator(appleFile.getPath());
                while (appleIterator.hasNext()) {
                    String line = appleIterator.next().trim();
                    if (!line.isEmpty()) {
                        String[] coords = line.split(",");
                        if (coords.length >= 2) {
                            appleObjs.add(new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        LinkedList<Point> goldenAppleObjs = new LinkedList<>();
        if (goldenFile.exists() && goldenFile.length() > 0) {
            try {
                FileLineIterator goldenIterator = new FileLineIterator(goldenFile.getPath());
                while (goldenIterator.hasNext()) {
                    String line = goldenIterator.next().trim();
                    if (!line.isEmpty()) {
                        String[] coords = line.split(",");
                        if (coords.length >= 2) {
                            goldenAppleObjs.add(new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        int bw = getBoardWidth();
        int bh = getBoardHeight();
        snake = new Snake(snakePx, snakePy, bw, bh, snakeObjs);
        apple = new Apple(applePx, applePy, bw, bh, appleObjs);
        goldenApple = new GoldenApple(goldenPx, goldenPy, bw, bh, goldenAppleObjs);

        snake.setSnakeVX(snakeVX);
        snake.setSnakeVY(snakeVY);
        score = localScore;
        updateScoreStatus();

        instructionsClicked = false;
        timer.stop();
        playing = true;
        repaint();
        requestFocusInWindow();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int bw = getBoardWidth();
        int bh = getBoardHeight();
        int imgW = Math.min(500, bw - 40);
        int imgH = Math.min(220, bh - 40);
        int imgX = Math.max(0, (bw - imgW) / 2);
        int imgY = Math.max(0, (bh - imgH) / 2);

        if (instructionsClicked) {
            if (instructionsImg != null) {
                g.drawImage(instructionsImg, imgX, imgY, imgW, imgH, null);
            } else {
                g.setColor(new Color(30, 41, 59, 235));
                g.fillRoundRect(imgX, Math.max(0, imgY - 10), imgW, imgH + 20, 16, 16);
                g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 18));
                g.drawString("HOW TO PLAY", imgX + 24, imgY + 24);

                g.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g.drawString("• Arrow Keys: Steer the snake. SPACE: Pause/Resume.", imgX + 24, imgY + 54);
                g.drawString("• Red Apples (+10 pts): Extends snake length by 3 segments.", imgX + 24, imgY + 78);
                g.drawString("• Golden Apples (+30 pts): Grants bonus score & speed boost!", imgX + 24, imgY + 102);
                g.drawString("• Difficulty: Toggle between EASY, MEDIUM, and HARD.", imgX + 24, imgY + 126);
                g.drawString("• AI Bot Mode: Autonomous pathfinding agent auto-plays!", imgX + 24, imgY + 150);

                g.setColor(new Color(110, 231, 183));
                g.drawString("Click INSTRUCTIONS or press any key to resume.", imgX + 24, imgY + 185);
            }
        } else if (paused) {
            g.setColor(new Color(15, 23, 42, 220));
            g.fillRoundRect(imgX, imgY, imgW, imgH, 16, 16);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 28));
            g.drawString("GAME PAUSED", imgX + Math.max(10, (imgW - 200) / 2), imgY + 80);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.drawString("Press SPACE or click PAUSE to resume", imgX + Math.max(10, (imgW - 260) / 2), imgY + 120);
        } else if (!playing) {
            if (gameOverImg != null) {
                g.drawImage(gameOverImg, imgX, imgY, imgW, imgH, null);
            } else {
                g.setColor(new Color(185, 28, 28, 230));
                g.fillRoundRect(imgX, imgY, imgW, imgH, 16, 16);
                g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 30));
                g.drawString("GAME OVER", imgX + Math.max(10, (imgW - 190) / 2), imgY + 85);
                g.setFont(new Font("SansSerif", Font.PLAIN, 15));
                g.drawString("Click RESET to play again", imgX + Math.max(10, (imgW - 180) / 2), imgY + 125);
            }
        } else {
            if (apple != null) {
                apple.draw(g);
            }
            if (goldenApple != null) {
                goldenApple.draw(g);
            }
            if (snake != null) {
                snake.draw(g);
            }

            // Sleek HUD badge when AI Autopilot is active
            if (aiMode) {
                int badgeW = 160;
                int badgeH = 26;
                int badgeX = bw - badgeW - 12;
                int badgeY = 12;

                g.setColor(new Color(16, 185, 129, 210)); // Emerald badge
                g.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 12, 12);
                g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 11));
                g.drawString("AI AUTOPILOT ON", badgeX + 22, badgeY + 17);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }

    public boolean isPlaying() {
        return playing;
    }

    public Snake getSnake() {
        return snake;
    }
}
