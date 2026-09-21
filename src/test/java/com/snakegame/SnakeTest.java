package com.snakegame;

import com.snakegame.core.Apple;
import com.snakegame.core.Difficulty;
import com.snakegame.core.Direction;
import com.snakegame.core.FileLineIterator;
import com.snakegame.core.GoldenApple;
import com.snakegame.core.Snake;
import com.snakegame.core.SnakeGameBoard;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test suite verifying core game mechanics, boundary physics,
 * score persistence, and dynamic resolution scaling.
 */
public class SnakeTest {

    @Test
    public void testSnakeMove() {
        Snake snake = new Snake(400, 400);
        snake.grow(1);
        snake.setVx(3);
        snake.setVy(3);
        Point first = snake.getGameObjects().get(0).getLocation();
        snake.move();
        assertEquals(snake.getGameObjects().get(1).getLocation(), first);
        assertEquals(snake.getGameObjects().get(0).getLocation(), new Point(23, 23));
    }

    @Test
    public void testSnakeGrow() {
        Snake snake = new Snake(400, 400);
        assertEquals(1, snake.getGameObjects().size());
        snake.grow(3);
        assertEquals(4, snake.getGameObjects().size());
    }

    @Test
    public void testAppleAdd() {
        Apple apple = new Apple(400, 400);
        assertEquals(1, apple.getGameObjects().size());
        apple.add();
        assertEquals(2, apple.getGameObjects().size());
    }

    @Test
    public void testAppleRemove() {
        Apple apple = new Apple(400, 400);
        assertEquals(1, apple.getGameObjects().size());
        apple.remove(0);
        assertEquals(0, apple.getGameObjects().size());
    }

    @Test
    public void testAppleIntersects() {
        Snake snake = new Snake(400, 400);
        Apple apple = new Apple(400, 400);
        Point p = apple.getGameObjects().get(0).getLocation();
        snake.setPx(p.x);
        snake.setPy(p.y);
        assertTrue(apple.intersects(snake));
    }

    @Test
    public void testApplePowerUp() {
        Snake snake = new Snake(400, 400);
        Apple apple = new Apple(400, 400);
        apple.powerUp(snake);
        assertEquals(4, snake.getGameObjects().size());
    }

    @Test
    public void testGoldenAppleAdd() {
        GoldenApple goldenApple = new GoldenApple(400, 400);
        assertEquals(1, goldenApple.getGameObjects().size());
        goldenApple.add();
        assertEquals(2, goldenApple.getGameObjects().size());
    }

    @Test
    public void testGoldenAppleRemove() {
        GoldenApple goldenApple = new GoldenApple(400, 400);
        assertEquals(1, goldenApple.getGameObjects().size());
        goldenApple.remove(0);
        assertEquals(0, goldenApple.getGameObjects().size());
    }

    @Test
    public void testGoldenAppleIntersects() {
        Snake snake = new Snake(400, 400);
        GoldenApple goldenApple = new GoldenApple(400, 400);
        Point p = goldenApple.getGameObjects().get(0).getLocation();
        snake.setPx(p.x);
        snake.setPy(p.y);
        assertTrue(goldenApple.intersects(snake));
    }

    @Test
    public void testGoldenApplePowerUp() {
        Snake snake = new Snake(400, 400);
        GoldenApple goldenApple = new GoldenApple(400, 400);
        goldenApple.powerUp(snake);
        assertEquals(4, snake.getSnakeVX());
        assertEquals(4, snake.getSnakeVY());
    }

    @Test
    public void testSnakeHasHitWall() {
        Snake snake = new Snake(400, 400);
        snake.setVx(100);
        snake.setVy(0);
        snake.setPx(500);
        assertEquals(Direction.RIGHT, snake.hitWall());
        assertTrue(snake.hasHitWall());

        snake.setVx(0);
        snake.setVy(100);
        snake.setPx(20);
        snake.setPy(500);
        assertEquals(Direction.DOWN, snake.hitWall());
        assertTrue(snake.hasHitWall());

        snake.setVx(-100);
        snake.setVy(0);
        snake.setPx(-500);
        snake.setPy(20);
        assertEquals(Direction.LEFT, snake.hitWall());
        assertTrue(snake.hasHitWall());

        snake.setVx(0);
        snake.setVy(-100);
        snake.setPx(20);
        snake.setPy(-500);
        assertEquals(Direction.UP, snake.hitWall());
        assertTrue(snake.hasHitWall());
    }

    @Test
    public void testSnakeHasHitItself() {
        Snake snake = new Snake(400, 400);
        snake.getGameObjects().add(new Point(30, 30));
        snake.setVx(10);
        snake.setVy(10);
        assertTrue(snake.hasHitItself());
    }

    @Test
    public void testFullScreenMovementAndDynamicBounds() {
        Snake fullScreenSnake = new Snake(1920, 1080);
        fullScreenSnake.setPx(700);
        fullScreenSnake.setPy(500);
        fullScreenSnake.setVx(3);
        fullScreenSnake.setVy(0);

        assertFalse(fullScreenSnake.hasHitWall());
        assertNull(fullScreenSnake.hitWall());

        fullScreenSnake.move();
        assertEquals(new Point(703, 500), fullScreenSnake.getGameObjects().get(0).getLocation());
        assertFalse(fullScreenSnake.hasHitWall());

        Snake resizedSnake = new Snake(600, 400);
        resizedSnake.updateBounds(1920, 1080);
        resizedSnake.setPx(800);
        resizedSnake.setPy(600);
        resizedSnake.setVx(5);
        resizedSnake.setVy(0);

        assertFalse(resizedSnake.hasHitWall());
        assertNull(resizedSnake.hitWall());

        resizedSnake.move();
        assertEquals(new Point(805, 600), resizedSnake.getGameObjects().get(0).getLocation());
        assertFalse(resizedSnake.hasHitWall());

        resizedSnake.setPx(1910);
        resizedSnake.setVx(5);
        assertTrue(resizedSnake.hasHitWall());
        assertEquals(Direction.RIGHT, resizedSnake.hitWall());
    }

    @Test
    public void testGameBoardFullScreenMovement() {
        JLabel status = new JLabel();
        SnakeGameBoard board = new SnakeGameBoard(status);
        board.setSize(1920, 1080);
        board.reset();

        assertTrue(board.isPlaying());
        Snake snake = board.getSnake();
        assertNotNull(snake);

        snake.setPx(590);
        snake.setPy(300);
        snake.setVx(10);
        snake.setVy(0);

        for (int i = 0; i < 20; i++) {
            board.begin();
            assertTrue(board.isPlaying(), "Snake should NOT crash at x=" + snake.getPx() + " in full screen");
        }

        assertTrue(snake.getPx() > 600);
        assertTrue(board.isPlaying());
    }

    @Test
    public void testCannotSaveWhenGameOver() {
        JLabel status = new JLabel();
        SnakeGameBoard board = new SnakeGameBoard(status);
        board.reset();
        assertTrue(board.isPlaying());

        Snake snake = board.getSnake();
        snake.setPx(board.getBoardWidth() - 5);
        snake.setVx(10);
        board.begin();

        assertFalse(board.isPlaying());

        boolean saveResult = board.save();
        assertFalse(saveResult, "Saving should NOT be allowed when the game is over");
        assertFalse(board.isPlaying());

        board.begin();
        assertFalse(board.isPlaying());

        board.reset();
        assertTrue(board.isPlaying());
    }

    @Test
    public void testFileLineIterator() throws IOException {
        File temp = File.createTempFile("test_iter", ".txt");
        temp.deleteOnExit();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            bw.write("Line 1");
            bw.newLine();
            bw.write("Line 2");
            bw.newLine();
        }

        FileLineIterator iterator = new FileLineIterator(temp.getAbsolutePath());
        assertTrue(iterator.hasNext());
        assertEquals("Line 1", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Line 2", iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void testDifficultyCycleAndDelays() {
        assertEquals(Difficulty.MEDIUM, Difficulty.EASY.next());
        assertEquals(Difficulty.HARD, Difficulty.MEDIUM.next());
        assertEquals(Difficulty.EASY, Difficulty.HARD.next());

        assertTrue(Difficulty.EASY.getDelayMs() > Difficulty.MEDIUM.getDelayMs(),
                "Easy difficulty should have longer delay (slower speed)");
        assertTrue(Difficulty.MEDIUM.getDelayMs() > Difficulty.HARD.getDelayMs(),
                "Medium difficulty should have longer delay than Hard");
    }

    @Test
    public void testBoardDifficultyAndPause() {
        JLabel status = new JLabel();
        SnakeGameBoard board = new SnakeGameBoard(status);
        board.reset();

        assertEquals(Difficulty.MEDIUM, board.getDifficulty());
        board.cycleDifficulty();
        assertEquals(Difficulty.HARD, board.getDifficulty());
        assertTrue(status.getText().contains("HARD"));

        board.cycleDifficulty();
        assertEquals(Difficulty.EASY, board.getDifficulty());
        assertTrue(status.getText().contains("EASY"));

        assertFalse(board.isPaused());
        board.togglePause();
        assertTrue(board.isPaused());
        assertTrue(status.getText().contains("[PAUSED]"));

        int initialPx = board.getSnake().getPx();
        board.begin();
        assertEquals(initialPx, board.getSnake().getPx(), "Snake should not advance while paused");

        board.togglePause();
        assertFalse(board.isPaused());
    }

    @Test
    public void testUpdatedScoringSystem() {
        JLabel status = new JLabel();
        SnakeGameBoard board = new SnakeGameBoard(status);
        board.reset();

        Snake snake = board.getSnake();
        snake.setPx(50);
        snake.setPy(50);

        board.begin();
        assertTrue(status.getText().contains("SCORE: 0"));
    }
}
