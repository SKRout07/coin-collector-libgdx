package com.skr.coincollector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {

    // Renderer
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    BitmapFont font;

    // Camera (RESPONSIVE FIX)
    OrthographicCamera camera;

    // Virtual world size
    final float WORLD_WIDTH = 800;
    final float WORLD_HEIGHT = 480;

    // Player
    float playerX = 100;
    float playerY = 100;
    float playerSize = 50;

    // Coin
    float coinX;
    float coinY;
    float coinSize = 50;

    // Game state
    int score = 0;
    float timeLeft = 60;
    boolean gameOver = false;

    @Override
    public void create() {

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        // Camera setup (IMPORTANT)
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        spawnCoin();
    }

    // Spawn coin inside world bounds
    public void spawnCoin() {
        coinX = (float) Math.random() * (WORLD_WIDTH - coinSize);
        coinY = (float) Math.random() * (WORLD_HEIGHT - coinSize);
    }

    @Override
    public void render() {

        // Update camera
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Timer
        if (!gameOver) {
            timeLeft -= Gdx.graphics.getDeltaTime();
            if (timeLeft <= 0) {
                timeLeft = 0;
                gameOver = true;
            }
        }

        // Player movement only if game running
        if (!gameOver) {

            float speed = 200 * Gdx.graphics.getDeltaTime();

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) playerX -= speed;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) playerX += speed;
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) playerY += speed;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) playerY -= speed;

            // Boundaries
            if (playerX < 0) playerX = 0;
            if (playerY < 0) playerY = 0;

            if (playerX > WORLD_WIDTH - playerSize)
                playerX = WORLD_WIDTH - playerSize;

            if (playerY > WORLD_HEIGHT - playerSize)
                playerY = WORLD_HEIGHT - playerSize;

            // Collision detection
            if (playerX < coinX + coinSize &&
                playerX + playerSize > coinX &&
                playerY < coinY + coinSize &&
                playerY + playerSize > coinY) {

                score++;
                spawnCoin();
            }
        }

        // Draw shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Player
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(playerX, playerY, playerSize, playerSize);

        // Coin
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(coinX, coinY, coinSize, coinSize);

        shapeRenderer.end();

        // UI
        batch.begin();

        font.draw(batch, "Score: " + score, 20, WORLD_HEIGHT - 20);
        font.draw(batch, "Time: " + (int) timeLeft, 20, WORLD_HEIGHT - 40);

        // Win/Lose screen
        if (gameOver) {

            String msg;

            if (score >= 10) {
                msg = "YOU WIN!";
            } else {
                msg = "GAME OVER";
            }

            font.draw(batch,
                msg,
                WORLD_WIDTH / 2 - 40,
                WORLD_HEIGHT / 2
            );
        }

        batch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
