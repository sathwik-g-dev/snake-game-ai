package com.snakegame.core;

/**
 * Food interface defining power-up behaviors when consumed by the snake.
 * Follows the Strategy design pattern so different food types can apply
 * distinct effects (e.g. body growth, speed boosts, special score bonuses).
 */
public interface Food {
    /**
     * Applies the power-up effect to the consuming snake.
     *
     * @param snake The snake instance consuming the item
     */
    void powerUp(Snake snake);
}
