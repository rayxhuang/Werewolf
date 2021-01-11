package com.example.werewolf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

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

    private int seerID = -1;
    private int witcherID = -1;
    private int guardianID = -1;
    private int idiotID = -1;
    private int hunterID = -1;

    private TextView playerLabel;
    private ImageView cCard;
    private Button next;
    private int playerViewIndex = 1;

    private ArrayList<Character> characterArray = new ArrayList<>();

    private final View.OnClickListener setBack = new View.OnClickListener() {
        public void onClick(View view) {
            cCard.setImageResource(R.drawable.card_back);
            cCard.setOnClickListener(seeCard);
        }
    };

    private final View.OnClickListener seeCard = new View.OnClickListener() {
        public void onClick(View view) {
            next.setEnabled(true);
            String c = characterArray.get(playerViewIndex - 1).getAssignedCharacter();
            switch(c) {
                case "村民":
                    cCard.setImageResource(R.drawable.villager);
                    break;
                case "狼人":
                    cCard.setImageResource(R.drawable.wolf);
                    break;
                case "预言家":
                    cCard.setImageResource(R.drawable.seer);
                    break;
                case "女巫":
                    cCard.setImageResource(R.drawable.witcher);
                    break;
                case "守卫":
                    cCard.setImageResource(R.drawable.guardian);
                    break;
                case "白痴":
                    cCard.setImageResource(R.drawable.idiot);
                    break;
                case "猎人":
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

            for (int i = 0; i < wolf; i++) { characterArray.add(new Character("狼人","狼",true)); }
            for (int i = 0; i < villagers; i++) { characterArray.add(new Character("村民","人",true)); }
            if (seer > 0) { characterArray.add(new Character("预言家","神",true)); }
            if (witcher > 0) {
                Character A = new Character("女巫","神",true);
                A.setHoldingAntidote(true);
                A.setHoldingPoison(true);
                characterArray.add(A);
            }
            if (guardian > 0) { characterArray.add(new Character("守卫","神",true)); }
            if (idiot > 0) { characterArray.add(new Character("白痴","神",true)); }
            if (hunter > 0) { characterArray.add(new Character("猎人","神",true)); }
            totalPlayers = characterArray.size();
            Collections.shuffle(characterArray);

            for (int i = 0; i < totalPlayers; i++) {
                Character A = characterArray.get(i);
                A.setID(i + 1);
                if (A.getAssignedCharacter().equals("预言家")) { seerID = i + 1;  }
                if (A.getAssignedCharacter().equals("女巫")) { witcherID = i + 1; }
                if (A.getAssignedCharacter().equals("守卫")) { guardianID = i + 1; }
                if (A.getAssignedCharacter().equals("白痴")) { idiotID = i + 1; }
                if (A.getAssignedCharacter().equals("猎人")) { hunterID = i + 1; }
            }

            for (int i = totalPlayers; i < 12; i++) {
                characterArray.add(new Character(i, null,null,false));
            }

            playerLabel = findViewById(R.id.viewCharacterLabel);
            playerLabel.setText("请1号玩家查看身份");

            cCard = findViewById(R.id.card);

            cCard.setOnClickListener(seeCard);

            next = findViewById(R.id.nextPlayerButton);
            next.setEnabled(false);
            next.setOnClickListener(view -> displayNext());
        }
    }

    private void displayNext() {
        if (playerViewIndex < totalPlayers) {
            next.setEnabled(false);
            playerViewIndex += 1;
            String s = "请" + playerViewIndex + "号玩家查看身份";
            playerLabel.setText(s);
            cCard.setImageResource(R.drawable.card_back);
            cCard.setOnClickListener(seeCard);
        }
        if (playerViewIndex == totalPlayers) {
            cCard.setImageResource(R.drawable.card_back);
            next.setText("开始游戏");
            next.setOnClickListener(view -> startGame());
        }
    }

    private void startGame() {
        Intent game = new Intent(SetPlayerCharacter.this, Game.class);

        game.putExtra("characters", characterArray);
        game.putExtra("mode", mode);
        game.putExtra("finished", false);
        game.putExtra("won", 0);
        game.putExtra("numPlayers", totalPlayers);

        game.putExtra("wolf", wolf);
        game.putExtra("villagers", villagers);
        game.putExtra("seer", seer);
        game.putExtra("witcher", witcher);
        game.putExtra("guardian", guardian);
        game.putExtra("idiot", idiot);
        game.putExtra("hunter", hunter);

        game.putExtra("seerID", seerID);
        game.putExtra("witcherID", witcherID);
        game.putExtra("guardianID", guardianID);
        game.putExtra("idiotID", idiotID);
        game.putExtra("hunterID", hunterID);

        startActivity(game);
    }
}