package com.snakegame.core;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Standard food item that yields points and causes the snake to grow in length.
 */
public class Apple extends GameObj implements Food {

    public static final int SIZE = 20;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;

    private BufferedImage appleImg;

    public Apple(int boardWidth, int boardHeight) {
        super(
                INIT_VEL_X, INIT_VEL_Y,
                (int) (Math.random() * Math.max(1, boardWidth - SIZE)),
                (int) (Math.random() * Math.max(1, boardHeight - SIZE)),
                SIZE, SIZE, boardWidth, boardHeight
        );
        loadImage();
    }

    public Apple(
            int positionX, int positionY, int boardWidth,
            int boardHeight, LinkedList<Point> objs
    ) {
        super(
                INIT_VEL_X, INIT_VEL_Y, positionX, positionY,
                SIZE, SIZE, boardWidth, boardHeight, objs
        );
        loadImage();
    }

    private void loadImage() {
        try {
            File imgFile = new File("files/apple.png");
            if (imgFile.exists()) {
                appleImg = ImageIO.read(imgFile);
            }
        } catch (IOException e) {
            appleImg = null;
        }
    }

    @Override
    public void powerUp(Snake snake) {
        snake.grow(3);
    }

    @Override
    public void draw(Graphics g) {
        if (appleImg != null) {
            for (Point p : getGameObjects()) {
                g.drawImage(appleImg, p.x, p.y, SIZE, SIZE, null);
            }
        } else {
            for (Point p : getGameObjects()) {
                g.setColor(new Color(220, 20, 60)); // Crimson red
                g.fillOval(p.x, p.y, SIZE, SIZE);
                g.setColor(new Color(34, 139, 34)); // Green leaf
                g.fillRect(p.x + (SIZE / 2) - 1, p.y - 2, 3, 4);
            }
        }
    }

    @Override
    public boolean intersects(GameObj that) {
        for (int i = 0; i < getGameObjects().size(); i++) {
            Point p = getGameObjects().get(i);
            if (p.x + getWidth() >= that.getPx()
                    && p.y + getHeight() >= that.getPy()
                    && that.getPx() + that.getWidth() >= p.x
                    && that.getPy() + that.getHeight() >= p.y) {
                remove(i);
                return true;
            }
        }
        return false;
    }
}
