package com.example.werewolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Day extends AppCompatActivity {
    private File file;
    private int wolf;
    private int villagers;
    private int seer;
    private int witcher;
    private int guardian;
    private int idiot;
    private int hunter;
    private Boolean mode;

    private Integer deathID1 = 0;
    private Integer deathID2 = 0;

    private ArrayList<Integer> playerID;
    private ArrayList<String> assignedCharacterList;
    private Integer numPlayers;
    private ArrayList<Boolean> alive;
    private Integer selectedPlayerID = 0;
    private Integer currentSelectedPlayerID = 0;
    private String currentCharacter = "None";
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
    private final ArrayList<ImageView> pCollection = new ArrayList<>(12);

    private MediaPlayer mp;

    private final MediaPlayer.OnCompletionListener prepareForNext = mp -> {
        mp.stop();
        mp.reset();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            getExtras(extras);
        }

        init();

        //Day starts...
        new Handler().postDelayed(() -> {
            mp = MediaPlayer.create(Day.this, R.raw.audio_day_start);
            mp.setOnCompletionListener(prepareForNext);
            mp.start();
        }, 1000);
    }

    private void init() {
        writeToFile("==========\n", file);
        writeToFile("天亮了\n", file);
        actionLabel = findViewById(R.id.actionLabel1);
        actionLabel.setText("天亮了");
        actionLabel2 = findViewById(R.id.actionLabel2);
        confirmButton = findViewById(R.id.confirmButton1);
        confirmButton.setEnabled(true);
        confirmButton.setText("显示昨晚信息");
        confirmButton.setOnClickListener(displayNightInfo);

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

        pCollection.add(p1);
        pCollection.add(p2);
        pCollection.add(p3);
        pCollection.add(p4);
        pCollection.add(p5);
        pCollection.add(p6);
        pCollection.add(p7);
        pCollection.add(p8);
        pCollection.add(p9);
        pCollection.add(p10);
        pCollection.add(p11);
        pCollection.add(p12);

        //This loop makes unassigned player card transparent
        //Sets onClickListener to assigned player card
        //Displays dead player card and make it unclickable
        for (int i = 0; i < pCollection.size(); i++){
            if (assignedCharacterList.get(i).equals("None")){
                makeTransparent(i);
            }
            ImageView card = pCollection.get(i);
            if (i < numPlayers) {
                card.setOnClickListener(selectPlayer);
                if (alive.get(i).equals(false)) {
                    card.setClickable(false);
                    updateDeadCard(i);
                }
            }
        }
    }

    private void getExtras(Bundle extras){
        wolf = extras.getInt("wolf");
        villagers = extras.getInt("villagers");
        seer = extras.getInt("seer");
        witcher = extras.getInt("witcher");
        guardian = extras.getInt("guardian");
        idiot = extras.getInt("idiot");
        hunter = extras.getInt("hunter");
        mode = extras.getBoolean("mode");
        playerID = (ArrayList<Integer>) extras.get("id");
        assignedCharacterList = (ArrayList<String>) extras.get("characters");
        numPlayers = (Integer) extras.get("numPlayers");
        alive = (ArrayList<Boolean>) extras.get("alive");
        guardedPlayerID = (Integer) extras.get("guardedPlayerID");
        intentKillPlayerID = (Integer) extras.get("intentKillPlayerID");
        antidotePlayerID = (Integer) extras.get("antidotePlayerID");
        poisonPlayerID = (Integer) extras.get("poisonPlayerID");
        witcherHoldsAntidote = (Boolean) extras.get("witcherHoldsAntidote");
        witcherHoldsPoison = (Boolean) extras.get("witcherHoldsPoison");
        file = (File) extras.get("file");
    }

    private void makeTransparent(int i) { pCollection.get(i).setImageResource(R.drawable.transparent); }

    private void updateDeadCard(int i) { pCollection.get(i).setImageResource(R.drawable.card_back_dead); }

    private void updateIdiotCard(int i) {
        pCollection.get(i).setImageResource(R.drawable.idiot);
    }

    private void updateHunterCard(int i) { pCollection.get(i).setImageResource(R.drawable.hunter); }

    private void makeDeadCardUnClickable() {
        for (int i = 0; i < numPlayers; i++) {
            ImageView card = pCollection.get(i);
            if (alive.get(i).equals(false)) {
                card.setClickable(false);
                updateDeadCard(i);
            }
        }
    }

    private void judgeDeath() {
        //Judge death(s)
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
                    if (alive.get(intentKillPlayerID - 1)) {
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
            if (alive.get(poisonPlayerID - 1)) {
                deathID2 = poisonPlayerID;
                alive.set(deathID2 - 1 , false);
                updateDeadCard(deathID2 - 1);
                if (deathID1 == 0) {
                    writeToFile("晚上死亡的是" + deathID2 + "号玩家\n", file);
                    actionLabel.setText("昨天晚上死亡的是");
                    actionLabel2.setText(deathID2 + "号玩家");
                } else {
                    int firstID = deathID1;
                    int secondID = deathID2;
                    int tempID;
                    if (firstID > secondID) {
                        tempID = firstID;
                        firstID = secondID;
                        secondID = tempID;
                    }
                    writeToFile("晚上死亡的是" + firstID + "号玩家\n", file);
                    writeToFile("晚上死亡的是" + secondID + "号玩家\n", file);
                    actionLabel.setText("昨天晚上死亡的是");
                    actionLabel2.setText(firstID + "号玩家和" + secondID + "号玩家");
                }
            } else {
                if (deathID1 == 0) {
                    writeToFile("昨天晚上是平安夜\n", file);
                    actionLabel.setText("昨天晚上是平安夜");
                    actionLabel2.setText("");
                } else {
                    writeToFile("晚上死亡的是" + deathID1 + "号玩家\n", file);
                    actionLabel.setText("昨天晚上死亡的是");
                    actionLabel2.setText(deathID1 + "号玩家");
                }
            }
        } else {
            if (deathID1 == 0) {
                actionLabel.setText("昨天晚上是平安夜");
                actionLabel2.setText("");
                writeToFile("昨天晚上是平安夜\n", file);
            } else {
                writeToFile("晚上死亡的是" + deathID1 + "号玩家\n", file);
                actionLabel.setText("昨天晚上死亡的是");
                actionLabel2.setText(deathID1 + "号玩家");
            }
        }

        makeDeadCardUnClickable();

        //Try to decide victory
        new Handler().postDelayed(() -> {
            Integer won = judgeGame(mode);
            if (won != 0) {
                backToGame(won);
            } else {
                //Vote player and handle dead hunter if any
                //Add onClickListeners to alive players
                for (int i = 0; i < pCollection.size(); i++){
                    if (assignedCharacterList.get(i).equals("None")){
                        makeTransparent(i);
                    }
                    ImageView card = pCollection.get(i);
                    if (i < numPlayers) {
                        card.setOnClickListener(selectPlayer);
                        if (alive.get(i).equals(false)) {
                            card.setClickable(false);
                            updateDeadCard(i);
                        }
                    }
                }

                if (assignedCharacterList.contains("hunter")) {
                    int hunterID = assignedCharacterList.indexOf("hunter") + 1;
                    if (deathID1.equals(hunterID)) {
                        //Hunter is killed by wolf
                        new Handler().postDelayed(() -> {
                            updateHunterCard(hunterID - 1);
                            writeToFile(deathID1 + "号玩家的身份是猎人\n", file);
                            actionLabel.setText(deathID1 + "号玩家是猎人");
                            actionLabel2.setText("请选择你要杀死的玩家或跳过");
                        }, 1000);

                        //Enable hunter to kill
                        new Handler().postDelayed(() -> hunter(true), 3000);
                    } else {
                        enableVote();
                    }
                } else {
                    enableVote();
                }
            }
        }, 2000);
    }

    private void hunter(Boolean t) {
        currentCharacter = "Hunter";
        confirmButton.setEnabled(true);
        confirmButton.setText("跳过");
        if (t) {
            confirmButton.setOnClickListener(hunterKill);
        } else {
            actionLabel.setText("请选择你要杀死的玩家或跳过");
            actionLabel2.setText("");
            confirmButton.setOnClickListener(hunterKillLast);
        }
    }

    private void enableVote() {
        actionLabel.setText("村民可以投票处决玩家");
        actionLabel2.setText("不处决玩家请跳过");
        confirmButton.setEnabled(true);
        confirmButton.setOnClickListener(votePlayer);
        confirmButton.setText("跳过");
        makeDeadCardUnClickable();
        currentSelectedPlayerID = 0;
        selectedPlayerID = 0;
    }

    private final View.OnClickListener displayNightInfo = new View.OnClickListener() {
        public void onClick(View view) {
            confirmButton.setEnabled(false);
            judgeDeath();
        }
    };

    private final View.OnClickListener selectPlayer = new View.OnClickListener() {
        public void onClick(View view) {
            currentSelectedPlayerID = selectedPlayerID;
            selectedPlayerID = Integer.parseInt((String) view.getContentDescription());
            if (currentCharacter.equals("Hunter")) {
                if (currentSelectedPlayerID.equals(selectedPlayerID)){
                    selectedPlayerID = 0;
                    actionLabel2.setText("你取消了选择");
                    confirmButton.setText("跳过");
                } else {
                    actionLabel2.setText("你选择了" + selectedPlayerID + "号玩家");
                    confirmButton.setText("开枪");
                }
            } else {
                if (currentSelectedPlayerID.equals(selectedPlayerID)){
                    selectedPlayerID = 0;
                    actionLabel2.setText("你取消了选择");
                    confirmButton.setText("跳过");
                } else {
                    actionLabel2.setText("你选择了" + selectedPlayerID + "号玩家");
                    confirmButton.setText("处决");
                }
            }
        }
    };

    private final View.OnClickListener votePlayer = new View.OnClickListener() {
        public void onClick(View view) {
            confirmButton.setEnabled(false);
            if (selectedPlayerID == 0) {
                writeToFile("村民没有处决任何玩家\n", file);
                actionLabel.setText("村民没有处决任何人");
                actionLabel2.setText("");
                new Handler().postDelayed(() -> {
                    Integer won = judgeGame(false);
                    if (won != 0) {
                        backToGame(won);
                    } else {
                        //Goes into Night again
                        new Handler().postDelayed(() -> {
                            actionLabel.setText("");
                            actionLabel2.setText("");
                            mp.stop();
                            mp.reset();
                            mp.release();

                            backToNight();
                        }, 3000);
                    }
                }, 3000);
            } else {
                //需要加上第二次票出白痴的情况
                int idiotID = 0;
                int hunterID = 0;
                if (assignedCharacterList.contains("idiot")) {
                    idiotID = assignedCharacterList.indexOf("idiot") + 1;
                }
                if (assignedCharacterList.contains("hunter")) {
                    hunterID = assignedCharacterList.indexOf("hunter") + 1;
                }

                writeToFile("村民处决了" + selectedPlayerID + "号玩家\n", file);
                actionLabel.setText("村民处决了" + selectedPlayerID + "号玩家");
                if (selectedPlayerID.equals(idiotID)) {
                    writeToFile(selectedPlayerID + "号玩家的身份是白痴！\n", file);
                    writeToFile(selectedPlayerID + "号玩家没有死亡\n", file);
                    actionLabel2.setText(selectedPlayerID + "号玩家的身份是白痴");
                    updateIdiotCard(selectedPlayerID - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Integer won = judgeGame(false);
                            if (won != 0) {
                                backToGame(won);
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

                                        backToNight();
                                    }
                                }, 3000);
                            }
                        }
                    }, 3000);
                } else if (selectedPlayerID.equals(hunterID)) {
                    alive.set(selectedPlayerID - 1, false);
                    writeToFile(selectedPlayerID + "号玩家死了！\n", file);
                    writeToFile(selectedPlayerID + "号玩家的身份是猎人\n", file);
                    actionLabel2.setText(selectedPlayerID + "号玩家的身份是猎人");
                    updateHunterCard(selectedPlayerID - 1);
                    hunter(false);
                } else {
                    alive.set(selectedPlayerID - 1, false);
                    writeToFile(selectedPlayerID + "号玩家死了！\n", file);
                    actionLabel2.setText("");
                    updateDeadCard(selectedPlayerID - 1);
                    new Handler().postDelayed(() -> {
                        Integer won = judgeGame(false);
                        if (won != 0) {
                            backToGame(won);
                        } else {
                            //Goes into Night again
                            new Handler().postDelayed(() -> {
                                actionLabel.setText("");
                                actionLabel2.setText("");
                                mp.stop();
                                mp.reset();
                                mp.release();

                                backToNight();
                            }, 3000);
                        }
                    }, 3000);
                }
            }
        }
    };

    private final View.OnClickListener hunterKill = new View.OnClickListener() {
        public void onClick(View view) {
            confirmButton.setEnabled(false);
            currentCharacter = "None";
            if (selectedPlayerID == 0) {
                writeToFile("猎人没有\n", file);
                actionLabel.setText("猎人没有开枪");
                actionLabel2.setText("");
            } else {
                writeToFile("猎人杀死了" + selectedPlayerID + "号玩家\n", file);
                actionLabel.setText("猎人杀死了");
                actionLabel2.setText(selectedPlayerID + "号玩家");
                alive.set(selectedPlayerID - 1, false);
                makeDeadCardUnClickable();
            }
            new Handler().postDelayed(() -> {
                Integer won = judgeGame(mode);
                if (won != 0) {
                    backToGame(won);
                } else {
                    actionLabel.setText("村民可以投票处决玩家");
                    actionLabel2.setText("不处决玩家请跳过");
                    confirmButton.setEnabled(true);
                    confirmButton.setText("跳过");
                    confirmButton.setOnClickListener(votePlayer);
                    currentSelectedPlayerID = 0;
                    selectedPlayerID = 0;
                }
            }, 3000);
        }
    };

    private final View.OnClickListener hunterKillLast = new View.OnClickListener() {
        public void onClick(View view) {
            confirmButton.setEnabled(false);
            currentCharacter = "None";
            if (selectedPlayerID == 0) {
                writeToFile("猎人没有\n", file);
                actionLabel.setText("猎人没有开枪");
                actionLabel2.setText("");
            } else {
                writeToFile("猎人杀死了" + selectedPlayerID + "号玩家\n", file);
                actionLabel.setText("猎人杀死了");
                actionLabel2.setText(selectedPlayerID + "号玩家");
                alive.set(selectedPlayerID - 1, false);
                makeDeadCardUnClickable();
            }

            new Handler().postDelayed(() -> {
                Integer won = judgeGame(mode);
                if (won != 0) {
                    backToGame(won);
                } else {
                    //Goes into Night again
                    new Handler().postDelayed(() -> {
                        actionLabel.setText("");
                        actionLabel2.setText("");
                        mp.stop();
                        mp.reset();
                        mp.release();

                        backToNight();
                    }, 2000);
                    currentSelectedPlayerID = 0;
                    selectedPlayerID = 0;
                }
            }, 3000);
        }
    };

    private Integer judgeGame(Boolean all) {
        //0 stands for undecided victory
        //1 stands for human victory
        //2 stands for werewolf victory
        int aliveGod = 0, aliveVillager = 0, aliveWolf = 0;
        for (int i = 0; i < numPlayers; i++) {
            switch (assignedCharacterList.get(i)) {
                case "wolf":
                    if (alive.get(i).equals(true)) {aliveWolf += 1;}
                    break;
                case "villager":
                    if (alive.get(i).equals(true)) {aliveVillager += 1;}
                    break;
                default:
                    if (alive.get(i).equals(true)) {aliveGod += 1;}
                    break;
            }
        }
        if (all) {
            if (aliveGod + aliveVillager == 0) {return 2;}
            if (aliveWolf == 0) {return 1;}
        } else {
            if (aliveWolf == 0) {return 1;}
            if (aliveVillager == 0) {return 2;}
            if (aliveGod == 0) {return 2;}
        }
        //Undecided
        return 0;
    }

    private void backToGame(Integer won){
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
        game.putExtra("hunter", hunter);
        game.putExtra("mode", mode);

        startActivity(game);
    }

    private void backToNight() {
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
        night.putExtra("hunter", hunter);
        night.putExtra("mode", mode);
        night.putExtra("file", file);
        startActivity(night);
    }

    private void writeToFile(String data, File file) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file,true);
            output.write(data.getBytes());
        }
        catch (IOException e) {
            Log.e("Exception", "Write failed " + e.toString());
        }
        finally {
            if (output != null) {
                try {
                    output.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}