package com.example.werewolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    private int wolf;
    private int villagers;
    private int seer;
    private int witcher;
    private int guardian;
    private int idiot;

    private Integer deathID1 = 0;
    private Integer deathID2 = 0;

    private ArrayList<Integer> playerID;
    private ArrayList<String> assignedCharacterList;
    private Integer numPlayers;
    private ArrayList<Boolean> alive;
    private Integer selectedPlayerID = 0;
    private Integer currentSelectedPlayerID = 0;
    private Integer guardedPlayerID;
    private Integer intentKillPlayerID;
    private Boolean witcherHoldsAntidote;
    private Boolean witcherHoldsPoison;
    private Integer antidotePlayerID;
    private Integer poisonPlayerID;
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
                confirmButton.setText("跳过");
            } else {
                Toast.makeText(Day.this, "你选择了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
                confirmButton.setText("处决");
            }
        }
    };

    private View.OnClickListener votePlayer = new View.OnClickListener() {
        public void onClick(View view) {
            confirmButton.setEnabled(false);
            if (selectedPlayerID == 0) {
                Toast.makeText(Day.this, "村民没有处决任何人", Toast.LENGTH_SHORT).show();
                actionLabel.setText("村民没有处决任何人");
                actionLabel2.setText("");
            } else {
                //需要加上第二次票出白痴的情况
                if (assignedCharacterList.contains("idiot")) {
                    Integer idiotID = assignedCharacterList.indexOf("idiot") + 1;
                    if (selectedPlayerID == idiotID) {
                        Toast.makeText(Day.this, "村民处决了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
                        actionLabel.setText("村民处决了" + selectedPlayerID + "号玩家");
                        actionLabel2.setText(selectedPlayerID + "号玩家的身份是白痴");
                        updateIdiotCard(selectedPlayerID - 1);
                    } else {
                        alive.set(selectedPlayerID - 1, false);
                        Toast.makeText(Day.this, "村民处决了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
                        actionLabel.setText("村民处决了");
                        actionLabel2.setText(selectedPlayerID + "号玩家");
                        updateDeadCard(selectedPlayerID - 1);
                    }
                }
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Integer won = judgeGame(false);
                    if (won != 0) {
                        Intent game = new Intent(Day.this, Game.class);

                        game.putExtra("id", playerID);
                        game.putExtra("characters", assignedCharacterList);
                        game.putExtra("finished", true);
                        game.putExtra("won", won);
                        game.putExtra("wolf", wolf);
                        game.putExtra("villagers", villagers);
                        game.putExtra("seer", seer);
                        game.putExtra("witcher", witcher);
                        game.putExtra("guardian", guardian);
                        game.putExtra("idiot", idiot);

                        startActivity(game);
                    } else {
                        //Goes into Night again
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                actionLabel.setText("");
                                actionLabel2.setText("");
                                mp.stop();
                                mp.reset();
                                mp.release();
                                Toast.makeText(Day.this, "天黑了...", Toast.LENGTH_SHORT).show();
                                Intent night = new Intent(Day.this, Night.class);
                                night.putExtra("id", playerID);
                                night.putExtra("characters", assignedCharacterList);
                                night.putExtra("alive",alive);
                                night.putExtra("numPlayers", numPlayers);
                                night.putExtra("guardedPlayerID", guardedPlayerID);
                                night.putExtra("intentKillPlayerID", 0);
                                night.putExtra("antidotePlayerID", antidotePlayerID);
                                night.putExtra("poisonPlayerID", poisonPlayerID);
                                night.putExtra("witcherHoldsAntidote", witcherHoldsAntidote);
                                night.putExtra("witcherHoldsPoison", witcherHoldsPoison);
                                night.putExtra("wolf", wolf);
                                night.putExtra("villagers", villagers);
                                night.putExtra("seer", seer);
                                night.putExtra("witcher", witcher);
                                night.putExtra("guardian", guardian);
                                night.putExtra("idiot", idiot);
                                startActivity(night);
                            }
                        }, 3000);
                    }
                }
            }, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            wolf = extras.getInt("wolf");
            villagers = extras.getInt("villagers");
            seer = extras.getInt("seer");
            witcher = extras.getInt("witcher");
            guardian = extras.getInt("guardian");
            idiot = extras.getInt("idiot");
            playerID = (ArrayList<Integer>) getIntent().getSerializableExtra("id");
            assignedCharacterList = (ArrayList<String>) getIntent().getSerializableExtra("characters");
            alive = (ArrayList<Boolean>) getIntent().getSerializableExtra("alive");
            numPlayers = (Integer) extras.get("numPlayers");
            guardedPlayerID = (Integer) extras.get("guardedPlayerID");
            intentKillPlayerID = (Integer) extras.get("intentKillPlayerID");
            antidotePlayerID = (Integer) extras.get("antidotePlayerID");
            poisonPlayerID = (Integer) extras.get("poisonPlayerID");
            witcherHoldsAntidote = (Boolean) extras.get("witcherHoldsAntidote");
            witcherHoldsPoison = (Boolean) extras.get("witcherHoldsPoison");
        }

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

        //Try to decide victory
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Integer won = judgeGame(false);
                if (won != 0) {
                    Intent game = new Intent(Day.this, Game.class);

                    game.putExtra("id", playerID);
                    game.putExtra("characters", assignedCharacterList);
                    game.putExtra("finished", true);
                    game.putExtra("won", won);
                    game.putExtra("wolf", wolf);
                    game.putExtra("villagers", villagers);
                    game.putExtra("seer", seer);
                    game.putExtra("witcher", witcher);
                    game.putExtra("guardian", guardian);
                    game.putExtra("idiot", idiot);

                    startActivity(game);
                } else {
                    //Vote player
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            actionLabel.setText("村民可以投票处决玩家");
                            actionLabel2.setText("不处决玩家请跳过");
                        }
                    }, 5000);
                }
            }
        }, 5000);
    }

    private void init() {
        actionLabel = findViewById(R.id.actionLabel1);
        actionLabel.setText("天亮了");
        actionLabel2 = findViewById(R.id.actionLabel2);
        confirmButton = findViewById(R.id.confirmButton1);
        confirmButton.setEnabled(true);
        confirmButton.setText("跳过");
        confirmButton.setOnClickListener(votePlayer);

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
                p1.setClickable(false);
                break;
            case 1:
                p2.setImageResource(R.drawable.card_back_dead);
                p2.setClickable(false);
                break;
            case 2:
                p3.setImageResource(R.drawable.card_back_dead);
                p3.setClickable(false);
                break;
            case 3:
                p4.setImageResource(R.drawable.card_back_dead);
                p4.setClickable(false);
                break;
            case 4:
                p5.setImageResource(R.drawable.card_back_dead);
                p5.setClickable(false);
                break;
            case 5:
                p6.setImageResource(R.drawable.card_back_dead);
                p6.setClickable(false);
                break;
            case 6:
                p7.setImageResource(R.drawable.card_back_dead);
                p7.setClickable(false);
                break;
            case 7:
                p8.setImageResource(R.drawable.card_back_dead);
                p8.setClickable(false);
                break;
            case 8:
                p9.setImageResource(R.drawable.card_back_dead);
                p9.setClickable(false);
                break;
            case 9:
                p10.setImageResource(R.drawable.card_back_dead);
                p10.setClickable(false);
                break;
            case 10:
                p11.setImageResource(R.drawable.card_back_dead);
                p11.setClickable(false);
                break;
            case 11:
                p12.setImageResource(R.drawable.card_back_dead);
                p12.setClickable(false);
                break;
        }
    }

    private void updateIdiotCard(int i) {
        switch (i) {
            case 0:
                p1.setImageResource(R.drawable.idiot);
                break;
            case 1:
                p2.setImageResource(R.drawable.idiot);
                break;
            case 2:
                p3.setImageResource(R.drawable.idiot);
                break;
            case 3:
                p4.setImageResource(R.drawable.idiot);
                break;
            case 4:
                p5.setImageResource(R.drawable.idiot);
                break;
            case 5:
                p6.setImageResource(R.drawable.idiot);
                break;
            case 6:
                p7.setImageResource(R.drawable.idiot);
                break;
            case 7:
                p8.setImageResource(R.drawable.idiot);
                break;
            case 8:
                p9.setImageResource(R.drawable.idiot);
                break;
            case 9:
                p10.setImageResource(R.drawable.idiot);
                break;
            case 10:
                p11.setImageResource(R.drawable.idiot);
                break;
            case 11:
                p12.setImageResource(R.drawable.idiot);
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
                    //Wolf is killing a dead player
                    if (alive.get(intentKillPlayerID - 1) == true) {
                        deathID1 = intentKillPlayerID;
                        alive.set(deathID1 - 1 , false);
                        updateDeadCard(deathID1 - 1);
                    } else {
                        deathID1 = 0;
                    }
                }
            }
        }
        if (poisonPlayerID != 0) {
            deathID2 = poisonPlayerID;
            alive.set(deathID2 - 1 , false);
            updateDeadCard(deathID2 - 1);
            if (deathID1 == 0) {
                Toast.makeText(Day.this, "昨天晚上死亡的是" + deathID2 + "号玩家", Toast.LENGTH_SHORT).show();
                actionLabel.setText("昨天晚上死亡的是");
                actionLabel2.setText(deathID2 + "号玩家");
            } else {
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
            }
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

    private Integer judgeGame(Boolean all) {
        //0 stands for undecided victory
        //1 stands for human victory
        //2 stands for werewolf victory
        Integer totalGod = 0, totalVillager = 0, totalWolf = 0;
        Integer aliveGod = 0, aliveVillager = 0, aliveWolf = 0;
        for (int i = 0; i < numPlayers; i++) {
            switch (assignedCharacterList.get(i)) {
                case "wolf":
                    totalWolf += 1;
                    if (alive.get(i).equals(true)) {aliveWolf += 1;}
                    break;
                case "villager":
                    totalVillager += 1;
                    if (alive.get(i).equals(true)) {aliveVillager += 1;}
                    break;
                default:
                    totalGod += 1;
                    if (alive.get(i).equals(true)) {aliveGod += 1;}
                    break;
            }
        }
        if (all) {
            if (aliveGod + aliveVillager == 0) {return 2;}
            if (aliveWolf == 0) {return 1;}
        } else {
            if (aliveWolf == 0) {return 1;}
//            if (aliveWolf == aliveVillager && aliveWolf == aliveGod) {return 2;}
            if (aliveVillager == 0) {return 2;}
            if (aliveGod == 0) {return 2;}
        }
        //Undecided
        return 0;
    }
}