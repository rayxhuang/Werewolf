package com.example.werewolf;

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

public class Wolf extends AppCompatActivity {
    private ArrayList<Integer> playerID;
    private ArrayList<String> assignedCharacterList;
    private Integer numPlayers = 0;
    private ArrayList<Boolean> alive;
    private String currentCharacter = "None";
    private Integer selectedPlayerID = 0;
    private Integer currentSelectedPlayerID = 0;
    private Integer guardedPlayerID = 0;
    private Integer intentKillPlayerID = 0;
    private Boolean witcherHoldsAntidote = true;
    private Boolean witcherHoldsPoison = true;
    private Integer antidotePlayerID = 0;
    private Integer poisonPlayerID = 0;
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
                Toast.makeText(Wolf.this, "你取消了选择", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Wolf.this, "你选择了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
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
                    confirmButton.setText("守护");
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
            confirmButton.setEnabled(false);
            if (selectedPlayerID == 0){
                Toast.makeText(Wolf.this, "你没有守护任何玩家", Toast.LENGTH_SHORT).show();
            } else {
                guardedPlayerID = selectedPlayerID;
                Toast.makeText(Wolf.this, "你守护了" + guardedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
            }
            currentSelectedPlayerID = 0;
            selectedPlayerID = 0;
            if (mp.isPlaying()) {
                Toast.makeText(Wolf.this, "Audio is playing, I will stop it", Toast.LENGTH_SHORT).show();
                mp.stop();
                mp.reset();
            }
            //Plays guardian finish audio
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mp = MediaPlayer.create(Wolf.this, R.raw.audio_guard_finish);
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
            Toast.makeText(Wolf.this, "你杀害了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
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
                    mp = MediaPlayer.create(Wolf.this, R.raw.audio_wolf_finish);
                    mp.setOnCompletionListener(prepareForNext);
                    mp.start();
                    //Hide wolf card
                    hideWolf();
                }
            }, 3000);

            //Setup seer
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setupSeer();
                }
            }, 12000);
        }
    };

    private View.OnClickListener confirmSave = new View.OnClickListener() {
        public void onClick(View view) {
            Toast.makeText(Wolf.this, "你解救了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Wolf.this, "你没有进行操作", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Wolf.this, "你毒死了" + selectedPlayerID + "号玩家", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Wolf.this, selectedPlayerID + "号玩家是" + characterLabel, Toast.LENGTH_SHORT).show();
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
                    mp = MediaPlayer.create(Wolf.this, R.raw.audio_seer_finish);
                    mp.setOnCompletionListener(prepareForNext);
                    mp.start();
                }
            }, 3000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (assignedCharacterList.contains("witcher")){
                        setupWitcher();
                    }
                    //
                }
            }, 12000);
        }
    };

    private MediaPlayer.OnCompletionListener prepareForNext = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(Wolf.this, "Audi playback finished, this is auto stop", Toast.LENGTH_SHORT).show();
            mp.stop();
            mp.reset();
        }
    };

    private MediaPlayer.OnCompletionListener complete = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(Wolf.this, "Audi playback finished", Toast.LENGTH_SHORT).show();
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wolf);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            playerID = (ArrayList<Integer>) getIntent().getSerializableExtra("id");
            assignedCharacterList = (ArrayList<String>) getIntent().getSerializableExtra("characters");
            numPlayers = (Integer) extras.get("numPlayer");
            alive = (ArrayList<Boolean>) getIntent().getSerializableExtra("alive");
        }
        init();

        //Night starts...
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(Wolf.this, R.raw.audio_night_start);
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
                    break;
                case 2:
                    p2.setVisibility(View.GONE);
                    p2.setOnClickListener(selectPlayer);
                    break;
                case 3:
                    p3.setVisibility(View.GONE);
                    p3.setOnClickListener(selectPlayer);
                    break;
                case 4:
                    p4.setVisibility(View.GONE);
                    p4.setOnClickListener(selectPlayer);
                    break;
                case 5:
                    p5.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p5.setOnClickListener(selectPlayer);
                    }
                    break;
                case 6:
                    p6.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p6.setOnClickListener(selectPlayer);
                    }
                    break;
                case 7:
                    p7.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p7.setOnClickListener(selectPlayer);
                    }
                    break;
                case 8:
                    p8.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p8.setOnClickListener(selectPlayer);
                    }
                    break;
                case 9:
                    p9.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p9.setOnClickListener(selectPlayer);
                    }
                    break;
                case 10:
                    p10.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p10.setOnClickListener(selectPlayer);
                    }
                    break;
                case 11:
                    p11.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p11.setOnClickListener(selectPlayer);
                    }
                    break;
                case 12:
                    p12.setVisibility(View.GONE);
                    if (i <= numPlayers) {
                        p12.setOnClickListener(selectPlayer);
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
        mp = MediaPlayer.create(Wolf.this, R.raw.audio_wolf_start);
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
        mp = MediaPlayer.create(Wolf.this, R.raw.audio_seer_start);
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
        mp = MediaPlayer.create(Wolf.this, R.raw.audio_witcher_start);
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
                mp = MediaPlayer.create(Wolf.this, R.raw.audio_witcher_finish);
                mp.setOnCompletionListener(prepareForNext);
                mp.start();
                hideWolf();
                confirmButton.setVisibility(View.VISIBLE);
                confirmButton.setEnabled(false);
                witcherSaveButton.setVisibility(View.GONE);
                witcherKillButton.setVisibility(View.GONE);
                witcherSaveButton.setEnabled(false);
                witcherKillButton.setEnabled(false);
            }
        }, 2000);

        //Setup next
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 12000);
    }
}