package com.snakegame.ai;

import com.snakegame.core.Apple;
import com.snakegame.core.Direction;
import com.snakegame.core.GoldenApple;
import com.snakegame.core.Snake;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Intelligent Autopilot Controller for the Snake Game.
 *
 * <p>Implements Breadth-First Search (BFS) pathfinding in actual pixel-space,
 * using the snake's real movement step size (snakeVX/snakeVY) as the grid resolution.
 * This guarantees that every cell explored by BFS corresponds to a position the snake
 * can physically reach, eliminating oscillation and misaligned path calculations.</p>
 *
 * <p>Algorithm:</p>
 * <ol>
 *   <li>Run BFS from head position toward the nearest food target using ±svx / ±svy steps.</li>
 *   <li>Return the first-step Direction from the optimal BFS path.</li>
 *   <li>If no path exists (snake surrounded), fall back to the direction with most open space.</li>
 * </ol>
 */
public class SnakeAI {

    /** Maximum BFS nodes to explore before giving up on pathfinding. */
    private static final int BFS_LIMIT = 8000;

    /** Maximum flood-fill cells for survival heuristic. */
    private static final int FLOOD_LIMIT = 200;

    /**
     * Returns the next best Direction for the snake to move.
     */
    public static Direction calculateNextMove(
            Snake snake, Apple apple, GoldenApple goldenApple,
            int courtWidth, int courtHeight
    ) {
        if (snake == null) {
            return null;
        }

        int svx = snake.getSnakeVX();
        int svy = snake.getSnakeVY();
        int maxX = courtWidth - snake.getWidth();
        int maxY = courtHeight - snake.getHeight();
        int segSize = snake.getWidth(); // 10 pixels

        Point target = selectTarget(snake, apple, goldenApple);

        // Step 1: BFS toward target
        if (target != null) {
            Direction d = bfsToTarget(snake, target, svx, svy, maxX, maxY, segSize);
            if (d != null) {
                return d;
            }
        }

        // Step 2: Fallback – pick the direction with most reachable open space
        return survivorMove(snake, svx, svy, maxX, maxY, segSize);
    }

    /**
     * A* search in pixel-space: finds the path to the target that minimizes
     * (steps_taken + manhattan_distance_remaining). This ensures the snake takes
     * the most DIRECT route to food, not just any short route.
     *
     * @return The first Direction to take along the optimal path, or null if unreachable.
     */
    private static Direction bfsToTarget(
            Snake snake, Point target,
            int svx, int svy, int maxX, int maxY, int segSize
    ) {
        int px = snake.getPx();
        int py = snake.getPy();

        // Priority queue: {priority, x, y, stepsTaken, initialDirectionOrdinal}
        // priority = stepsTaken + manhattan_distance_to_target (A* heuristic)
        PriorityQueue<long[]> open = new PriorityQueue<>(
                (a, b) -> Long.compare(a[0], b[0])
        );
        // Visited map: position key → minimum steps to reach it
        Map<Long, Integer> visited = new HashMap<>();

        // Seed with all valid first-step moves
        for (Direction dir : Direction.values()) {
            if (isReversal(snake, dir)) {
                continue;
            }
            int nx = px + pixelDX(dir, svx);
            int ny = py + pixelDY(dir, svy);
            if (outOfBounds(nx, ny, 0, 0, maxX, maxY)) {
                continue;
            }
            if (bodyCollides(nx, ny, snake, segSize)) {
                continue;
            }
            long key = encode(nx, ny);
            int h = Math.abs(nx - target.x) + Math.abs(ny - target.y);
            long priority = 1L + h; // f = g(1 step) + h
            open.add(new long[]{priority, nx, ny, 1L, dir.ordinal()});
            visited.put(key, 1);
        }

        int iterations = 0;
        while (!open.isEmpty() && iterations < BFS_LIMIT) {
            long[] node = open.poll();
            int cx = (int) node[1];
            int cy = (int) node[2];
            int steps = (int) node[3];
            Direction initDir = Direction.values()[(int) node[4]];
            iterations++;

            // Reached the apple vicinity (apple has 20px hit area)
            if (Math.abs(cx - target.x) <= 22 && Math.abs(cy - target.y) <= 22) {
                return initDir;
            }

            // Expand neighbors
            for (Direction dir : Direction.values()) {
                int nx = cx + pixelDX(dir, svx);
                int ny = cy + pixelDY(dir, svy);
                if (outOfBounds(nx, ny, 0, 0, maxX, maxY)) {
                    continue;
                }
                if (bodyCollides(nx, ny, snake, segSize)) {
                    continue;
                }
                long key = encode(nx, ny);
                int newSteps = steps + 1;
                if (!visited.containsKey(key) || visited.get(key) > newSteps) {
                    visited.put(key, newSteps);
                    int h = Math.abs(nx - target.x) + Math.abs(ny - target.y);
                    long priority = newSteps + h;
                    open.add(new long[]{priority, nx, ny, newSteps, (long) initDir.ordinal()});
                }
            }
        }

        return null; // No path found within budget
    }

    /**
     * Survival fallback: chooses the move that leads to the largest open space.
     * Used when the target is completely surrounded and BFS fails.
     */
    private static Direction survivorMove(
            Snake snake, int svx, int svy, int maxX, int maxY, int segSize
    ) {
        int px = snake.getPx();
        int py = snake.getPy();

        Direction bestDir = null;
        int bestSpace = -1;

        for (Direction dir : Direction.values()) {
            if (isReversal(snake, dir)) {
                continue;
            }
            int nx = px + pixelDX(dir, svx);
            int ny = py + pixelDY(dir, svy);
            if (outOfBounds(nx, ny, 0, 0, maxX, maxY)) {
                continue;
            }
            if (bodyCollides(nx, ny, snake, segSize)) {
                continue;
            }
            int space = floodCount(nx, ny, snake, svx, svy, 0, 0, maxX, maxY, segSize, FLOOD_LIMIT);
            if (space > bestSpace) {
                bestSpace = space;
                bestDir = dir;
            }
        }

        return bestDir;
    }

    /**
     * Flood-fill counter: counts reachable pixel positions reachable from (startX, startY)
     * expanding in ±svx / ±svy steps, up to {@code limit} cells.
     */
    private static int floodCount(
            int startX, int startY, Snake snake,
            int svx, int svy, int minX, int minY, int maxX, int maxY, int segSize, int limit
    ) {
        Set<Long> visited = new HashSet<>();
        Queue<long[]> queue = new ArrayDeque<>();

        long startKey = encode(startX, startY);
        visited.add(startKey);
        queue.add(new long[]{startX, startY});

        int count = 0;
        while (!queue.isEmpty() && count < limit) {
            long[] pos = queue.poll();
            count++;
            int cx = (int) pos[0];
            int cy = (int) pos[1];

            for (Direction dir : Direction.values()) {
                int nx = cx + pixelDX(dir, svx);
                int ny = cy + pixelDY(dir, svy);
                if (outOfBounds(nx, ny, minX, minY, maxX, maxY)) {
                    continue;
                }
                long key = encode(nx, ny);
                if (!visited.contains(key) && !bodyCollides(nx, ny, snake, segSize)) {
                    visited.add(key);
                    queue.add(new long[]{nx, ny});
                }
            }
        }
        return count;
    }

    /**
     * Checks whether (nx, ny) overlaps any snake body segment except the tail tip
     * (which vacates its cell each tick before the head arrives).
     */
    private static boolean bodyCollides(int nx, int ny, Snake snake, int segSize) {
        LinkedList<Point> body = snake.getGameObjects();
        if (body == null || body.size() <= 3) {
            return false;
        }
        // Skip index 0 (head), index 1 (immediate neck), and index 2 (diagonal corner)
        // Check from index 3 (first point reachable via a 4-step loop) up to last-1
        int last = body.size() - 1;
        for (int i = 3; i < last; i++) {
            Point seg = body.get(i);
            if (nx == seg.x && ny == seg.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Selects the highest-priority food target.
     * Golden Apples are preferred; among regular apples, the closest is chosen.
     */
    private static Point selectTarget(Snake snake, Apple apple, GoldenApple goldenApple) {
        if (goldenApple != null && !goldenApple.getGameObjects().isEmpty()) {
            return goldenApple.getGameObjects().getFirst();
        }
        if (apple != null && !apple.getGameObjects().isEmpty()) {
            int hx = snake.getPx(), hy = snake.getPy();
            Point closest = null;
            int minDist = Integer.MAX_VALUE;
            for (Point p : apple.getGameObjects()) {
                int dist = Math.abs(p.x - hx) + Math.abs(p.y - hy);
                if (dist < minDist) {
                    minDist = dist;
                    closest = p;
                }
            }
            return closest;
        }
        return null;
    }

    /**
     * Applies the chosen Direction by setting the snake's velocity components.
     */
    public static void applyDirection(Snake snake, Direction dir) {
        if (snake == null || dir == null) {
            return;
        }
        int svx = snake.getSnakeVX();
        int svy = snake.getSnakeVY();
        switch (dir) {
            case LEFT:
                snake.setVx(-svx);
                snake.setVy(0);
                break;
            case RIGHT:
                snake.setVx(svx);
                snake.setVy(0);
                break;
            case UP:
                snake.setVx(0);
                snake.setVy(-svy);
                break;
            case DOWN:
                snake.setVx(0);
                snake.setVy(svy);
                break;
        }
    }

    /**
     * Checks if moving in a direction would immediately hit a wall or body segment.
     */
    public static boolean isMoveSafe(Snake snake, Direction dir, int courtWidth, int courtHeight) {
        if (snake == null || dir == null) {
            return false;
        }
        int svx = snake.getSnakeVX();
        int svy = snake.getSnakeVY();
        int nx = snake.getPx() + pixelDX(dir, svx);
        int ny = snake.getPy() + pixelDY(dir, svy);
        int maxX = courtWidth - snake.getWidth();
        int maxY = courtHeight - snake.getHeight();
        if (outOfBounds(nx, ny, 0, 0, maxX, maxY)) {
            return false;
        }
        return !bodyCollides(nx, ny, snake, snake.getWidth());
    }

    // -------------------------------------------------------------------------
    // Utility helpers
    // -------------------------------------------------------------------------

    /**
     * Prevents instant 180-degree reversal into the neck segment.
     * When snake is stationary (vx=vy=0) no direction is blocked.
     */
    private static boolean isReversal(Snake snake, Direction dir) {
        if (snake.getVx() == 0 && snake.getVy() == 0) {
            return false; // Snake hasn't started yet — all directions allowed
        }
        switch (dir) {
            case LEFT:  return snake.getVx() > 0;
            case RIGHT: return snake.getVx() < 0;
            case UP:    return snake.getVy() > 0;
            case DOWN:  return snake.getVy() < 0;
        }
        return false;
    }

    private static boolean outOfBounds(int x, int y, int minX, int minY, int maxX, int maxY) {
        return x < minX || x > maxX || y < minY || y > maxY;
    }

    private static int pixelDX(Direction dir, int svx) {
        if (dir == Direction.LEFT)  return -svx;
        if (dir == Direction.RIGHT) return  svx;
        return 0;
    }

    private static int pixelDY(Direction dir, int svy) {
        if (dir == Direction.UP)   return -svy;
        if (dir == Direction.DOWN) return  svy;
        return 0;
    }

    /**
     * Encodes (x, y) into a unique long key for HashMap/HashSet storage.
     * Supports values up to 32767 in each axis.
     */
    private static long encode(int x, int y) {
        return ((long) (x & 0x7FFF) << 15) | (y & 0x7FFF);
    }
}
