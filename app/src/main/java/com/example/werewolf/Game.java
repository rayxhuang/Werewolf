package com.example.werewolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;

import android.os.Bundle;

import java.util.ArrayList;

public class Game extends AppCompatActivity {
    private MediaPlayer mp;
    private final MediaPlayer.OnCompletionListener complete = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.stop();
            mp.release();
            mp = null;
        }
    };

    private ArrayList<Integer> playerID;
    private ArrayList<String> assignedCharacterList;
    private Integer numPlayer;
    private ArrayList<Boolean> alive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            playerID = (ArrayList<Integer>) getIntent().getSerializableExtra("id");
            assignedCharacterList = (ArrayList<String>) getIntent().getSerializableExtra("characters");
            numPlayer = assignedCharacterList.size();
            alive = new ArrayList<Boolean>();
            for (int i = 0; i < assignedCharacterList.size(); i++){
                alive.add(true);
            }
            int extraPlayers = 12 - assignedCharacterList.size();
            for (int i = 0; i < extraPlayers; i++){
                alive.add(false);
                assignedCharacterList.add("None");
            }
        }

        Intent wolfkill = new Intent(Game.this, Wolf.class);
        wolfkill.putExtra("id", playerID);
        wolfkill.putExtra("characters", assignedCharacterList);
        wolfkill.putExtra("alive",alive);
        wolfkill.putExtra("numPlayer", numPlayer);
        startActivity(wolfkill);

    }
}