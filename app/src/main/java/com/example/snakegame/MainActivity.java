package com.example.snakegame;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends Activity {

    SnakeGameLoop snakeGameLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display =getWindowManager().getDefaultDisplay();

        Point size = new Point();

        display.getSize(size);

        if (size.x != 0 && size.y != 0) {
            snakeGameLoop = new SnakeGameLoop(this, size);
            setContentView(snakeGameLoop);
        } else {
            // Handle the case where the display size is invalid
            // For example, display.getSize() may return (0, 0) on some devices
            // You could show an error message or finish the activity
            // For demonstration, we'll simply finish the activity
            finish();
        }
    }

    @Override
    protected void onPause() {
        if (snakeGameLoop != null) {
            snakeGameLoop.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (snakeGameLoop != null) {
            snakeGameLoop.onResume();
        }
    }
}