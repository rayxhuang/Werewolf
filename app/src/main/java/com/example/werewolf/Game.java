package com.example.werewolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private Boolean finished;
    private Integer won;

    private TextView text1;
    private Button bt1, bt2;

    private int wolf;
    private int villagers;
    private int seer;
    private int witcher;
    private int guardian;
    private int idiot;

    private View.OnClickListener startAgain = new View.OnClickListener() {
        public void onClick(View view) {
            Intent assignCharacter = new Intent(Game.this, SetPlayerCharacter.class);
            assignCharacter.putExtra("wolf", wolf);
            assignCharacter.putExtra("villagers", villagers);
            assignCharacter.putExtra("seer", seer);
            assignCharacter.putExtra("witcher", witcher);
            assignCharacter.putExtra("guardian", guardian);
            assignCharacter.putExtra("idiot", idiot);
            startActivity(assignCharacter);
        }
    };

    private View.OnClickListener changeSettings = new View.OnClickListener() {
        public void onClick(View view) {
            Intent changeSettings = new Intent(Game.this, Setting.class);
            changeSettings.putExtra("wolf", wolf);
            changeSettings.putExtra("villagers", villagers);
            changeSettings.putExtra("seer", seer);
            changeSettings.putExtra("witcher", witcher);
            changeSettings.putExtra("guardian", guardian);
            changeSettings.putExtra("idiot", idiot);
            startActivity(changeSettings);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            text1 = findViewById(R.id.textView);
            bt1 = findViewById(R.id.button);
            bt2 = findViewById(R.id.button2);
            text1.setVisibility(View.GONE);
            bt1.setVisibility(View.GONE);
            bt1.setEnabled(false);
            bt2.setVisibility(View.GONE);
            bt2.setEnabled(false);

            playerID = (ArrayList<Integer>) getIntent().getSerializableExtra("id");
            assignedCharacterList = (ArrayList<String>) getIntent().getSerializableExtra("characters");
            numPlayer = assignedCharacterList.size();
            alive = new ArrayList<Boolean>();
            finished = (Boolean) extras.get("finished");
            won = (Integer) extras.get("won");
            wolf = extras.getInt("wolf");
            villagers = extras.getInt("villagers");
            seer = extras.getInt("seer");
            witcher = extras.getInt("witcher");
            guardian = extras.getInt("guardian");
            idiot = extras.getInt("idiot");

            if (finished) {
                text1.setVisibility(View.VISIBLE);
                text1.setVisibility(View.VISIBLE);
                bt1.setVisibility(View.VISIBLE);
                bt2.setVisibility(View.VISIBLE);
                bt1.setEnabled(true);
                bt2.setEnabled(true);
                bt1.setText("再来一局");
                bt1.setOnClickListener(startAgain);
                bt2.setText("更改配置");
                bt2.setOnClickListener(changeSettings);
                if (won == 1) {
                    text1.setText("游戏结束, 好人获胜!");
                } else {
                    text1.setText("游戏结束, 狼人获胜!");
                }
            } else {
                for (int i = 0; i < assignedCharacterList.size(); i++){
                    alive.add(true);
                }
                int extraPlayers = 12 - assignedCharacterList.size();
                for (int i = 0; i < extraPlayers; i++){
                    alive.add(false);
                    assignedCharacterList.add("None");
                }

                Intent night = new Intent(Game.this, Night.class);
                night.putExtra("id", playerID);
                night.putExtra("characters", assignedCharacterList);
                night.putExtra("alive", alive);
                night.putExtra("numPlayers", numPlayer);
                night.putExtra("guardedPlayerID", 0);
                night.putExtra("intentKillPlayerID", 0);
                night.putExtra("antidotePlayerID", 0);
                night.putExtra("poisonPlayerID", 0);
                night.putExtra("witcherHoldsAntidote", true);
                night.putExtra("witcherHoldsPoison", true);
                night.putExtra("wolf", wolf);
                night.putExtra("villagers", villagers);
                night.putExtra("seer", seer);
                night.putExtra("witcher", witcher);
                night.putExtra("guardian", guardian);
                night.putExtra("idiot", idiot);
                startActivity(night);
            }
        }
    }
}