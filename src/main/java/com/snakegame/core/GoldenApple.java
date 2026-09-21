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
 * Rare bonus item that boosts the snake's velocity and grants bonus points.
 */
public class GoldenApple extends GameObj implements Food {

    public static final int SIZE = 20;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 0;

    private BufferedImage goldenAppleImg;

    public GoldenApple(int boardWidth, int boardHeight) {
        super(
                INIT_VEL_X, INIT_VEL_Y,
                (int) (Math.random() * Math.max(1, boardWidth - SIZE)),
                (int) (Math.random() * Math.max(1, boardHeight - SIZE)),
                SIZE, SIZE, boardWidth, boardHeight
        );
        loadImage();
    }

    public GoldenApple(
            int positionX, int positionY, int boardWidth,
            int boardHeight, LinkedList<Point> objs
    ) {
        super(
                INIT_VEL_X, INIT_VEL_Y, positionX, positionY, SIZE,
                SIZE, boardWidth, boardHeight, objs
        );
        loadImage();
    }

    private void loadImage() {
        try {
            File imgFile = new File("files/goldenapple.png");
            if (imgFile.exists()) {
                goldenAppleImg = ImageIO.read(imgFile);
            }
        } catch (IOException e) {
            goldenAppleImg = null;
        }
    }

    @Override
    public void powerUp(Snake snake) {
        snake.setSnakeVX(snake.getSnakeVX() + 1);
        snake.setSnakeVY(snake.getSnakeVY() + 1);
        if (snake.getVx() > 0) {
            snake.setVx(snake.getSnakeVX());
        } else if (snake.getVx() < 0) {
            snake.setVx(-snake.getSnakeVX());
        }
        if (snake.getVy() > 0) {
            snake.setVy(snake.getSnakeVY());
        } else if (snake.getVy() < 0) {
            snake.setVy(-snake.getSnakeVY());
        }
    }

    @Override
    public void draw(Graphics g) {
        if (goldenAppleImg != null) {
            for (Point p : getGameObjects()) {
                g.drawImage(goldenAppleImg, p.x, p.y, SIZE, SIZE, null);
            }
        } else {
            for (Point p : getGameObjects()) {
                g.setColor(new Color(255, 215, 0)); // Gold
                g.fillOval(p.x, p.y, SIZE, SIZE);
                g.setColor(new Color(255, 255, 224)); // Light sparkle highlight
                g.fillOval(p.x + 4, p.y + 4, 4, 4);
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
