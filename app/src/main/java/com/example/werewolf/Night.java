package com.example.werewolf;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.example.werewolf.R.string.killTextLabel;

public class Night extends AppCompatActivity {
    private int wolf;
    private int villagers;
    private int seer;
    private int witcher;
    private int guardian;
    private int idiot;
    private ArrayList<Integer> playerID;
    private ArrayList<String> assignedCharacterList;
    private Integer numPlayers;
    private ArrayList<Boolean> alive;
    private String currentCharacter = "None";
    private Integer selectedPlayerID = 0;
    private Integer currentSelectedPlayerID = 0;
    private Integer guardedPlayerID;
    private Integer intentKillPlayerID;
    private Boolean witcherHoldsAntidote;
    private Boolean witcherHoldsPoison;
    private Integer antidotePlayerID;
    private Integer poisonPlayerID;
    private TextView actionLabel;
    private Button confirmButton;
    private Button witcherSaveButton;
    private Button witcherKillButton;
    private ImageView p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12;

    private MediaPlayer mp;

    private View.OnClickListener selectPlayer = new View.OnClickListener() {
        public void onClick(View view) {
            currentSelectedPlayerID = selectedPlayerID;
            selectedPlayerID = Integer.parseInt((String) view.getContentDescription());
            if (currentSelectedPlayerID == selectedPlayerID){
                selectedPlayerID = 0;
                Toast.makeText(Night.this, "你取消了选择", Toast.LENGTH_SHORT).show();
                if (currentCharacter.equals("Wolf")){
                    confirmButton.setEnabled(false);
                }
                if (currentCharacter.equals("Seer")){
                    confirmButton.setEnabled(false);
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
                Toast.makeText(Night.this, "你选择了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
                if (currentCharacter.equals("Witcher")){
                    if (witcherHoldsAntidote) {
                        if (selectedPlayerID == intentKillPlayerID) {
                            witcherSaveButton.setEnabled(true);
                        } else {
                            witcherSaveButton.setEnabled(false);
                        }
                    }
                    if (witcherHoldsPoison) {
                        witcherKillButton.setEnabled(true);
                    }
                }
                if (currentCharacter.equals("Guardian")){
                    if (guardedPlayerID == selectedPlayerID) {
                        Toast.makeText(Night.this, "你不能连续两晚守护同一玩家", Toast.LENGTH_SHORT).show();
                        selectedPlayerID = 0;
                        confirmButton.setText("跳过");
                    } else {
                        confirmButton.setText("守护");
                    }
                }
                if (currentCharacter.equals("Seer")){
                    confirmButton.setEnabled(true);
                }
                if (currentCharacter.equals("Wolf")){
                    confirmButton.setEnabled(true);
                }
            }
        }
    };

    private View.OnClickListener confirmGuard = new View.OnClickListener() {
        public void onClick(View view) {
            guardedPlayerID = selectedPlayerID;
            confirmButton.setEnabled(false);
            if (selectedPlayerID == 0){
                Toast.makeText(Night.this, "你没有守护任何玩家", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Night.this, "你守护了" + guardedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
            }
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            //Plays guardian finish audio
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mp = MediaPlayer.create(Night.this, R.raw.audio_guard_finish);
                    mp.setOnCompletionListener(prepareForNext);
                    mp.start();
                }
            }, 2000);

            //Setup wolf
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setupWolf();
                }
            }, 12000);
        }
    };

    private View.OnClickListener confirmKill = new View.OnClickListener() {
        public void onClick(View view) {
            confirmButton.setEnabled(false);
            Toast.makeText(Night.this, "你杀害了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
            intentKillPlayerID = selectedPlayerID;
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;

            //Plays wolf finish audio
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mp = MediaPlayer.create(Night.this, R.raw.audio_wolf_finish);
                    mp.setOnCompletionListener(prepareForNext);
                    mp.start();
                    //Hide wolf card
                    hideWolf();
                    if (guardedPlayerID != intentKillPlayerID) {
                        Integer won = judgeGame(false);
                        if (won == 2){
                            mp.release();
                            Intent game = new Intent(Night.this, Game.class);

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
                            //Setup seer
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    makeUnClickable();
                                    setupSeer();
                                }
                            }, 9000);
                        }
                    } else {
                        //Setup seer
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                makeUnClickable();
                                setupSeer();
                            }
                        }, 9000);
                    }
                }
            }, 3000);
        }
    };

    private View.OnClickListener confirmSave = new View.OnClickListener() {
        public void onClick(View view) {
            Toast.makeText(Night.this, "你解救了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            witcherHoldsAntidote = false;
            witcherSaveButton.setEnabled(false);
            witcherKillButton.setEnabled(false);
            confirmButton.setEnabled(false);
            antidotePlayerID = intentKillPlayerID;
            finishWitcher();
        }
    };

    private View.OnClickListener witcherSkip = new View.OnClickListener() {
        public void onClick(View view) {
            Toast.makeText(Night.this, "你没有进行操作", Toast.LENGTH_SHORT).show();
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            confirmButton.setEnabled(false);
            witcherSaveButton.setEnabled(false);
            witcherKillButton.setEnabled(false);
            finishWitcher();
        }
    };

    private View.OnClickListener confirmPoison = new View.OnClickListener() {
        public void onClick(View view) {
            Toast.makeText(Night.this, "你毒死了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            poisonPlayerID = selectedPlayerID;
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            witcherHoldsPoison = false;
            confirmButton.setEnabled(false);
            witcherSaveButton.setEnabled(false);
            witcherKillButton.setEnabled(false);
            finishWitcher();
        }
    };

    private View.OnClickListener confirmSeek = new View.OnClickListener() {
        public void onClick(View view) {
            confirmButton.setEnabled(false);
            String character = assignedCharacterList.get(selectedPlayerID - 1);
            Boolean goodCharacter = true;
            String characterLabel = "好人";
            switch (character) {
                case "wolf":
                    goodCharacter = false;
                    characterLabel = "坏人";
            }
            Toast.makeText(Night.this, selectedPlayerID + "号玩家是" + characterLabel, Toast.LENGTH_SHORT).show();
            showCharacter(goodCharacter, selectedPlayerID);
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            //Plays seer finish audio
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                    }
                    hideWolf();
                    mp = MediaPlayer.create(Night.this, R.raw.audio_seer_finish);
                    mp.setOnCompletionListener(prepareForNext);
                    mp.start();
                }
            }, 3000);

            if (assignedCharacterList.contains("witcher")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (assignedCharacterList.contains("witcher")){
                            setupWitcher();
                        }
                        //
                    }
                }, 12000);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishNight();
                    }
                }, 12000);
            }

        }
    };

    private MediaPlayer.OnCompletionListener prepareForNext = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
//            Toast.makeText(Wolf.this, "Audi playback finished, this is auto stop", Toast.LENGTH_SHORT).show();
            mp.stop();
            mp.reset();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night);
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
            numPlayers = (Integer) extras.get("numPlayers");
            alive = (ArrayList<Boolean>) getIntent().getSerializableExtra("alive");
            guardedPlayerID = (Integer) extras.get("guardedPlayerID");
            intentKillPlayerID = (Integer) extras.get("intentKillPlayerID");
            antidotePlayerID = (Integer) extras.get("antidotePlayerID");
            poisonPlayerID = (Integer) extras.get("poisonPlayerID");
            witcherHoldsAntidote = (Boolean) extras.get("witcherHoldsAntidote");
            witcherHoldsPoison = (Boolean) extras.get("witcherHoldsPoison");
        }
        init();

        //Night starts...
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(Night.this, R.raw.audio_night_start);
                mp.setOnCompletionListener(prepareForNext);
                mp.start();
            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeCardVisible();
                if (assignedCharacterList.contains("guardian")){
                    setupGuardian();
                } else {
                    setupWolf();
                }
            }
        }, 10000);

    }

    private void init() {
        actionLabel = findViewById(R.id.actionLabel);
        actionLabel.setText("天黑请闭眼");
        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setEnabled(false);
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


        for (int i = 0; i < 12; i++){
            if (assignedCharacterList.get(i).equals("None")){
                makeTransparent(i);
            }
        }

        for (int i = 1; i < 13; i++) {
            switch (i){
                case 1:
                    p1.setVisibility(View.GONE);
                    p1.setOnClickListener(selectPlayer);
                    if (alive.get(i - 1) == false) {
                        p1.setClickable(false);
                        updateDeadCard(i - 1);
                    }
                    break;
                case 2:
                    p2.setVisibility(View.GONE);
                    p2.setOnClickListener(selectPlayer);
                    if (alive.get(i - 1) == false) {
                        p2.setClickable(false);
                        updateDeadCard(i - 1);
                    }
                    break;
                case 3:
                    p3.setVisibility(View.GONE);
                    p3.setOnClickListener(selectPlayer);
                    if (alive.get(i - 1) == false) {
                        p3.setClickable(false);
                        updateDeadCard(i - 1);
                    }
                    break;
                case 4:
                    p4.setVisibility(View.GONE);
                    p4.setOnClickListener(selectPlayer);
                    if (alive.get(i - 1) == false) {
                        p4.setClickable(false);
                        updateDeadCard(i - 1);
                    }
                    break;
                case 5:
                    p5.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p5.setOnClickListener(selectPlayer);
                        if (alive.get(i - 1) == false) {
                            p5.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 6:
                    p6.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p6.setOnClickListener(selectPlayer);
                        if (alive.get(i - 1) == false) {
                            p6.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 7:
                    p7.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p7.setOnClickListener(selectPlayer);
                        if (alive.get(i - 1) == false) {
                            p7.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 8:
                    p8.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p8.setOnClickListener(selectPlayer);
                        if (alive.get(i - 1) == false) {
                            p8.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 9:
                    p9.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p9.setOnClickListener(selectPlayer);
                        if (alive.get(i - 1) == false) {
                            p9.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 10:
                    p10.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p10.setOnClickListener(selectPlayer);
                        if (alive.get(i - 1) == false) {
                            p10.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 11:
                    p11.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p11.setOnClickListener(selectPlayer);
                        if (alive.get(i - 1) == false) {
                            p11.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 12:
                    p12.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p12.setOnClickListener(selectPlayer);
                        if (alive.get(i - 1) == false) {
                            p12.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
            }
        }
    }

    private void makeCardVisible () {
        p1.setVisibility(View.VISIBLE);
        p2.setVisibility(View.VISIBLE);
        p3.setVisibility(View.VISIBLE);
        p4.setVisibility(View.VISIBLE);
        p5.setVisibility(View.VISIBLE);
        p6.setVisibility(View.VISIBLE);
        p7.setVisibility(View.VISIBLE);
        p8.setVisibility(View.VISIBLE);
        p9.setVisibility(View.VISIBLE);
        p10.setVisibility(View.VISIBLE);
        p11.setVisibility(View.VISIBLE);
        p12.setVisibility(View.VISIBLE);
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

    private void makeClickable() {
        p1.setClickable(true);
        p2.setClickable(true);
        p3.setClickable(true);
        p4.setClickable(true);
        p5.setClickable(true);
        p6.setClickable(true);
        p7.setClickable(true);
        p8.setClickable(true);
        p9.setClickable(true);
        p10.setClickable(true);
        p11.setClickable(true);
        p12.setClickable(true);
    }

    private void makeUnClickable() {
        for (int i = 1; i < 13; i++) {
            switch (i){
                case 1:
                    if (alive.get(i - 1) == false) {
                        p1.setClickable(false);
                        updateDeadCard(i - 1);
                    }
                    break;
                case 2:
                    if (alive.get(i - 1) == false) {
                        p2.setClickable(false);
                        updateDeadCard(i - 1);
                    }
                    break;
                case 3:
                    if (alive.get(i - 1) == false) {
                        p3.setClickable(false);
                        updateDeadCard(i - 1);
                    }
                    break;
                case 4:
                    if (alive.get(i - 1) == false) {
                        p4.setClickable(false);
                        updateDeadCard(i - 1);
                    }
                    break;
                case 5:
                    if (i <= numPlayers) {
                        if (alive.get(i - 1) == false) {
                            p5.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 6:
                    if (i <= numPlayers) {
                        if (alive.get(i - 1) == false) {
                            p6.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 7:
                    if (i <= numPlayers) {
                        if (alive.get(i - 1) == false) {
                            p7.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 8:
                    if (i <= numPlayers) {
                        if (alive.get(i - 1) == false) {
                            p8.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 9:
                    if (i <= numPlayers) {
                        if (alive.get(i - 1) == false) {
                            p9.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 10:
                    if (i <= numPlayers) {
                        if (alive.get(i - 1) == false) {
                            p10.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 11:
                    if (i <= numPlayers) {
                        if (alive.get(i - 1) == false) {
                            p11.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
                case 12:
                    if (i <= numPlayers) {
                        if (alive.get(i - 1) == false) {
                            p12.setClickable(false);
                            updateDeadCard(i - 1);
                        }
                    }
                    break;
            }
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

    private void setupGuardian() {
        currentCharacter = "Guardian";
        actionLabel.setText(R.string.guardTextLabel);
        confirmButton.setEnabled(true);
        confirmButton.setText(R.string.skipTextLabel);
        confirmButton.setOnClickListener(confirmGuard);
        mp = MediaPlayer.create(this, R.raw.audio_guardian_start);
        mp.setOnCompletionListener(prepareForNext);
        mp.start();
    }

    private void setupWolf() {
        currentCharacter = "Wolf";
        actionLabel.setText(killTextLabel);
        confirmButton.setText("杀害");
        confirmButton.setEnabled(false);
        confirmButton.setOnClickListener(confirmKill);
        makeClickable();
        mp = MediaPlayer.create(Night.this, R.raw.audio_wolf_start);
        mp.setOnCompletionListener(prepareForNext);
        mp.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showWolfMate();
            }
        }, 3000);
    }

    private void showWolfMate() {
        for (int i = 0; i < numPlayers; i++) {
            if (assignedCharacterList.get(i).equals("wolf")) {
                switch (i) {
                    case 0:
                        p1.setImageResource(R.drawable.wolf);
                        break;
                    case 1:
                        p2.setImageResource(R.drawable.wolf);
                        break;
                    case 2:
                        p3.setImageResource(R.drawable.wolf);
                        break;
                    case 3:
                        p4.setImageResource(R.drawable.wolf);
                        break;
                    case 4:
                        p5.setImageResource(R.drawable.wolf);
                        break;
                    case 5:
                        p6.setImageResource(R.drawable.wolf);
                        break;
                    case 6:
                        p7.setImageResource(R.drawable.wolf);
                        break;
                    case 7:
                        p8.setImageResource(R.drawable.wolf);
                        break;
                    case 8:
                        p9.setImageResource(R.drawable.wolf);
                        break;
                    case 9:
                        p10.setImageResource(R.drawable.wolf);
                        break;
                    case 10:
                        p11.setImageResource(R.drawable.wolf);
                        break;
                    case 11:
                        p12.setImageResource(R.drawable.wolf);
                        break;
                }
            }
        }
    }

    private void hideWolf() {
        for (int i = 0; i < numPlayers; i++) {
            switch (i) {
                case 0:
                    p1.setImageResource(R.drawable.card_back1);
                    break;
                case 1:
                    p2.setImageResource(R.drawable.card_back2);
                    break;
                case 2:
                    p3.setImageResource(R.drawable.card_back3);
                    break;
                case 3:
                    p4.setImageResource(R.drawable.card_back4);
                    break;
                case 4:
                    p5.setImageResource(R.drawable.card_back5);
                    break;
                case 5:
                    p6.setImageResource(R.drawable.card_back6);
                    break;
                case 6:
                    p7.setImageResource(R.drawable.card_back7);
                    break;
                case 7:
                    p8.setImageResource(R.drawable.card_back8);
                    break;
                case 8:
                    p9.setImageResource(R.drawable.card_back9);
                    break;
                case 9:
                    p10.setImageResource(R.drawable.card_back10);
                    break;
                case 10:
                    p11.setImageResource(R.drawable.card_back11);
                    break;
                case 11:
                    p12.setImageResource(R.drawable.card_back12);
                    break;
            }
        }
    }

    private void setupSeer() {
        currentCharacter = "Seer";
        confirmButton.setText("查验");
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setEnabled(false);
        confirmButton.setOnClickListener(confirmSeek);
        actionLabel.setText(R.string.seerTextLabel);
        mp = MediaPlayer.create(Night.this, R.raw.audio_seer_start);
        mp.setOnCompletionListener(prepareForNext);
        mp.start();
    }

    private void showCharacter(Boolean g, int i) {
        int resID = R.drawable.villager;
        if (!g) {
            resID = R.drawable.wolf;
        }
        switch (i-1) {
            case 0:
                p1.setImageResource(resID);
                break;
            case 1:
                p2.setImageResource(resID);
                break;
            case 2:
                p3.setImageResource(resID);
                break;
            case 3:
                p4.setImageResource(resID);
                break;
            case 4:
                p5.setImageResource(resID);
                break;
            case 5:
                p6.setImageResource(resID);
                break;
            case 6:
                p7.setImageResource(resID);
                break;
            case 7:
                p8.setImageResource(resID);
                break;
            case 8:
                p9.setImageResource(resID);
                break;
            case 9:
                p10.setImageResource(resID);
                break;
            case 10:
                p11.setImageResource(resID);
                break;
            case 11:
                p12.setImageResource(resID);
                break;
        }
    }

    private void setupWitcher() {
        currentCharacter = "Witcher";
        confirmButton.setEnabled(true);
        confirmButton.setText(R.string.skipTextLabel);
        confirmButton.setOnClickListener(witcherSkip);
        confirmButton.setText("跳过");
        witcherSaveButton.setVisibility(View.VISIBLE);
        witcherSaveButton.setOnClickListener(confirmSave);
        witcherKillButton.setVisibility(View.VISIBLE);
        witcherKillButton.setOnClickListener(confirmPoison);
        actionLabel.setText(R.string.actionTextLabel);
        mp = MediaPlayer.create(Night.this, R.raw.audio_witcher_start);
        mp.setOnCompletionListener(prepareForNext);
        mp.start();
        if (witcherHoldsAntidote) {
            showIntentKill(intentKillPlayerID);
        }
    }

    private void showIntentKill(int i){
        i -= 1;
        switch (i) {
            case 0:
                p1.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 1:
                p2.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 2:
                p3.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 3:
                p4.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 4:
                p5.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 5:
                p6.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 6:
                p7.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 7:
                p8.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 8:
                p9.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 9:
                p10.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 10:
                p11.setImageResource(R.drawable.card_back_intent_kill);
                break;
            case 11:
                p12.setImageResource(R.drawable.card_back_intent_kill);
                break;
        }
    }

    private void finishWitcher() {
        //Plays witcher finish audio
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(Night.this, R.raw.audio_witcher_finish);
                mp.setOnCompletionListener(prepareForNext);
                mp.start();
                hideWolf();
                confirmButton.setEnabled(false);
                witcherSaveButton.setVisibility(View.GONE);
                witcherKillButton.setVisibility(View.GONE);
                witcherSaveButton.setEnabled(false);
                witcherKillButton.setEnabled(false);
            }
        }, 2000);

        //Setup next
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finishNight();
            }
        }, 12000);
    }

    private void finishNight(){
        mp.release();
        Toast.makeText(Night.this, "天亮了...", Toast.LENGTH_SHORT).show();
        Intent day = new Intent(Night.this, Day.class);
        day.putExtra("id", playerID);
        day.putExtra("characters", assignedCharacterList);
        day.putExtra("alive",alive);
        day.putExtra("numPlayers", numPlayers);
        day.putExtra("guardedPlayerID", guardedPlayerID);
        day.putExtra("intentKillPlayerID", intentKillPlayerID);
        day.putExtra("antidotePlayerID", antidotePlayerID);
        day.putExtra("poisonPlayerID", poisonPlayerID);
        day.putExtra("witcherHoldsAntidote", witcherHoldsAntidote);
        day.putExtra("witcherHoldsPoison", witcherHoldsPoison);
        day.putExtra("wolf", wolf);
        day.putExtra("villagers", villagers);
        day.putExtra("seer", seer);
        day.putExtra("witcher", witcher);
        day.putExtra("guardian", guardian);
        day.putExtra("idiot", idiot);
        startActivity(day);
    }

    private Integer judgeGame(Boolean all) {
        //0 stands for undecided victory
        //2 stands for werewolf victory
        if (assignedCharacterList.contains("witcher") && witcherHoldsAntidote.equals(true)) {return 0;}
        ArrayList<Boolean> intentAlive = new ArrayList<Boolean>(alive);
        intentAlive.set(intentKillPlayerID - 1, false);
        Integer totalGod = 0, totalVillager = 0, totalWolf = 0;
        Integer aliveGod = 0, aliveVillager = 0, aliveWolf = 0;
        for (int i = 0; i < numPlayers; i++) {
            switch (assignedCharacterList.get(i)) {
                case "wolf":
                    totalWolf += 1;
                    if (intentAlive.get(i).equals(true)) {aliveWolf += 1;}
                    break;
                case "villager":
                    totalVillager += 1;
                    if (intentAlive.get(i).equals(true)) {aliveVillager += 1;}
                    break;
                default:
                    totalGod += 1;
                    if (intentAlive.get(i).equals(true)) {aliveGod += 1;}
                    break;
            }
        }
        if (all) {
            if (aliveGod + aliveVillager == 0) {return 2;}
        } else {
            if (aliveVillager == 0) {return 2;}
            if (aliveGod == 0) {return 2;}
        }
        //Undecided
        return 0;
    }
}