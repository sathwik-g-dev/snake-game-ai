package com.snakegame.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

/**
 * Snake entity representing the player or AI controlled serpent.
 * Manages body segment coordinates, motion updates, tail elongation (growth),
 * and self-collision detection.
 */
public class Snake extends GameObj {

    public static final int SIZE = 10;
    public static final int INIT_POS_X = 20;
    public static final int INIT_POS_Y = 20;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;

    private int snakeVX = 3;
    private int snakeVY = 3;

    public Snake(int boardWidth, int boardHeight) {
        super(INIT_VEL_X, INIT_VEL_Y, INIT_POS_X, INIT_POS_Y, SIZE, SIZE, boardWidth, boardHeight);
    }

    public Snake(
            int positionX, int positionY, int boardWidth,
            int boardHeight, LinkedList<Point> objs
    ) {
        super(INIT_VEL_X, INIT_VEL_Y, positionX, positionY, SIZE, SIZE, boardWidth, boardHeight, objs);
    }

    public int getSnakeVX() {
        return snakeVX;
    }

    public int getSnakeVY() {
        return snakeVY;
    }

    public void setSnakeVX(int velocity) {
        this.snakeVX = velocity;
    }

    public void setSnakeVY(int velocity) {
        this.snakeVY = velocity;
    }

    @Override
    public void draw(Graphics g) {
        LinkedList<Point> body = getGameObjects();
        if (body.isEmpty()) {
            return;
        }

        // Draw body segments
        g.setColor(new Color(34, 139, 34)); // Forest green body
        for (int i = 1; i < body.size(); i++) {
            Point p = body.get(i);
            g.fillRoundRect(p.x, p.y, SIZE, SIZE, 4, 4);
        }

        // Draw head segment with distinct styling
        Point head = body.getFirst();
        g.setColor(new Color(0, 100, 0)); // Darker green head
        g.fillRoundRect(head.x, head.y, SIZE, SIZE, 6, 6);

        // Eye highlights for visual polish
        g.setColor(Color.WHITE);
        g.fillRect(head.x + 2, head.y + 2, 2, 2);
        g.fillRect(head.x + 6, head.y + 2, 2, 2);
    }

    @Override
    public void move() {
        LinkedList<Point> body = getGameObjects();
        // Shift each trailing segment to the position of the segment ahead of it
        for (int i = body.size() - 1; i >= 1; i--) {
            body.get(i).setLocation(body.get(i - 1));
        }

        // Advance head position
        setPx(getPx() + getVx());
        setPy(getPy() + getVy());
        body.set(0, new Point(getPx(), getPy()));

        clip();
    }

    /**
     * Appends new tail segments when food is ingested.
     *
     * @param length Number of segments to grow
     */
    public void grow(int length) {
        for (int i = 0; i < length; i++) {
            LinkedList<Point> current = getGameObjects();
            current.add(new Point(current.getLast()));
            setGameObjects(current);
        }
    }

    /**
     * Checks if the head's projected step intersects any following body segment.
     *
     * @return true if self-collision occurs, false otherwise
     */
    public boolean hasHitItself() {
        LinkedList<Point> body = getGameObjects();
        if (body.size() > 1) {
            Point head = body.getFirst();
            int nextX = head.x + getVx();
            int nextY = head.y + getVy();

            for (int i = 1; i < body.size(); i++) {
                Point segment = body.get(i);
                if (nextX == segment.x && nextY == segment.y) {
                    return true;
                }
            }
        }
        return false;
    }
}
