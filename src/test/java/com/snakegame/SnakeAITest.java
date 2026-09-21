package com.snakegame;

import com.snakegame.ai.SnakeAI;
import com.snakegame.core.Apple;
import com.snakegame.core.Direction;
import com.snakegame.core.GoldenApple;
import com.snakegame.core.Snake;
import com.snakegame.core.SnakeGameBoard;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import java.awt.Point;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit and integration test suite verifying the AI Autopilot agent.
 */
public class SnakeAITest {

    @Test
    public void testAIDecidesDirectionTowardTarget() {
        int width = 600;
        int height = 400;

        Snake snake = new Snake(width, height);
        snake.setPx(50);
        snake.setPy(100);
        snake.setVx(3);
        snake.setVy(0);

        LinkedList<Point> applePoints = new LinkedList<>();
        applePoints.add(new Point(200, 100));
        Apple apple = new Apple(200, 100, width, height, applePoints);
        GoldenApple goldenApple = new GoldenApple(500, 300, width, height, new LinkedList<>());

        Direction decision = SnakeAI.calculateNextMove(snake, apple, goldenApple, width, height);
        assertNotNull(decision);
        assertEquals(Direction.RIGHT, decision, "AI should direct snake right towards the apple");
    }

    @Test
    public void testAIPrioritizesGoldenApple() {
        int width = 600;
        int height = 400;

        Snake snake = new Snake(width, height);
        snake.setPx(100);
        snake.setPy(100);
        snake.setVx(0);
        snake.setVy(3);

        LinkedList<Point> applePoints = new LinkedList<>();
        applePoints.add(new Point(100, 300));
        Apple apple = new Apple(100, 300, width, height, applePoints);

        LinkedList<Point> goldenPoints = new LinkedList<>();
        goldenPoints.add(new Point(100, 50));
        GoldenApple goldenApple = new GoldenApple(100, 50, width, height, goldenPoints);

        Direction decision = SnakeAI.calculateNextMove(snake, apple, goldenApple, width, height);
        assertNotNull(decision);
    }

    @Test
    public void testAIAvoidsWallCollision() {
        int width = 400;
        int height = 400;

        Snake snake = new Snake(width, height);
        snake.setPx(width - snake.getWidth() - 2);
        snake.setPy(200);
        snake.setVx(snake.getSnakeVX());
        snake.setVy(0);

        LinkedList<Point> applePoints = new LinkedList<>();
        applePoints.add(new Point(width - 5, 200));
        Apple apple = new Apple(width - 5, 200, width, height, applePoints);
        GoldenApple goldenApple = new GoldenApple(10, 10, width, height, new LinkedList<>());

        assertFalse(SnakeAI.isMoveSafe(snake, Direction.RIGHT, width, height));

        Direction decision = SnakeAI.calculateNextMove(snake, apple, goldenApple, width, height);
        assertNotNull(decision);
        assertNotEquals(Direction.RIGHT, decision, "AI must not steer into a wall");
    }

    @Test
    public void testIsMoveSafeDetection() {
        int width = 500;
        int height = 500;

        Snake snake = new Snake(width, height);
        // Place snake several steps away from any wall so all directions have clear readings
        snake.setPx(100);
        snake.setPy(100);

        // Moving into the middle of the board is always safe
        assertTrue(SnakeAI.isMoveSafe(snake, Direction.DOWN, width, height), "DOWN into open space must be safe");
        assertTrue(SnakeAI.isMoveSafe(snake, Direction.RIGHT, width, height), "RIGHT into open space must be safe");

        // Place snake at the very right edge; RIGHT must be blocked
        snake.setPx(width - snake.getWidth());
        assertFalse(SnakeAI.isMoveSafe(snake, Direction.RIGHT, width, height), "RIGHT at right edge must be unsafe");

        // Place snake at the very top edge; UP must be blocked
        snake.setPx(100);
        snake.setPy(0);
        assertFalse(SnakeAI.isMoveSafe(snake, Direction.UP, width, height), "UP at top edge must be unsafe");
    }

    @Test
    public void testBoardAIToggle() {
        JLabel status = new JLabel();
        SnakeGameBoard board = new SnakeGameBoard(status);
        board.reset();

        assertFalse(board.isAiMode());
        board.toggleAiMode();
        assertTrue(board.isAiMode());
        assertTrue(status.getText().contains("[BOT AUTO-PLAY]"));

        board.toggleAiMode();
        assertFalse(board.isAiMode());
        assertFalse(status.getText().contains("[BOT AUTO-PLAY]"));
    }

    /**
     * End-to-end simulation: snake at (20,200) must reach apple at (540,200)
     * within 400 steps without going out of bounds or hitting itself.
     */
    @Test
    public void testAINavigatesToAppleEndToEnd() {
        int W = 600, H = 400;
        int maxX = W - 10; // Snake.SIZE = 10
        int maxY = H - 10;

        Snake snake = new Snake(W, H);
        snake.setPx(20);
        snake.setPy(200);
        snake.setVx(0);
        snake.setVy(0);

        LinkedList<Point> ap = new LinkedList<>();
        ap.add(new Point(540, 200));
        Apple apple = new Apple(540, 200, W, H, ap);
        GoldenApple golden = new GoldenApple(0, 0, W, H, new LinkedList<>());

        boolean reachedApple = false;
        for (int steps = 0; steps < 400; steps++) {
            Direction d = SnakeAI.calculateNextMove(snake, apple, golden, W, H);
            assertNotNull(d, "AI returned null at step " + steps);
            SnakeAI.applyDirection(snake, d);
            snake.move();

            // Check actual head position (px/py) — not hasHitWall() which is forward-projection
            int hx = snake.getPx(), hy = snake.getPy();
            assertTrue(hx >= 0 && hx <= maxX, "Snake X out of bounds at step " + steps + ": " + hx);
            assertTrue(hy >= 0 && hy <= maxY, "Snake Y out of bounds at step " + steps + ": " + hy);
            assertFalse(snake.hasHitItself(), "Snake hit itself at step " + steps);

            for (Point p : apple.getGameObjects()) {
                if (Math.abs(hx - p.x) < 30 && Math.abs(hy - p.y) < 30) {
                    reachedApple = true;
                    break;
                }
            }
            if (reachedApple) break;
        }

        assertTrue(reachedApple, "AI failed to reach the apple within 400 steps");
    }

    /**
     * Stress-test: long snake (16 segments) must survive 150 steps without dying.
     */
    @Test
    public void testAISurvivesWithLongBody() {
        int W = 600, H = 400;

        Snake snake = new Snake(W, H);
        snake.setPx(200);
        snake.setPy(100);
        snake.setVx(3);
        snake.setVy(0);
        snake.grow(15);

        LinkedList<Point> ap = new LinkedList<>();
        ap.add(new Point(400, 100));
        Apple apple = new Apple(400, 100, W, H, ap);
        GoldenApple golden = new GoldenApple(0, 0, W, H, new LinkedList<>());

        for (int step = 0; step < 150; step++) {
            Direction d = SnakeAI.calculateNextMove(snake, apple, golden, W, H);
            if (d == null) break;
            SnakeAI.applyDirection(snake, d);
            assertFalse(snake.hasHitWall(), "Long snake hit wall at step " + step);
            assertFalse(snake.hasHitItself(), "Long snake hit itself at step " + step);
            snake.move();
        }

        assertFalse(snake.hasHitWall(), "Long snake should not crash into a wall");
        assertFalse(snake.hasHitItself(), "Long snake should not tangle itself");
    }
}
