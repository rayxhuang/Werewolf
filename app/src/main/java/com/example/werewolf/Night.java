package com.example.werewolf;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.werewolf.R.string.killTextLabel;

public class Night extends AppCompatActivity {
    private File file;
    private int seerID;
    private int witcherID;
    private int guardianID;
    private int idiotID;
    private int hunterID;
    private Character Seer;
    private Character Witcher;
    private Character Guardian;
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

    private String currentCharacter = "None";
    private Integer selectedPlayerID = 0;
    private Integer currentSelectedPlayerID = 0;
    private Integer intentKillPlayerID = 0;
    private Integer deathID1 = 0;
    private Integer deathID2 = 0;

    private TextView actionLabel;
    private Button confirmButton;
    private Button witcherSaveButton;
    private Button witcherKillButton;
    private ImageView p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12;
    private final ArrayList<ImageView> pCollection = new ArrayList<>(12);
    private final ArrayList<Integer> cCollection = new ArrayList<>(12);

    private MediaPlayer mp;

    private final View.OnClickListener selectPlayer = new View.OnClickListener() {
        public void onClick(View view) {
            currentSelectedPlayerID = selectedPlayerID;
            selectedPlayerID = Integer.parseInt((String) view.getContentDescription());
            if (currentSelectedPlayerID.equals(selectedPlayerID)){
                selectedPlayerID = 0;
                actionLabel.setText("你取消了选择");
                if (currentCharacter.equals("Wolf")){
                    confirmButton.setEnabled(false);
                }
                if (currentCharacter.equals("Seer")){
                    confirmButton.setText("跳过");
                }
                if (currentCharacter.equals("Guardian")){
                    confirmButton.setText("跳过");
                }
                if (currentCharacter.equals("Witcher")){
                    confirmButton.setText("跳过");
                    witcherSaveButton.setEnabled(false);
                    witcherKillButton.setEnabled(false);
                }
            } else {
                actionLabel.setText("你选择了" + selectedPlayerID + "号玩家");
                if (currentCharacter.equals("Witcher")){
                    if (Witcher.isHoldingAntidote()) {
                        if (selectedPlayerID.equals(intentKillPlayerID)) {
                            if (selectedPlayerID != witcherID) {
                                //Witcher cannot save herself
                                witcherSaveButton.setEnabled(true);
                            }
                        } else {
                            witcherSaveButton.setEnabled(false);
                        }
                    }
                    if (Witcher.isHoldingPoison()) {
                        witcherKillButton.setEnabled(true);
                    }
                }
                if (currentCharacter.equals("Guardian")){
                    if (selectedPlayerID == Guardian.getGuardedPlayerID()) {
                        //Guardian cannot guard the same player two consecutive nights
                        actionLabel.setText("你不能连续两晚守护同一玩家");
                        selectedPlayerID = 0;
                        confirmButton.setText("跳过");
                    } else {
                        confirmButton.setText("守护");
                    }
                }
                if (currentCharacter.equals("Seer")){
                    confirmButton.setText("查验");
                }
                if (currentCharacter.equals("Wolf")){
                    confirmButton.setEnabled(true);
                }
            }
        }
    };

    private final View.OnClickListener confirmGuard = new View.OnClickListener() {
        public void onClick(View view) {
            Guardian.setGuardedPlayerID(selectedPlayerID);
            confirmButton.setEnabled(false);
            hideRevealed();
            makeAllClickable(false);
            if (selectedPlayerID == 0){
                actionLabel.setText("你没有守护任何玩家");
                writeToFile("守卫没有守护任何玩家\n", file);
            } else {
                actionLabel.setText("你守护了" + selectedPlayerID + "号玩家");
                writeToFile("守卫守护了" + selectedPlayerID + "号玩家\n", file);
            }
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            //Plays guardian finish audio
            new Handler().postDelayed(() -> {
                mp = MediaPlayer.create(Night.this, R.raw.audio_guard_finish);
                mp.setOnCompletionListener(prepareForNext);
                mp.start();
            }, 2000);

            //Setup wolf
            new Handler().postDelayed(() -> {
                setupWolf();
                makeAllClickable(true);
            }, 12000);
        }
    };

    private final View.OnClickListener confirmKill = new View.OnClickListener() {
        public void onClick(View view) {
            confirmButton.setEnabled(false);
            makeAllClickable(false);
            actionLabel.setText("你杀害了" + selectedPlayerID + "号玩家");
            writeToFile("狼人杀害了" + selectedPlayerID + "号玩家\n", file);
            intentKillPlayerID = selectedPlayerID;
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;

            //Plays wolf finish audio
            new Handler().postDelayed(() -> {
                mp = MediaPlayer.create(Night.this, R.raw.audio_wolf_finish);
                mp.setOnCompletionListener(prepareForNext);
                mp.start();
                //Hide wolf card
                hideRevealed();
                if (guardianID != -1) {
                    if (Guardian.getGuardedPlayerID() != (intentKillPlayerID)) {
                        Integer won = judgeGame(mode);
                        if (won == 2) {
                            mp.release();
                            Intent game = new Intent(Night.this, Game.class);

                            game.putExtra("characters", characterArray);
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
                        } else {
                            //Setup seer
                            new Handler().postDelayed(() -> {
                                makeAllClickable(true);
                                setupSeer();
                            }, 9000);
                        }
                    } else {
                        //Setup seer
                        new Handler().postDelayed(() -> {
                            makeAllClickable(true);
                            setupSeer();
                        }, 9000);
                    }
                }
            }, 3000);
        }
    };

    private final View.OnClickListener confirmSave = new View.OnClickListener() {
        public void onClick(View view) {
            actionLabel.setText("你解救了" + selectedPlayerID + "号玩家");
            writeToFile("女巫解救了" + selectedPlayerID + "号玩家\n", file);
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            Witcher.setSavedPlayerID(intentKillPlayerID);
            Witcher.setHoldingAntidote(false);
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            witcherSaveButton.setEnabled(false);
            witcherKillButton.setEnabled(false);
            confirmButton.setEnabled(false);
            makeAllClickable(false);
            finishWitcher();
        }
    };

    private final View.OnClickListener witcherSkip = new View.OnClickListener() {
        public void onClick(View view) {
            actionLabel.setText("你没有进行操作");
            writeToFile("女巫没有进行操作\n", file);
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            confirmButton.setEnabled(false);
            witcherSaveButton.setEnabled(false);
            witcherKillButton.setEnabled(false);
            makeAllClickable(false);
            finishWitcher();
        }
    };

    private final View.OnClickListener confirmPoison = new View.OnClickListener() {
        public void onClick(View view) {
            actionLabel.setText("你毒死了" + selectedPlayerID + "号玩家");
            writeToFile("女巫毒死了" + selectedPlayerID + "号玩家\n", file);
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            Witcher.setPoisonedPlayerID(selectedPlayerID);
            Witcher.setHoldingPoison(false);
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            witcherSaveButton.setEnabled(false);
            witcherKillButton.setEnabled(false);
            confirmButton.setEnabled(false);
            makeAllClickable(false);
            finishWitcher();
        }
    };

    private final View.OnClickListener confirmSeek = new View.OnClickListener() {
        public void onClick(View view) {
            confirmButton.setEnabled(false);
            hideRevealed();
            makeAllClickable(false);
            if (selectedPlayerID != 0) {
                String character = characterArray.get(selectedPlayerID - 1).getParty();
                boolean goodCharacter = true;
                String characterLabel = "好人";
                if ("狼".equals(character)) {
                    goodCharacter = false;
                    characterLabel = "狼人";
                }
                actionLabel.setText(selectedPlayerID + "号玩家是" + characterLabel);
                writeToFile("预言家查验了" + selectedPlayerID + "号玩家\n", file);
                showCharacter(goodCharacter, selectedPlayerID);
            } else {
                actionLabel.setText("你没有查验任何人");
                writeToFile("预言家没有查验任何玩家\n", file);
            }
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            //Plays seer finish audio
            new Handler().postDelayed(() -> {
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.reset();
                }
                hideRevealed();
                mp = MediaPlayer.create(Night.this, R.raw.audio_seer_finish);
                mp.setOnCompletionListener(prepareForNext);
                mp.start();
            }, 3000);

            if (witcherID != -1) {
                new Handler().postDelayed(() -> {
                    makeAllClickable(true);
                    setupWitcher();
                    //
                }, 12000);
            } else {
                new Handler().postDelayed(() -> finishNight(), 12000);
            }

        }
    };

    private final MediaPlayer.OnCompletionListener prepareForNext = mp -> {
        mp.stop();
        mp.reset();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Get extras
            getExtras(extras);
            //Initialise
            init();
        }

        //Night starts...
        new Handler().postDelayed(() -> {
            mp = MediaPlayer.create(Night.this, R.raw.audio_night_start);
            mp.setOnCompletionListener(prepareForNext);
            mp.start();
        }, 2000);

        //Setup different characters
        new Handler().postDelayed(() -> {
            makeCardVisible();
            if (guardianID != -1){
                setupGuardian();
            } else {
                setupWolf();
            }
        }, 7000);
    }

//    This function initialises Night.class
    private void init() {
        actionLabel = findViewById(R.id.actionLabel);
        actionLabel.setText("天黑请闭眼");
        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setEnabled(false);
        writeToFile("==========\n", file);
        writeToFile("天黑了\n", file);
        witcherSaveButton = findViewById(R.id.saveButton);
        witcherKillButton = findViewById(R.id.killButton);

        p1 = findViewById(R.id.pCard1);
        p2 = findViewById(R.id.pCard2);
        p3 = findViewById(R.id.pCard3);
        p4 = findViewById(R.id.pCard4);
        p5 = findViewById(R.id.pCard5);
        p6 = findViewById(R.id.pCard6);
        p7 = findViewById(R.id.pCard7);
        p8 = findViewById(R.id.pCard8);
        p9 = findViewById(R.id.pCard9);
        p10 = findViewById(R.id.pCard10);
        p11 = findViewById(R.id.pCard11);
        p12 = findViewById(R.id.pCard12);

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
            card.setVisibility(View.GONE);
            if (i < numPlayers) {
                card.setOnClickListener(selectPlayer);
                if (!A.isAlive()) {
                    card.setClickable(false);
                    updateDeadCard(i);
                }
            }
        }

        //This stores resource ids in an array for later use
        cCollection.add(R.drawable.card_back1);
        cCollection.add(R.drawable.card_back2);
        cCollection.add(R.drawable.card_back3);
        cCollection.add(R.drawable.card_back4);
        cCollection.add(R.drawable.card_back5);
        cCollection.add(R.drawable.card_back6);
        cCollection.add(R.drawable.card_back7);
        cCollection.add(R.drawable.card_back8);
        cCollection.add(R.drawable.card_back9);
        cCollection.add(R.drawable.card_back10);
        cCollection.add(R.drawable.card_back11);
        cCollection.add(R.drawable.card_back12);
    }

    //This function gets all the extras and set them to the field
    private void getExtras(Bundle extras){
        mode = extras.getBoolean("mode");
        file = (File) extras.get("file");
        characterArray = (ArrayList<Character>) extras.get("characters");
        numPlayers = extras.getInt("numPlayers");

        seerID = extras.getInt("seerID");
        witcherID = extras.getInt("witcherID");
        guardianID = extras.getInt("guardianID");
        idiotID = extras.getInt("idiotID");
        hunterID = extras.getInt("hunterID");
        if (seerID != -1) { Seer = characterArray.get(seerID - 1); }
        if (witcherID != -1) { Witcher = characterArray.get(witcherID - 1); }
        if (guardianID != -1) { Guardian = characterArray.get(guardianID - 1); }

        wolf = extras.getInt("wolf");
        villagers = extras.getInt("villagers");
        seer = extras.getInt("seer");
        witcher = extras.getInt("witcher");
        guardian = extras.getInt("guardian");
        idiot = extras.getInt("idiot");
        hunter = extras.getInt("hunter");
    }

    //This function makes all card visible
    private void makeCardVisible () {
        for (int i = 0; i < pCollection.size(); i++) {
            pCollection.get(i).setVisibility(View.VISIBLE);
        }
    }

    //This function makes a player's card transparent
    private void makeTransparent(int i) {
        pCollection.get(i).setImageResource(R.drawable.transparent);
    }

    //This function toggles all cards' clickable status
    private void makeAllClickable(Boolean t) {
        for (int i = 0; i < pCollection.size(); i++) {
            pCollection.get(i).setClickable(t);
        }
    }

    //This function makes dead player's card unclickable
    private void makeDeadCardUnClickable() {
        for (int i = 0; i < numPlayers; i++) {
            ImageView card = pCollection.get(i);
            if (!characterArray.get(i).isAlive()) {
                card.setClickable(false);
                updateDeadCard(i);
            }
        }
    }

    //This function makes a player's card to display dead status
    private void updateDeadCard(int i) {
        pCollection.get(i).setImageResource(R.drawable.card_back_dead);
    }

    //This function replaces all assigned cards back to original numbered card
    private void hideRevealed() {
        for (int i = 0; i < numPlayers; i++) {
            pCollection.get(i).setImageResource(cCollection.get(i));
        }
        makeDeadCardUnClickable();
    }

    //This function setup Guardian
    private void setupGuardian() {
        currentCharacter = "Guardian";
        actionLabel.setText(R.string.guardTextLabel);
        confirmButton.setEnabled(true);
        confirmButton.setText(R.string.skipTextLabel);
        confirmButton.setOnClickListener(confirmGuard);
        showGuardian();
        mp = MediaPlayer.create(this, R.raw.audio_guardian_start);
        mp.setOnCompletionListener(prepareForNext);
        mp.start();
    }

    private void showGuardian() {
        for (int i = 0; i < numPlayers; i++) {
            if (characterArray.get(i).getAssignedCharacter().equals("守卫")) {
                pCollection.get(i).setImageResource(R.drawable.guardian);
            }
        }
    }

    //This function setup Wolf
    private void setupWolf() {
        currentCharacter = "Wolf";
        actionLabel.setText(killTextLabel);
        confirmButton.setText("杀害");
        //Wolf has to kill someone, cannot skip, but he can kill a dead body
        confirmButton.setEnabled(false);
        confirmButton.setOnClickListener(confirmKill);
        makeAllClickable(true);
        mp = MediaPlayer.create(Night.this, R.raw.audio_wolf_start);
        mp.setOnCompletionListener(prepareForNext);
        mp.start();

        //Reveal all wolves
        new Handler().postDelayed(this::showWolfMate, 2000);
    }

    //This function replaces player's card with wolf if his assigned character is wolf
    private void showWolfMate() {
        for (int i = 0; i < numPlayers; i++) {
            Character A = characterArray.get(i);
            if (A.getAssignedCharacter().equals("狼人")) {
                ImageView card = pCollection.get(i);
                if (A.isAlive()) {
                    card.setImageResource(R.drawable.wolf);
                } else {
                    card.setImageResource(R.drawable.wolf_dead);
                }
            }
        }
    }

    private void setupSeer() {
        currentCharacter = "Seer";
        confirmButton.setText("跳过");
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setEnabled(true);
        confirmButton.setOnClickListener(confirmSeek);
        showSeer();
        actionLabel.setText(R.string.seerTextLabel);
        makeDeadCardUnClickable();
        mp = MediaPlayer.create(Night.this, R.raw.audio_seer_start);
        mp.setOnCompletionListener(prepareForNext);
        mp.start();
    }

    private void showSeer() {
        for (int i = 0; i < numPlayers; i++) {
            if (characterArray.get(i).getAssignedCharacter().equals("预言家")) {
                pCollection.get(i).setImageResource(R.drawable.seer);
            }
        }
    }

    private void showCharacter(Boolean g, int i) {
        int resID = R.drawable.villager;
        if (!g) {
            resID = R.drawable.wolf;
        }
        pCollection.get(i - 1).setImageResource(resID);
    }

    private void setupWitcher() {
        currentCharacter = "Witcher";
        confirmButton.setEnabled(true);
        confirmButton.setText(R.string.skipTextLabel);
        confirmButton.setOnClickListener(witcherSkip);
        confirmButton.setText("跳过");
        showWitcher();
        witcherSaveButton.setVisibility(View.VISIBLE);
        witcherSaveButton.setOnClickListener(confirmSave);
        witcherKillButton.setVisibility(View.VISIBLE);
        witcherKillButton.setOnClickListener(confirmPoison);
        actionLabel.setText(R.string.actionTextLabel);
        mp = MediaPlayer.create(Night.this, R.raw.audio_witcher_start);
        mp.setOnCompletionListener(prepareForNext);
        mp.start();
        if (Witcher.isHoldingAntidote()) {
            showIntentKill(intentKillPlayerID);
        }
    }

    private void showWitcher() {
        for (int i = 0; i < numPlayers; i++) {
            if (characterArray.get(i).getAssignedCharacter().equals("女巫")) {
                pCollection.get(i).setImageResource(R.drawable.witcher);
            }
        }
    }

    private void showIntentKill(int i){
        pCollection.get(i - 1).setImageResource(R.drawable.card_back_intent_kill);
    }

    private void finishWitcher() {
        //Plays witcher finish audio
        new Handler().postDelayed(() -> {
            mp = MediaPlayer.create(Night.this, R.raw.audio_witcher_finish);
            mp.setOnCompletionListener(prepareForNext);
            mp.start();
            hideRevealed();
            confirmButton.setEnabled(false);
            witcherSaveButton.setVisibility(View.GONE);
            witcherKillButton.setVisibility(View.GONE);
            witcherSaveButton.setEnabled(false);
            witcherKillButton.setEnabled(false);
        }, 2000);

        //Setup next
        new Handler().postDelayed(this::finishNight, 12000);
    }

    private void finishNight(){
        mp.release();

        judgeDeath();

        Intent day = new Intent(Night.this, Day.class);

        day.putParcelableArrayListExtra("characters", characterArray);
        day.putExtra("numPlayers", numPlayers);
        day.putExtra("mode", mode);
        day.putExtra("file", file);

        day.putExtra("deathID1", deathID1);
        day.putExtra("deathID2", deathID2);

        day.putExtra("seerID", seerID);
        day.putExtra("witcherID", witcherID);
        day.putExtra("guardianID", guardianID);
        day.putExtra("idiotID", idiotID);
        day.putExtra("hunterID", hunterID);

        day.putExtra("wolf", wolf);
        day.putExtra("villagers", villagers);
        day.putExtra("seer", seer);
        day.putExtra("witcher", witcher);
        day.putExtra("guardian", guardian);
        day.putExtra("idiot", idiot);
        day.putExtra("hunter", hunter);

        startActivity(day);
    }

    private Integer judgeGame(Boolean all) {
        //0 stands for undecided victory
        //2 stands for werewolf victory
        if (!characterArray.get(intentKillPlayerID - 1).isAlive()) { return 0; }
        if (witcherID != -1) {
            if (Witcher.isHoldingAntidote()) {
                return 0;
            }
        }
        characterArray.get(intentKillPlayerID - 1).setAlive(false);
        int aliveGod = 0, aliveVillager = 0;
        for (int i = 0; i < numPlayers; i++) {
            Character A = characterArray.get(i);
            if (A.getAssignedCharacter().equals("村民") && A.isAlive()) { aliveVillager += 1; }
            if (A.getParty().equals("神") && A.isAlive()) { aliveGod += 1; }
        }
        if (all) {
            if (aliveGod + aliveVillager == 0) {return 2;}
        } else {
            if (aliveVillager == 0) {return 2;}
            if (aliveGod == 0) {return 2;}
        }
        //Undecided
        characterArray.get(intentKillPlayerID - 1).setAlive(true);
        return 0;
    }

    private void judgeDeath() {
        //Check if wolf managed to kill
        Character A = characterArray.get(intentKillPlayerID - 1);
        if (A.isAlive()) {
            if (Witcher != null) {
                if (Witcher.getSavedPlayerID() == intentKillPlayerID) {
                    if (Guardian != null) {
                        if (Guardian.getGuardedPlayerID() == intentKillPlayerID) {
                            A.setAlive(false);
                            deathID1 = intentKillPlayerID;
                        }
                    }
                }
            } else if (Guardian != null) {
                if (Guardian.getGuardedPlayerID() != intentKillPlayerID) {
                    A.setAlive(false);
                    deathID1 = intentKillPlayerID;
                }
            }
        }

        //Check if witcher used poison
        if (Witcher != null) {
            int poisonedPlayerID = Witcher.getPoisonedPlayerID();
            if (poisonedPlayerID != 0) {
                Character B = characterArray.get(poisonedPlayerID - 1);
                B.setAlive(false);
                deathID2 = poisonedPlayerID;
            }
        }
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