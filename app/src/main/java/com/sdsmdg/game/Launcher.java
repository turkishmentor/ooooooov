package com.sdsmdg.game;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.game.Bluetooth.Bluetooth;
import com.sdsmdg.game.GameWorld.SinglePlayer;
import com.sdsmdg.game.LeaderBoard.LeaderBoard;
import com.sdsmdg.game.LeaderBoard.LocalDB.DBHandler;

public class Launcher extends AppCompatActivity implements View.OnClickListener {

    public static long startTime;
    public static int check;
    public static int winner = 1;
    public static int height, width;
    public String TAG = "com.sdsmdg.game";
    Button sP, mP;
    ImageView left, right;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Launcher.check = 0;
        left = (ImageView) findViewById(R.id.left_image);
        right = (ImageView) findViewById(R.id.right_image);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sP = (Button) findViewById(R.id.singlePlayerButton);
        mP = (Button) findViewById(R.id.multiPlayerButton);
        sP.setOnClickListener(this);
        mP.setOnClickListener(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = (displaymetrics.heightPixels);
        width = displaymetrics.widthPixels;
        dbHandler = new DBHandler(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singlePlayerButton: {
                Intent i = new Intent(getApplicationContext(), SinglePlayer.class);
                Log.i(TAG, "SP Button clicked");
                startActivity(i);
                break;
            }
            case R.id.multiPlayerButton: {
                Intent i = new Intent(getApplicationContext(), Bluetooth.class);
                Log.i(TAG, "MP Button clicked");
                startActivity(i);
                break;
            }
        }
    }

    public void highScoreClicked(View view) {

        if (!dbHandler.checkDatabase()) {
            Toast.makeText(Launcher.this, "Your High Score is : " + dbHandler.getPastHighScore(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Launcher.this, "You didn't played yet !", Toast.LENGTH_LONG).show();
        }
    }

    public void infoClicked(View view) {
        checkConnection();
    }

    private void checkConnection() {
        if (isNetworkAvailable()) {
            Intent i = new Intent(getApplicationContext(), LeaderBoard.class);
            startActivity(i);
        } else {
            Toast.makeText(this, "Check your Internet Connection !", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        left.setAnimation(animation);
        right.setAnimation(animation);
        Log.i(TAG, "value of check is : " + check);
        dialog(check);
    }

    @Override
    protected void onPause() {
        super.onPause();
        check--;
    }

    public void dialog(int param) {
        if (param == 1) {
            long finalTime = (System.currentTimeMillis()) / 1000;
            long score = finalTime - startTime;
            String result = "Your score is " + String.valueOf(score);

            final Dialog d = new Dialog(Launcher.this);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.game_over_dialog);
            d.show();
            d.setCancelable(true);

            dbHandler.updateDatabase((int) score, this);
            Button btn_yes = (Button) d.findViewById(R.id.btn_yes);
            Button btn_no = (Button) d.findViewById(R.id.btn_no);

            TextView result_textView = (TextView) d.findViewById(R.id.result_textView);

            result_textView.setText(result);

            btn_yes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    d.dismiss();

                }
            });
            btn_no.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    d.dismiss();
                    System.exit(0);
                }
            });
        }

    }
}
