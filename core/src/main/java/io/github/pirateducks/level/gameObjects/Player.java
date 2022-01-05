package io.github.pirateducks.level.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.pirateducks.level.GameObject;
import io.github.pirateducks.level.GameObjectHealth;
import io.github.pirateducks.level.LevelManager;
import io.github.pirateducks.screen.GameOverScreen;

public class Player extends GameObjectHealth {
    //Body is an object yet to be defined which will be defined as the main Player

    private final Texture texture;
    private float rotation;
    private final LevelManager manager;
    private final OrthographicCamera camera;
    private float timeFired = 0;

    public Player(LevelManager manager, OrthographicCamera camera) {
        super(45, 55);

        this.camera = camera;
        this.manager = manager;
        // loading the texture
        texture = new Texture(Gdx.files.internal("DuckBoat_TopView.png"));
        rotation = 0;
        maxHealth = 6;
        health = 6;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width / 2, height / 2, width, height, 1, 1, rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    private final double SPEED = 5;

    @Override
    public void update(float delta) {
        float vel_x = 0;   //Replace the x to find optimal moving speed
        float vel_y = 0;   //Replace the y to find optimal moving speed

        double deltaSpeed = SPEED + delta;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            vel_y += deltaSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            vel_y -= deltaSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            vel_x -= deltaSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            vel_x += deltaSpeed;
        }

        if (vel_x != 0 || vel_y != 0) {
            rotation = (float) Math.toDegrees(-Math.atan2(vel_x, vel_y));
        }

        float newX = x + vel_x, newY = y + vel_y;
        // testing if the boat is on land, if the boat is on land reducing the speed of the boat until it is not
        while (manager.isOnLand(newX + width / 2, newY + height / 2) && (vel_x != 0 || vel_y != 0)) {
            // reducing the velocity until a valid speed is found
            vel_x *= 0.9;
            vel_y *= 0.9;
            // applying the new speed
            newX = x + vel_x;
            newY = y + vel_y;

        }

        x = newX;
        y = newY;
        // limiting x
        if (x <= -width / 2) {
            x = -width / 2;
        } else if (x >= camera.viewportWidth - width / 2) {
            x = camera.viewportWidth - width / 2;
        }

        // limiting y
        if (y <= -height / 2) {
            y = -height / 2;
        } else if (y >= camera.viewportHeight - height / 2) {
            y = camera.viewportHeight - height / 2;
        }

        // firing code
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Cannonballs can only be fired once every 2 seconds
            if (timeFired > 2) {
                setHealth(health - 1);
                // Mouse position coordinates start in top left, whereas game coordinates start in bottom left
                // inverse them before use
                int mouseX = Gdx.input.getX();
                int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
                // Center of boat sprite
                float playerCenterX = x + width / 2;
                float playerCenterY = y + height / 2;
                // Fire a cannonball from boat center to mouse position
                manager.addObject(new CannonBall(playerCenterX, playerCenterY, mouseX, mouseY, this, manager, camera));
                timeFired = 0;
            }
        }
        // Add delay between shots
        timeFired += delta;
    }

    public void dispose() {
        texture.dispose();
    }

    private int health;
    private int maxHealth;

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
        if (health <= 0) {
            manager.getMainClass().setCurrentScreen(new GameOverScreen());
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Set the max health of the player, NOTE: This number must be even
     *
     * @param maxHealth The max health of the player
     */
    public void setMaxHealth(int maxHealth) {
        if (maxHealth % 2 != 0) {
            throw new IllegalArgumentException("The game currently cannot render a max health which is odd.");
        }
        this.maxHealth = maxHealth;
    }
}