package com.example.snakegame;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

@SuppressLint("ViewConstructor")
public class SnakeGameLoop extends SurfaceView implements Runnable{

    private Thread thread;
    private volatile Boolean isThreadRunning;
    private int score;

    private Bitmap backgroundImage;
    private final SurfaceHolder surfaceHolder;
    private  long nextFrameTime;

    private Boolean paused = true;
    private final Paint paint = new Paint();
    private final GestureDetector gestureDetector;
    private float x1 , x2 , y1 , y2;
    private float abs_x , abs_y;
    static final int MIN_DISTANCE = 100;

    private final Apple apple;
    private final Snake snake;
    SnakeGameLoop(Context context , Point screenSize)
    {
        super(context);
        int NUM_BLOCK_WIDE = 40;
        int blockSize = screenSize.x / NUM_BLOCK_WIDE;
        int NUM_BLOCK_HIGH = screenSize.y / blockSize;

        backgroundImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        backgroundImage = Bitmap.createScaledBitmap(backgroundImage , screenSize.x , screenSize.y , false);

        apple = new Apple(context , NUM_BLOCK_HIGH, NUM_BLOCK_WIDE, blockSize);
        snake = new Snake(context , new Point(NUM_BLOCK_HIGH, NUM_BLOCK_WIDE) , blockSize);

        gestureDetector = new GestureDetector(getContext() , getGestureListner()){

        };

        surfaceHolder = getHolder();
        startNewGame();

    }

    void startNewGame()
    {
        score = 0;
        // Apple position...
        apple.setApplePosition();
        // move Snake...
        snake.moveSnake();
        nextFrameTime = System.currentTimeMillis();
    }


    @Override
    public void run(){

        while(isThreadRunning)
        {
            if(updateRequired())
            {


                if (!paused) {
                    update();
                }
                draw();
            }

        }
    }
    private Boolean  updateRequired()
    {
        if(System.currentTimeMillis() > nextFrameTime)
        {
            nextFrameTime += 120;
            return true;
        }
        return false;
    }
    private void update()
    {
        snake.moveSnake();

        if(snake.isDead())
        {

            paused = true;

        }

        if(snake.haveDinner(apple.getPositionOfApple()))
        {
            score++;
            apple.setApplePosition();
        }

    }
    private void draw()
    {
        if(surfaceHolder.getSurface().isValid())
        {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(backgroundImage,  0,  0 ,  paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(120);
            canvas.drawText(String.valueOf(score), 1 , 120 , paint);

            if(!paused)
            {
                //Draw snake
                //Draw Apple
                apple.drawApple(canvas, paint);
                snake.drawSnake(canvas, paint);
            }else
            {
                paint.setTextSize(250);
                paint.setColor(Color.BLACK);
                canvas.drawText("Tap to Play....." ,  250,  250, paint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        return true;
    }
    private  GestureDetector.OnGestureListener getGestureListner()
    {
        return new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(@NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(@NonNull MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
                if (paused) {
                    paused = false;
                    score = 0;
                    snake.reset();
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent downEvent, @NonNull MotionEvent upEvent, float v, float v1) {
                x1 = downEvent.getX();
                y1 = downEvent.getY();
                x2 = upEvent.getX();
                y2 = upEvent.getY();

                abs_x = Math.abs(x2 - x1);
                abs_y = Math.abs(y2 - y1);
                if (abs_y > MIN_DISTANCE || abs_x > MIN_DISTANCE)
                {
                    if(abs_y > abs_x)
                    {
                        //Vertical swipe.
                        if (snake.getMovingDirection() == Heading.RIGHT || snake.getMovingDirection() == Heading.LEFT)
                        {
                            if(y1 > y2)
                            {
                                snake.setMovingDirection(Heading.UP);
                            }else
                            {
                                snake.setMovingDirection(Heading.DOWN);
                            }
                        }
                    }else if(abs_x > MIN_DISTANCE)
                    {
                        //Horizontal swipe.
                        if(snake.getMovingDirection() == Heading.UP || snake.getMovingDirection() == Heading.DOWN)
                        {
                            if(x1 > x2)
                            {
                                snake.setMovingDirection(Heading.LEFT);
                            }else
                            {
                                snake.setMovingDirection(Heading.RIGHT);
                            }
                        }
                    }
                }

                return false;
            }
        };



    }

    public void onResume()
    {
        isThreadRunning = true;
        thread = new Thread( this);
        thread.start();
    }
    public void onPause()
    {
        isThreadRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
