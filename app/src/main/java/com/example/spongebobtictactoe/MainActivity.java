package com.example.spongebobtictactoe;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("spongebobtictactoe");
    }

    private native void updateLED(int length);
    private native void setLCD(String line1, String line2);

    private boolean gameActive = true;
    private int activePlayer = 0;
    private int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    private int[][] winPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };
    private int counter = 0;
    private Handler handler = new Handler();
    private Runnable timerRunnable;
    private int remainingTime = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLCD("Spongebob's Turn", "");
        startTimer();
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (remainingTime > 0 && gameActive) {
                    remainingTime -= 2;
                    updateLED(remainingTime / 2);
                    handler.postDelayed(this, 2000);
                } else {
                    switchPlayer();
                }
            }
        };
        handler.post(timerRunnable);
    }

    private void resetTimer() {
        handler.removeCallbacks(timerRunnable);
        remainingTime = 16;
        updateLED(8);
        if (gameActive) {
            startTimer();
        }
    }

    private void switchPlayer() {
        gameActive = !gameEnded();
        if (!gameActive) {
            handler.removeCallbacks(timerRunnable);
        }
        if (activePlayer == 0) {
            activePlayer = 1;
            setLCD("Patrick's Turn", "");
        } else {
            activePlayer = 0;
            setLCD("Spongebob's Turn", "");
        }
        resetTimer();
    }

    public void playerTap(View view) {
        if (!gameActive) {
            return;
        }

        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());

        if (gameState[tappedImage] == 2) {
            counter++;
            gameState[tappedImage] = activePlayer;
            img.setTranslationY(-1000f);
            if (activePlayer == 0) {
                img.setImageResource(R.drawable.x);
                activePlayer = 1;
                setLCD("Patrick's Turn", "");
            } else {
                img.setImageResource(R.drawable.o);
                activePlayer = 0;
                setLCD("Spongebob's Turn", "");
            }
            img.animate().translationYBy(1000f).setDuration(300);
            resetTimer();
        }

        if (gameEnded()) {
            displayGameResult();
        }
    }

    private boolean gameEnded() {
        if (counter > 4) {
            for (int[] winPosition : winPositions) {
                if (gameState[winPosition[0]] == gameState[winPosition[1]] &&
                        gameState[winPosition[1]] == gameState[winPosition[2]] &&
                        gameState[winPosition[0]] != 2) {
                    return true;
                }
            }
            if (counter == 9) {
                return true;
            }
        }
        return false;
    }

    private void displayGameResult() {
        gameActive = false;
        handler.removeCallbacks(timerRunnable);
        if (counter == 9) {
            setLCD("Match Draw!", "");
        } else if (activePlayer == 0) {
            setLCD("Patrick won!", "");
        } else {
            setLCD("Spongebob won!", "");
        }
        Button playAgainButton = findViewById(R.id.playAgainButton);
        playAgainButton.setVisibility(View.VISIBLE);
    }

    public void gameReset(View view) {
        gameActive = true;
        activePlayer = 0;
        counter = 0;
        Arrays.fill(gameState, 2);
        ((ImageView) findViewById(R.id.imageView0)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView1)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView2)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView3)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView4)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView5)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView6)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView7)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView8)).setImageResource(0);
        setLCD("Spongebob's Turn", "");
        Button playAgainButton = findViewById(R.id.playAgainButton);
        playAgainButton.setVisibility(View.GONE);
        resetTimer();
    }

    public void playAgain(View view) {
        gameReset(view);
    }

    public void exitGame(View view) {
        handler.removeCallbacks(timerRunnable);
        finish();
    }
}
