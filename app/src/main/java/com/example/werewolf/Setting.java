package com.example.werewolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Setting extends AppCompatActivity {
    private Button start;

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

    private TextView villagerNumber;
    private TextView wolfNumber;
    private TextView seerNumber;
    private TextView witcherNumber;
    private TextView guardianNumber;
    private TextView idiotNumber;

    private int wolf;
    private int villagers;
    private int seer;
    private int witcher;
    private int guardian;
    private int idiot;

    public Setting() {
    }

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
        }

        start = (Button)findViewById(R.id.startGameButton);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        villagerNumber = (TextView)findViewById(R.id.villagerNumber);
        villagerNumber.setText(Integer.toString(villagers));
        wolfNumber = (TextView)findViewById(R.id.wolfNumber);
        wolfNumber.setText(Integer.toString(wolf));
        seerNumber = (TextView)findViewById(R.id.seerNumber);
        seerNumber.setText(Integer.toString(seer));
        witcherNumber = (TextView)findViewById(R.id.witcherNumber);
        witcherNumber.setText(Integer.toString(witcher));
        guardianNumber = (TextView)findViewById(R.id.guardianNumber);
        guardianNumber.setText(Integer.toString(guardian));
        idiotNumber = (TextView)findViewById(R.id.idiotNumber);
        idiotNumber.setText(Integer.toString(idiot));

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
                seer += 1;
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
                witcher += 1;
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
                guardian += 1;
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
                idiot += 1;
                idiotNumber.setText(Integer.toString(idiot));
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
        startActivity(assignCharacter);
    }
}