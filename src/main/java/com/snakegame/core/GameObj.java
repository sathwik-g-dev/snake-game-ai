package com.snakegame.core;

import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

/**
 * Abstract base class representing any entity within the game court.
 * Encapsulates position coordinates, dimensions, velocity, collision boundaries,
 * and multi-point entity management.
 */
public abstract class GameObj {

    /* Current upper-left coordinate in court space */
    private int px;
    private int py;

    /* Entity dimensions in pixels */
    private final int width;
    private final int height;

    /* Velocity components (pixels displaced per game loop tick) */
    private int vx;
    private int vy;

    /* Maximum allowed coordinates based on court dimensions */
    private int maxX;
    private int maxY;

    /* Holds coordinate points for compound entities (e.g. multi-segment snake or fruit clusters) */
    private LinkedList<Point> gameObjects;

    /**
     * Primary constructor initializing a single-point game object.
     */
    public GameObj(
            int vx, int vy, int px, int py, int width, int height,
            int courtWidth, int courtHeight
    ) {
        this.vx = vx;
        this.vy = vy;
        this.px = px;
        this.py = py;
        this.width = width;
        this.height = height;

        this.maxX = courtWidth - width;
        this.maxY = courtHeight - height;

        this.gameObjects = new LinkedList<>();
        this.gameObjects.addFirst(new Point(px, py));
    }

    /**
     * Secondary constructor for initializing an object with existing coordinate points.
     */
    public GameObj(
            int vx, int vy, int px, int py, int width, int height,
            int courtWidth, int courtHeight, LinkedList<Point> objs
    ) {
        this.vx = vx;
        this.vy = vy;
        this.px = px;
        this.py = py;
        this.width = width;
        this.height = height;

        this.maxX = courtWidth - width;
        this.maxY = courtHeight - height;

        this.gameObjects = objs;
    }

    /* Getters */
    public int getPx() {
        return this.px;
    }

    public int getPy() {
        return this.py;
    }

    public int getVx() {
        return this.vx;
    }

    public int getVy() {
        return this.vy;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public LinkedList<Point> getGameObjects() {
        return this.gameObjects;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxY() {
        return this.maxY;
    }

    /* Setters */
    public void setPx(int px) {
        this.px = px;
        clip();
    }

    public void setPy(int py) {
        this.py = py;
        clip();
    }

    public void setVx(int vx) {
        this.vx = vx;
    }

    public void setVy(int vy) {
        this.vy = vy;
    }

    public void setGameObjects(LinkedList<Point> objs) {
        this.gameObjects = objs;
    }

    /**
     * Dynamically updates boundary limits when the game window is resized or maximized.
     *
     * @param courtWidth  New court pixel width
     * @param courtHeight New court pixel height
     */
    public void updateBounds(int courtWidth, int courtHeight) {
        this.maxX = courtWidth - this.width;
        this.maxY = courtHeight - this.height;
    }

    /**
     * Restricts entity coordinates within the permissible boundary box [0, maxX] x [0, maxY].
     */
    public void clip() {
        this.px = Math.min(Math.max(this.px, 0), this.maxX);
        this.py = Math.min(Math.max(this.py, 0), this.maxY);
    }

    /**
     * Displaces the object according to its current velocity vector.
     */
    public void move() {
        this.px += this.vx;
        this.py += this.vy;
        clip();
    }

    /**
     * Checks Axis-Aligned Bounding Box (AABB) intersection between this and another entity.
     *
     * @param that Other GameObj to compare against
     * @return true if bounding boxes overlap, false otherwise
     */
    public boolean intersects(GameObj that) {
        return (this.px + this.width >= that.px
                && this.py + this.height >= that.py
                && that.px + that.width >= this.px
                && that.py + that.height >= this.py);
    }

    /**
     * Predicts whether the object will collide with a boundary wall on the next step.
     *
     * @return Direction of impending wall collision, or null if trajectory is clear
     */
    public Direction hitWall() {
        if (this.px + this.vx < 0) {
            return Direction.LEFT;
        } else if (this.px + this.vx > this.maxX) {
            return Direction.RIGHT;
        }

        if (this.py + this.vy < 0) {
            return Direction.UP;
        } else if (this.py + this.vy > this.maxY) {
            return Direction.DOWN;
        } else {
            return null;
        }
    }

    /**
     * Checks if moving by current velocity causes wall collision.
     *
     * @return true if wall collision occurs, false otherwise
     */
    public boolean hasHitWall() {
        return (px + vx < 0) || (px + vx > maxX)
                || (py + vy < 0) || (py + vy > maxY);
    }

    /**
     * Spawns an additional point at a pseudo-random location within court boundaries.
     */
    public void add() {
        gameObjects.add(new Point((int) (Math.random() * maxX), (int) (Math.random() * maxY)));
    }

    /**
     * Removes the point at the specified index.
     *
     * @param index Target index to remove
     */
    public void remove(int index) {
        gameObjects.remove(index);
    }

    /**
     * Renders the entity onto the Swing graphics context. Subclasses override to provide
     * custom visual representations.
     *
     * @param g Graphics2D rendering context
     */
    public void draw(Graphics g) {
        // Default implementation does not render anything.
    }
}
