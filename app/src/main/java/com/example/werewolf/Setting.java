package com.example.werewolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends AppCompatActivity {
    private Button start;
    private Switch switchButton;

    private View villagerMinus;
    private View villagerPlus;
    private View wolfMinus;
    private View wolfPlus;
    private View seerMinus;
    private View seerPlus;
    private View witcherMinus;
    private View witcherPlus;
    private View guardianMinus;
    private View guardianPlus;
    private View idiotMinus;
    private View idiotPlus;
    private View hunterMinus;
    private View hunterPlus;

    private TextView villagerNumber;
    private TextView wolfNumber;
    private TextView seerNumber;
    private TextView witcherNumber;
    private TextView guardianNumber;
    private TextView idiotNumber;
    private TextView hunterNumber;

    private int wolf;
    private int villagers;
    private int seer;
    private int witcher;
    private int guardian;
    private int idiot;
    private int hunter;
    private Boolean mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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
        }

        start = findViewById(R.id.startGameButton);

        start.setOnClickListener(view -> startGame());

        switchButton = findViewById(R.id.switchMode);
        setSwitch();

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = !mode;
                setSwitch();
            }
        });

        villagerNumber = findViewById(R.id.villagerNumber);
        villagerNumber.setText(Integer.toString(villagers));
        wolfNumber = findViewById(R.id.wolfNumber);
        wolfNumber.setText(Integer.toString(wolf));
        seerNumber = findViewById(R.id.seerNumber);
        seerNumber.setText(Integer.toString(seer));
        witcherNumber = findViewById(R.id.witcherNumber);
        witcherNumber.setText(Integer.toString(witcher));
        guardianNumber = findViewById(R.id.guardianNumber);
        guardianNumber.setText(Integer.toString(guardian));
        idiotNumber = findViewById(R.id.idiotNumber);
        idiotNumber.setText(Integer.toString(idiot));
        hunterNumber = findViewById(R.id.hunterNumber);
        hunterNumber.setText(Integer.toString(hunter));
//        wolfKingNumber = findViewById(R.id.wolfKingNumber);
//        wolfKingNumber.setText(Integer.toString(wolfKing));

        villagerMinus = findViewById(R.id.villagerNumberMinus);
        villagerMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (villagers > 1) {
                    villagers -= 1;
                }
                villagerNumber.setText(Integer.toString(villagers));
            }
        });

        villagerPlus = findViewById(R.id.villagerNumberPlus);
        villagerPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                villagers += 1;
                villagerNumber.setText(Integer.toString(villagers));
            }
        });

        wolfMinus = findViewById(R.id.wolfNumberMinus);
        wolfMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wolf > 1) {
                    wolf -= 1;
                }
                wolfNumber.setText(Integer.toString(wolf));
            }
        });

        wolfPlus = findViewById(R.id.wolfNumberPlus);
        wolfPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wolf += 1;
                wolfNumber.setText(Integer.toString(wolf));
            }
        });

        seerMinus = findViewById(R.id.seerNumberMinus);
        seerMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seer > 0) {
                    seer -= 1;
                }
                seerNumber.setText(Integer.toString(seer));
            }
        });

        seerPlus = findViewById(R.id.seerNumberPlus);
        seerPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seer < 1) {
                    seer += 1;
                }
                seerNumber.setText(Integer.toString(seer));
            }
        });

        witcherMinus = findViewById(R.id.witcherNumberMinus);
        witcherMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (witcher > 0) {
                    witcher -= 1;
                }
                witcherNumber.setText(Integer.toString(witcher));
            }
        });

        witcherPlus = findViewById(R.id.witcherNumberPlus);
        witcherPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (witcher < 1) {
                    witcher += 1;
                }
                witcherNumber.setText(Integer.toString(witcher));
            }
        });

        guardianMinus = findViewById(R.id.guardianNumberMinus);
        guardianMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (guardian > 0) {
                    guardian -= 1;
                }
                guardianNumber.setText(Integer.toString(guardian));
            }
        });

        guardianPlus = findViewById(R.id.guardianNumberPlus);
        guardianPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (guardian < 1) {
                    guardian += 1;
                }
                guardianNumber.setText(Integer.toString(guardian));
            }
        });

        idiotMinus = findViewById(R.id.idiotNumberMinus);
        idiotMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idiot > 0) {
                    idiot -= 1;
                }
                idiotNumber.setText(Integer.toString(idiot));
            }
        });

        idiotPlus = findViewById(R.id.idiotNumberPlus);
        idiotPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idiot < 1) {
                    idiot += 1;
                }
                idiotNumber.setText(Integer.toString(idiot));
            }
        });

        hunterMinus = findViewById(R.id.hunterNumberMinus);
        hunterMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hunter > 0) {
                    hunter -= 1;
                }
                hunterNumber.setText(Integer.toString(hunter));
            }
        });

        hunterPlus = findViewById(R.id.hunterNumberPlus);
        hunterPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hunter < 1) {
                    hunter += 1;
                }
                hunterNumber.setText(Integer.toString(hunter));
            }
        });
    }

    private void startGame() {
        Intent assignCharacter = new Intent(Setting.this, SetPlayerCharacter.class);
        assignCharacter.putExtra("wolf", wolf);
        assignCharacter.putExtra("villagers", villagers);
        assignCharacter.putExtra("seer", seer);
        assignCharacter.putExtra("witcher", witcher);
        assignCharacter.putExtra("guardian", guardian);
        assignCharacter.putExtra("idiot", idiot);
        assignCharacter.putExtra("hunter", hunter);
        assignCharacter.putExtra("mode", mode);
        startActivity(assignCharacter);
    }

    private void setSwitch(){
        if (mode) {
            switchButton.setChecked(true);
            switchButton.setText(R.string.switch2);
        } else {
            switchButton.setChecked(false);
            switchButton.setText(R.string.switch1);
        }
    }
}