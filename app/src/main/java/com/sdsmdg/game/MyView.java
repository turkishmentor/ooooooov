package com.sdsmdg.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sdsmdg.game.GameWorld.Ball;
import com.sdsmdg.game.GameWorld.GameWorld;


/**
 * Created by Rahul Yadav on 5/20/2016.
 */
public class MyView extends SurfaceView implements SurfaceHolder.Callback, Ball {

    private final int boardWidth = (GameWorld.width) / 5;
    private final int boardHeight = (GameWorld.height) / 50;
    private final float dT = 0.3f;
    private final Paint paintB1, paintB2,paintBall;
    String TAG = "com.sdsmdg.game";
    private float vBallX, vBallY;
    private RectF rectFB1, rectFB2, rectInvisible;
    private GameWorld.RenderThread renderThread;
    private float vB1X, vB2X;
    private int xBallCenter, yBallCenter;
    private int xB1Center, yB1Center;
    private int xB2Center, yB2Center;


    public MyView(Context context) {
        super(context);
        Log.i(TAG, "Constructor of shape view starts");
        getHolder().addCallback(this);
        renderThread = new GameWorld.RenderThread(getHolder(), this);

        setFocusable(true);
        paintB1 = new Paint();
        paintB1.setColor(0xFF3F51B5);
        paintB1.setAlpha(255);
        paintB1.setStyle(Paint.Style.FILL);
        paintB1.setAntiAlias(true);

        paintB2 = new Paint();
        paintB2.setColor(0xFF3F51B5);
        paintB2.setAlpha(255);
        paintB2.setStyle(Paint.Style.FILL);
        paintB2.setAntiAlias(true);

        paintBall = new Paint();
        paintBall.setColor(0xFFFF4081);
        paintBall.setAlpha(255);
        paintBall.setStyle(Paint.Style.FILL);
        paintBall.setAntiAlias(true);

        rectFB1 = new RectF();
        rectFB2 = new RectF();

        setBoardOneAtCenter(GameWorld.width / 2, GameWorld.height);
        setBoardTwoAtCenter(GameWorld.width / 2, 0);
        initializeBallPosition(GameWorld.width, GameWorld.height);


    }

    public boolean setBoardOneAtCenter(int x, int y) {
        //Default position at the start of the Game
        xB1Center = x;
        yB1Center = (y - boardHeight);

        return true;
    }

    public boolean setBoardTwoAtCenter(int x, int y) {
        //Default position at the start of the Game
        xB2Center = x;
        yB2Center = (y + boardHeight);

        return true;
    }

    @Override
    public boolean initializeBallPosition(int x, int y) {
        xBallCenter = x / 2;
        yBallCenter = y / 2;
        initializeBallVelocity(x, y);
        return true;
    }

    @Override
    public boolean initializeBallVelocity(int x, int y) {
        vBallX = (GameWorld.temp)*x / 25;
        vBallY = (GameWorld.temp)*y / 30;
        return true;
    }


    public boolean update() {
        updateB1Center();
        updateB2Center();
        updateBall();
        return true;
    }

    public boolean updateB1Center() {
        if (Math.abs(GameWorld.aB1X) < 1) {
            vB1X = 0;
        } else {
            if (GameWorld.aB1X < 0) {
                vB1X = (GameWorld.temp)*GameWorld.width / 36;
            } else {
                vB1X = -(GameWorld.temp)*GameWorld.width / 36;
            }
        }

        xB1Center += (int) (vB1X * dT);

        if (xB1Center < boardWidth / 2) {
            xB1Center = boardWidth / 2;
            vB1X = 0;
        }
        if (xB1Center > GameWorld.width - (boardWidth / 2)) {
            xB1Center = (GameWorld.width - (boardWidth / 2));
            vB1X = 0;
        }

        return true;
    }

    public boolean updateB2Center() {
        if (GameWorld.directionB2 == 0) {
            vB2X = 0;
        } else if (GameWorld.directionB2 > 0) {
            vB2X = -(GameWorld.temp)*GameWorld.width / 36;

        } else {
            vB2X = (GameWorld.temp)*GameWorld.width / 36;
        }

        xB2Center += (int) (vB2X * dT);


        if (xB2Center < boardWidth / 2) {
            xB2Center = boardWidth / 2;
            vB2X = 0;
        }
        if (xB2Center > GameWorld.width - (boardWidth / 2)) {
            xB2Center = (GameWorld.width - (boardWidth / 2));
            vB2X = 0;
        }
        return true;
    }

    @Override
    public boolean updateBall() {

        xBallCenter += vBallX * dT;
        yBallCenter += vBallY * dT;

        if (xBallCenter < Ball.radius) {
            xBallCenter = Ball.radius;
            vBallX = -vBallX;
        } else if (xBallCenter > GameWorld.width - Ball.radius) {
            xBallCenter = GameWorld.width - Ball.radius;
            vBallX = -vBallX;
        } else if (yBallCenter < Ball.radius) {
            //P1 wins
            yBallCenter = Ball.radius;
            vBallY = -vBallY;


        } else if (yBallCenter > GameWorld.height - Ball.radius) {
            //P2 wins
           // GameWorld.startDialog(1);
            yBallCenter = GameWorld.height - Ball.radius;
            vBallY = -vBallY;
        }
        return true;
    }

    @Override
    public boolean collide(int x) {
        if (x == 1) {
            yBallCenter = (int) (GameWorld.height - boardHeight - Ball.radius);
            vBallY = -vBallY;
        } else if (x == 2) {
            yBallCenter = Ball.radius + boardHeight;
            vBallY = -vBallY;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(0XFFFFFFFF);
        if (rectFB1 != null) {
            rectFB1.set(xB1Center - (boardWidth / 2), yB1Center + (boardHeight / 2), xB1Center + (boardWidth / 2), yB1Center - (boardHeight / 2));

            canvas.drawRect(rectFB1, paintB1);
        }

        if (rectFB2 != null) {
            rectFB2.set(xB2Center - (boardWidth / 2), yB2Center + (boardHeight / 2), xB2Center + (boardWidth / 2), yB2Center - (boardHeight / 2));

            canvas.drawRect(rectFB2, paintB2);
        }
        if (Ball.rectFBall != null) {
            Ball.rectFBall.set(xBallCenter - Ball.radius, yBallCenter - Ball.radius, xBallCenter + Ball.radius, yBallCenter + Ball.radius);

            canvas.drawOval(Ball.rectFBall, paintBall);
        }
        if (rectFB1 != null) {
            if (rectFBall.intersect(rectFB1)) {
                collide(1);
            } else if (rectFBall.intersect(rectFB2)) {
                collide(2);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        renderThread.setRunning(true);
        renderThread.start();

        Log.i(TAG, "Surface created called");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.i(TAG, "Surface changed called");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        renderThread.setRunning(false);

        Log.i(TAG, "Surface destroyed called");
    }


}