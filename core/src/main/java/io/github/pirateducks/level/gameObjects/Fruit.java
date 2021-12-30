package io.github.pirateducks.level.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.pirateducks.level.GameObject;
import io.github.pirateducks.level.LevelManager;

public class Fruit extends GameObject {

    private final Texture texture;
    private final LevelManager manager;
    private final OrthographicCamera camera;
    private double angle;
    private String fruitChoice = "apple.png";

    private int startX;
    private int startY;

    private int blastPower = 10; // Amount of damage fruit does

    /**
     * Fruit
     * Be able to set an image for the fruit: Yes
     * Be able to change between apple, banana, melon: Yes
     * Be able to change size: Yes
     * Be able to start the fruit from outside the window: Maybe
     * Be able to explode when hit: No
     */

    public Fruit(float playerX, float playerY, int size, int select, LevelManager manager, OrthographicCamera camera) {
        super(100, 100);

        this.camera = camera;
        this.manager = manager;

        // Allows the fruit to be changed using an int value
        if (select == 0) {
            fruitChoice = "apple.png";
        }else if (select == 1) {
            fruitChoice = "banana.png";
        }else if (select == 2) {
            fruitChoice = "melon.png";
        }else if (select == 3) {
            fruitChoice = "bomb.png";
        }

        // loads the texture
        texture = new Texture(Gdx.files.internal(fruitChoice));

        // scales the sprite depending on window size multiplied by a constant
        float scaleRatio = ((float) texture.getWidth() / (float) Gdx.graphics.getWidth()) * 135f;
        // increases the sprite based on the multiplier 'size' (default=1)
        SetSize((texture.getWidth() / scaleRatio) * size, (texture.getHeight() / scaleRatio) * size);

        x = playerX;
        y = playerY;

        startX = 0;
        startY = 0;

        // We use a triangle to calculate the new trajectory
        angle = Math.atan2(startY - y, startX - x);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width / 2, height / 2, width, height, 1, 1, 0, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    @Override
    public void update(float delta) {
        float velocity = 200 * delta;
        x += Math.cos(angle) * velocity;
        y += Math.sin(angle) * velocity;

        // limiting x
        if (x <= -width / 2 || x >= camera.viewportWidth - width / 2) {
            dispose();
        }

        // limiting y
        if (y <= -height / 2 || y >= camera.viewportHeight - height / 2) {
            dispose();
        }
    }

    public void dispose() {
        texture.dispose();
        // Remove fruit object from the list of objects to be rendered
        manager.removeObject(this);
    }

    public void explode() {
        // Display explosion animation over the fruit

        // Then remove the fruit
        dispose();
    }

}

