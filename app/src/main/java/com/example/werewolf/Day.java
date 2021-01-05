package com.example.werewolf;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Day extends AppCompatActivity {
    private Integer deathID1 = 0;
    private Integer deathID2 = 0;

    private ArrayList<Integer> playerID;
    private ArrayList<String> assignedCharacterList;
    private Integer numPlayers = 0;
    private ArrayList<Boolean> alive;
    private String currentCharacter = "None";
    private Integer selectedPlayerID = 0;
    private Integer currentSelectedPlayerID = 0;
    private Integer guardedPlayerID = 0;
    private Integer intentKillPlayerID = 0;
//    private Boolean witcherHoldsAntidote = false;
//    private Boolean witcherHoldsPoison = false;
    private Integer antidotePlayerID = 0;
    private Integer poisonPlayerID = 0;
    private TextView actionLabel;
    private TextView actionLabel2;
    private Button confirmButton;
    private ImageView p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12;

    private MediaPlayer mp;

    private MediaPlayer.OnCompletionListener prepareForNext = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.stop();
            mp.reset();
        }
    };

    private View.OnClickListener selectPlayer = new View.OnClickListener() {
        public void onClick(View view) {
            currentSelectedPlayerID = selectedPlayerID;
            selectedPlayerID = Integer.parseInt((String) view.getContentDescription());
            if (currentSelectedPlayerID == selectedPlayerID){
                selectedPlayerID = 0;
                Toast.makeText(Day.this, "你取消了选择", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Day.this, "你选择了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            playerID = (ArrayList<Integer>) getIntent().getSerializableExtra("id");
            assignedCharacterList = (ArrayList<String>) getIntent().getSerializableExtra("characters");
            alive = (ArrayList<Boolean>) getIntent().getSerializableExtra("alive");
            numPlayers = (Integer) extras.get("numPlayers");
            guardedPlayerID = (Integer) extras.get("guardedPlayerID");
            intentKillPlayerID = (Integer) extras.get("intentKillPlayerID");
            antidotePlayerID = (Integer) extras.get("antidotePlayerID");
            poisonPlayerID = (Integer) extras.get("poisonPlayerID");
        }
//        Toast.makeText(Day.this, "guardedPlayerID " + guardedPlayerID, Toast.LENGTH_SHORT).show();
//        Toast.makeText(Day.this, "intentKillPlayerID " + intentKillPlayerID, Toast.LENGTH_SHORT).show();
//        Toast.makeText(Day.this, "antidotePlayerID " + antidotePlayerID, Toast.LENGTH_SHORT).show();
//        Toast.makeText(Day.this, "poisonPlayerID " +poisonPlayerID, Toast.LENGTH_SHORT).show();
//        Toast.makeText(Day.this, "numPlayers " +numPlayers, Toast.LENGTH_SHORT).show();

        init();

        //Day starts...
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(Day.this, R.raw.audio_day_start);
                mp.setOnCompletionListener(prepareForNext);
                mp.start();
                judgeDeath();
            }
        }, 3000);
    }

    private void init() {
        actionLabel = findViewById(R.id.actionLabel1);
        actionLabel.setText("天亮了");
        actionLabel2 = findViewById(R.id.actionLabel2);
        confirmButton = findViewById(R.id.confirmButton1);
        confirmButton.setEnabled(false);

        p1 = findViewById(R.id.p1Card1);
        p2 = findViewById(R.id.p1Card2);
        p3 = findViewById(R.id.p1Card3);
        p4 = findViewById(R.id.p1Card4);
        p5 = findViewById(R.id.p1Card5);
        p6 = findViewById(R.id.p1Card6);
        p7 = findViewById(R.id.p1Card7);
        p8 = findViewById(R.id.p1Card8);
        p9 = findViewById(R.id.p1Card9);
        p10 = findViewById(R.id.p1Card10);
        p11 = findViewById(R.id.p1Card11);
        p12 = findViewById(R.id.p1Card12);


        for (int i = 0; i < 12; i++){
            if (assignedCharacterList.get(i).equals("None")){
                makeTransparent(i);
            } else {
                if (!alive.get(i)) {
                    updateDeadCard(i);
                }
            }
        }

        //this is only for adding onClickListeners
        for (int i = 1; i < 13; i++) {
            switch (i) {
                case 1:
                    p1.setOnClickListener(selectPlayer);
                    break;
                case 2:
                    p2.setOnClickListener(selectPlayer);
                    break;
                case 3:
                    p3.setOnClickListener(selectPlayer);
                    break;
                case 4:
                    p4.setOnClickListener(selectPlayer);
                    break;
                case 5:
                    if (i <= numPlayers) {
                        p5.setOnClickListener(selectPlayer);
                    }
                    break;
                case 6:
                    if (i <= numPlayers) {
                        p6.setOnClickListener(selectPlayer);
                    }
                    break;
                case 7:
                    if (i <= numPlayers) {
                        p7.setOnClickListener(selectPlayer);
                    }
                    break;
                case 8:
                    if (i <= numPlayers) {
                        p8.setOnClickListener(selectPlayer);
                    }
                    break;
                case 9:
                    if (i <= numPlayers) {
                        p9.setOnClickListener(selectPlayer);
                    }
                    break;
                case 10:
                    if (i <= numPlayers) {
                        p10.setOnClickListener(selectPlayer);
                    }
                    break;
                case 11:
                    if (i <= numPlayers) {
                        p11.setOnClickListener(selectPlayer);
                    }
                    break;
                case 12:
                    if (i <= numPlayers) {
                        p12.setOnClickListener(selectPlayer);
                    }
                    break;
            }
        }
    }

    private void makeTransparent(int i) {
        switch (i) {
            case 4:
                p5.setImageResource(R.drawable.transparent);
                break;
            case 5:
                p6.setImageResource(R.drawable.transparent);
                break;
            case 6:
                p7.setImageResource(R.drawable.transparent);
                break;
            case 7:
                p8.setImageResource(R.drawable.transparent);
                break;
            case 8:
                p9.setImageResource(R.drawable.transparent);
                break;
            case 9:
                p10.setImageResource(R.drawable.transparent);
                break;
            case 10:
                p11.setImageResource(R.drawable.transparent);
                break;
            case 11:
                p12.setImageResource(R.drawable.transparent);
                break;
        }
    }

    private void updateDeadCard(int i) {
        switch (i) {
            case 0:
                p1.setImageResource(R.drawable.card_back_dead);
                break;
            case 1:
                p2.setImageResource(R.drawable.card_back_dead);
                break;
            case 2:
                p3.setImageResource(R.drawable.card_back_dead);
                break;
            case 3:
                p4.setImageResource(R.drawable.card_back_dead);
                break;
            case 4:
                p5.setImageResource(R.drawable.card_back_dead);
                break;
            case 5:
                p6.setImageResource(R.drawable.card_back_dead);
                break;
            case 6:
                p7.setImageResource(R.drawable.card_back_dead);
                break;
            case 7:
                p8.setImageResource(R.drawable.card_back_dead);
                break;
            case 8:
                p9.setImageResource(R.drawable.card_back_dead);
                break;
            case 9:
                p10.setImageResource(R.drawable.card_back_dead);
                break;
            case 10:
                p11.setImageResource(R.drawable.card_back_dead);
                break;
            case 11:
                p12.setImageResource(R.drawable.card_back_dead);
                break;
        }
    }

    private void judgeDeath() {
        //        Judge death(s)
        if (intentKillPlayerID != 0) {
            if (antidotePlayerID.equals(intentKillPlayerID)){
                if (guardedPlayerID.equals(intentKillPlayerID)) {
                    deathID1 = intentKillPlayerID;
                    alive.set(deathID1 - 1 , false);
                    updateDeadCard(deathID1 - 1);
                }
            } else {
                if (!guardedPlayerID.equals(intentKillPlayerID)) {
                    deathID1 = intentKillPlayerID;
                    alive.set(deathID1 - 1 , false);
                    updateDeadCard(deathID1 - 1);
                }
            }
        }
        if (poisonPlayerID != 0) {
            deathID2 = poisonPlayerID;
            alive.set(deathID2 - 1 , false);
            updateDeadCard(deathID2 - 1);
            int firstID = deathID1;
            int secondID = deathID2;
            int tempID = 0;
            if (firstID > secondID) {
                tempID = firstID;
                firstID = secondID;
                secondID = tempID;
            }
            Toast.makeText(Day.this, "昨天晚上死亡的是" + firstID + "号玩家和" + secondID + "号玩家", Toast.LENGTH_SHORT).show();
            actionLabel.setText("昨天晚上死亡的是");
            actionLabel2.setText(firstID + "号玩家和" + secondID + "号玩家");
        } else {
            if (deathID1 == 0) {
                Toast.makeText(Day.this, "昨天晚上是平安夜", Toast.LENGTH_SHORT).show();
                actionLabel.setText("昨天晚上是平安夜");
            } else {
                Toast.makeText(Day.this, "昨天晚上死亡的是" + deathID1 + "号玩家", Toast.LENGTH_SHORT).show();
                actionLabel.setText("昨天晚上死亡的是");
                actionLabel2.setText(deathID1 + "号玩家");
            }
        }
    }
}