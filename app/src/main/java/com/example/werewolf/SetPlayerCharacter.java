package com.example.werewolf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SetPlayerCharacter extends AppCompatActivity {
    private int totalPlayers;
    private int wolf;
    private int villagers;
    private int seer;
    private int witcher;
    private int guardian;
    private int idiot;

    private TextView playerLabel;
    private ImageView cCard;
    private Button next;
    private int playerViewIndex = 1;
    private View.OnClickListener setBack = new View.OnClickListener() {
        public void onClick(View view) {
            cCard.setImageResource(R.drawable.card_back);
            cCard.setOnClickListener(seeCard);
        }
    };

    private View.OnClickListener seeCard = new View.OnClickListener() {
        public void onClick(View view) {
            String c = assignedCharacterList.get(playerViewIndex - 1);
            switch(c) {
                case "villager":
                    cCard.setImageResource(R.drawable.villager);
                    cCard.setOnClickListener(setBack);
                    break;
                case "wolf":
                    cCard.setImageResource(R.drawable.wolf);
                    cCard.setOnClickListener(setBack);
                    break;
                case "seer":
                    cCard.setImageResource(R.drawable.seer);
                    cCard.setOnClickListener(setBack);
                    break;
                case "witcher":
                    cCard.setImageResource(R.drawable.witcher);
                    cCard.setOnClickListener(setBack);
                    break;
                case "guardian":
                    cCard.setImageResource(R.drawable.guardian);
                    cCard.setOnClickListener(setBack);
                    break;
                case "idiot":
                    cCard.setImageResource(R.drawable.idiot);
                    cCard.setOnClickListener(setBack);
                    break;
            }
        }
    };

    private ArrayList<String> assignedCharacterList;
    private ArrayList<Integer> playerID;

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
            totalPlayers = wolf + villagers + seer + witcher + guardian + idiot;

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

            assignedCharacterList = getRandomCharacter(characterList, totalPlayers);
            System.out.println(playerID);
            System.out.println(assignedCharacterList);

            playerLabel = findViewById(R.id.viewCharacterLabel);
            playerLabel.setText("请1号玩家查看身份");

            cCard = findViewById(R.id.card);

            cCard.setOnClickListener(seeCard);

            next = findViewById(R.id.nextPlayerButton);

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

        startActivity(game);
    }
}