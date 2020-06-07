package com.example.coinman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.example.coinman.CoinMan;

public class MainActivity extends AppCompatActivity {

    private Button newGameButton;
    private Button highScoresButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newGameButton=findViewById(R.id.newGameButton);
        highScoresButton=findViewById(R.id.highScoresButton);
        exitButton=findViewById(R.id.exitButton);
    }

    public void exit(View view){
        finish();
    }

    public void newGame(View view){
    }
}
