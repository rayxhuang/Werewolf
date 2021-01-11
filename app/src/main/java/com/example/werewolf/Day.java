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
    private int seerID;
    private int witcherID;
    private int guardianID;
    private int idiotID;
    private int hunterID;
    private Character Idiot;
    private Character Hunter;
    private Boolean mode;
    private ArrayList<Character> characterArray;
    private Integer numPlayers;

    private int wolf;
    private int villagers;
    private int seer;
    private int witcher;
    private int guardian;
    private int idiot;
    private int hunter;

    private Integer deathID1;
    private Integer deathID2;

    private Integer selectedPlayerID = 0;
    private Integer currentSelectedPlayerID = 0;
    private String currentCharacter = "None";
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
            Character A = characterArray.get(i);
            if (A.getAssignedCharacter() == null){
                makeTransparent(i);
            }
            ImageView card = pCollection.get(i);
            if (i < numPlayers) {
                card.setOnClickListener(selectPlayer);
                if (!A.isAlive()) {
                    card.setClickable(false);
                }
            }
        }
    }

    private void getExtras(Bundle extras){
        mode = extras.getBoolean("mode");
        file = (File) extras.get("file");
        characterArray = extras.getParcelableArrayList("characters");
        numPlayers = (Integer) extras.get("numPlayers");

        seerID = extras.getInt("seerID");
        witcherID = extras.getInt("witcherID");
        guardianID = extras.getInt("guardianID");
        idiotID = extras.getInt("idiotID");
        hunterID = extras.getInt("hunterID");
        if (idiotID != -1) { Idiot = characterArray.get(idiotID - 1); }
        if (hunterID != -1) { Hunter = characterArray.get(hunterID - 1); }
        deathID1 = extras.getInt("deathID1");
        deathID2 = extras.getInt("deathID2");

        wolf = extras.getInt("wolf");
        villagers = extras.getInt("villagers");
        seer = extras.getInt("seer");
        witcher = extras.getInt("witcher");
        guardian = extras.getInt("guardian");
        idiot = extras.getInt("idiot");
        hunter = extras.getInt("hunter");
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
            if (!characterArray.get(i).isAlive()) {
                card.setClickable(false);
                updateDeadCard(i);
            }
        }
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
            if (deathID1 != 0 && deathID2 != 0) {
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
            } else {
                if (deathID1 == 0 && deathID2 == 0) {
                    writeToFile("昨天晚上是平安夜\n", file);
                    actionLabel.setText("昨天晚上是平安夜");
                    actionLabel2.setText("");
                } else {
                    if (deathID1 == 0) {
                        deathID1 = deathID2;
                    }
                    writeToFile("晚上死亡的是" + deathID1 + "号玩家\n", file);
                    actionLabel.setText("昨天晚上死亡的是");
                    actionLabel2.setText(deathID1 + "号玩家");
                }
            }
            makeDeadCardUnClickable();

            new Handler().postDelayed(() -> {
                Integer won = judgeGame(mode);
                if (won != 0) {
                    backToGame(won);
                } else {
                    //Vote player and handle dead hunter if any
                    //Add onClickListeners to alive players
                    if (Hunter != null) {
                        if (!Hunter.isAlive() && !deathID2.equals(hunterID)) {
                            //Hunter is killed by wolf
                            new Handler().postDelayed(() -> {
                                updateHunterCard(hunterID - 1);
                                writeToFile(hunterID + "号玩家的身份是猎人\n", file);
                                actionLabel.setText(hunterID + "号玩家是猎人");
                                actionLabel2.setText("请选择你要杀死的玩家或跳过");
                            }, 1000);

                            //Enable hunter to kill
                            new Handler().postDelayed(() -> hunter(true), 3000);
                        } else {
                            //Hunter is poisoned or still alive
                            enableVote();
                        }
                    } else {
                        //No hunter in the game
                        enableVote();
                    }
                }
            }, 2000);
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
            } else {
                //需要加上第二次票出白痴的情况
                writeToFile("村民处决了" + selectedPlayerID + "号玩家\n", file);
                actionLabel.setText("村民处决了" + selectedPlayerID + "号玩家");
                if (Idiot != null) {
                    if (selectedPlayerID.equals(idiotID)) {
                        if (!Idiot.hasBeenVoted()) {
                            writeToFile(idiotID + "号玩家的身份是白痴！\n", file);
                            writeToFile(idiotID + "号玩家没有死亡\n", file);
                            actionLabel2.setText(selectedPlayerID + "号玩家的身份是白痴");
                            updateIdiotCard(selectedPlayerID - 1);
                            Idiot.setHasBeenVoted(true);
                        } else {
                            writeToFile("村民处决了白痴！\n", file);
                            writeToFile(idiotID + "号玩家死了！\n", file);
                            actionLabel2.setText("村民处决了白痴！");
                            updateIdiotCard(selectedPlayerID - 1);
                            Idiot.setAlive(false);
                        }
                    }
                } else if (Hunter != null) {
                    if (selectedPlayerID.equals(hunterID)) {
                        writeToFile(selectedPlayerID + "号玩家死了！\n", file);
                        writeToFile(selectedPlayerID + "号玩家的身份是猎人\n", file);
                        actionLabel2.setText(selectedPlayerID + "号玩家的身份是猎人");
                        updateHunterCard(hunterID - 1);
                        Hunter.setAlive(false);
                        hunter(false);
                    }
                } else {
                    characterArray.get(selectedPlayerID - 1).setAlive(false);
                    writeToFile(selectedPlayerID + "号玩家死了！\n", file);
                    actionLabel2.setText("");
                    updateDeadCard(selectedPlayerID - 1);
                }
            }

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
                characterArray.get(selectedPlayerID - 1).setAlive(false);
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
                characterArray.get(selectedPlayerID - 1).setAlive(false);
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
            Character A = characterArray.get(i);
            if (A.getAssignedCharacter().equals("村民") && A.isAlive()) { aliveVillager += 1; }
            if (A.getAssignedCharacter().equals("狼人") && A.isAlive()) { aliveWolf += 1; }
            if (A.getParty().equals("神") && A.isAlive()) { aliveGod += 1; }
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

        game.putParcelableArrayListExtra("characters", characterArray);
        game.putExtra("mode", mode);
        game.putExtra("finished", true);
        game.putExtra("won", won);
        game.putExtra("numPlayers", numPlayers);

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

    private void backToNight() {
        Intent night = new Intent(Day.this, Night.class);

        night.putParcelableArrayListExtra("characters", characterArray);
        night.putExtra("numPlayers", numPlayers);
        night.putExtra("mode", mode);
        night.putExtra("file", file);

        night.putExtra("seerID", seerID);
        night.putExtra("witcherID", witcherID);
        night.putExtra("guardianID", guardianID);
        night.putExtra("idiotID", idiotID);
        night.putExtra("hunterID", hunterID);

        night.putExtra("wolf", wolf);
        night.putExtra("villagers", villagers);
        night.putExtra("seer", seer);
        night.putExtra("witcher", witcher);
        night.putExtra("guardian", guardian);
        night.putExtra("idiot", idiot);
        night.putExtra("hunter", hunter);

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