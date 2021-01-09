package com.example.werewolf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class SetPlayerCharacter extends AppCompatActivity {
    private int totalPlayers;
    private int wolf;
    private int villagers;
    private int seer;
    private int witcher;
    private int guardian;
    private int idiot;
    private int hunter;
    private Boolean mode;

    private TextView playerLabel;
    private ImageView cCard;
    private Button next;
    private int playerViewIndex = 1;

    private ArrayList<String> assignedCharacterList;
    private ArrayList<Integer> playerID;

    private final View.OnClickListener setBack = new View.OnClickListener() {
        public void onClick(View view) {
            cCard.setImageResource(R.drawable.card_back);
            cCard.setOnClickListener(seeCard);
        }
    };

    private final View.OnClickListener seeCard = new View.OnClickListener() {
        public void onClick(View view) {
            next.setEnabled(true);
            String c = assignedCharacterList.get(playerViewIndex - 1);
            switch(c) {
                case "villager":
                    cCard.setImageResource(R.drawable.villager);
                    break;
                case "wolf":
                    cCard.setImageResource(R.drawable.wolf);
                    break;
                case "seer":
                    cCard.setImageResource(R.drawable.seer);
                    break;
                case "witcher":
                    cCard.setImageResource(R.drawable.witcher);
                    break;
                case "guardian":
                    cCard.setImageResource(R.drawable.guardian);
                    break;
                case "idiot":
                    cCard.setImageResource(R.drawable.idiot);
                    break;
                case "hunter":
                    cCard.setImageResource(R.drawable.hunter);
            }
            cCard.setOnClickListener(setBack);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_player_character);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            wolf = extras.getInt("wolf");
            villagers = extras.getInt("villagers");
            seer = extras.getInt("seer");
            witcher = extras.getInt("witcher");
            guardian = extras.getInt("guardian");
            idiot = extras.getInt("idiot");
            hunter = extras.getInt("hunter");
            mode = extras.getBoolean("mode");
            totalPlayers = wolf + villagers + seer + witcher + guardian + idiot + hunter;

            playerID = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                playerID.add(i+1);
            }
            ArrayList<String> characterList = new ArrayList<>();
            for (int i = 0; i < wolf; i++) {
                characterList.add("wolf");
            }
            for (int i = 0; i < villagers; i++) {
                characterList.add("villager");
            }
            for (int i = 0; i < seer; i++) {
                characterList.add("seer");
            }
            for (int i = 0; i < witcher; i++) {
                characterList.add("witcher");
            }
            for (int i = 0; i < guardian; i++) {
                characterList.add("guardian");
            }
            for (int i = 0; i < idiot; i++) {
                characterList.add("idiot");
            }
            for (int i = 0; i < hunter; i++) {
                characterList.add("hunter");
            }

            assignedCharacterList = getRandomCharacter(characterList, totalPlayers);

            playerLabel = findViewById(R.id.viewCharacterLabel);
            playerLabel.setText("请1号玩家查看身份");

            cCard = findViewById(R.id.card);

            cCard.setOnClickListener(seeCard);

            next = findViewById(R.id.nextPlayerButton);
            next.setEnabled(false);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayNext();
                }
            });
        }


    }

    private ArrayList<String> getRandomCharacter(ArrayList<String> characterList, int totalCharacters) {
        Random rand = new Random();
        ArrayList<String> newList = new ArrayList<>();
        for (int i = 0; i < totalCharacters; i++) {
            int randomIndex = rand.nextInt(characterList.size());
            newList.add(characterList.get(randomIndex));
            characterList.remove(randomIndex);
        }
        return newList;
    }

    private void displayNext() {
        if (playerViewIndex < totalPlayers) {
            next.setEnabled(false);
            playerViewIndex += 1;
            String s = "请" + Integer.toString(playerViewIndex) + "号玩家查看身份";
            playerLabel.setText(s);
            cCard.setImageResource(R.drawable.card_back);
            cCard.setOnClickListener(seeCard);
        }
        if (playerViewIndex == totalPlayers) {
            cCard.setImageResource(R.drawable.card_back);
            next.setText("开始游戏");
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startGame();
                }
            });
        }
    }

    private void startGame() {
        Intent game = new Intent(SetPlayerCharacter.this, Game.class);

        game.putExtra("id", playerID);
        game.putExtra("characters", assignedCharacterList);
        game.putExtra("finished", false);
        game.putExtra("won", 0);
        game.putExtra("wolf", wolf);
        game.putExtra("villagers", villagers);
        game.putExtra("seer", seer);
        game.putExtra("witcher", witcher);
        game.putExtra("guardian", guardian);
        game.putExtra("idiot", idiot);
        game.putExtra("hunter", hunter);
        game.putExtra("mode", mode);

        startActivity(game);
    }
}