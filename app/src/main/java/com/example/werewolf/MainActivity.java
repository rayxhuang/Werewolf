package com.example.werewolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button start;
    private Button setting;

    private int wolf = 2;
    private int villagers = 2;
    private int seer = 1;
    private int witcher = 0;
    private int guardian = 1;
    private int idiot = 0;
    private int hunter = 0;
    private Boolean mode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start_button);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        setting = findViewById(R.id.settings_button);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSettings();
            }
        });
    }

    private void startGame() {
        Intent assignCharacter = new Intent(MainActivity.this, SetPlayerCharacter.class);
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

    public void changeSettings() {
        Intent changeSettings = new Intent(MainActivity.this, Setting.class);
        changeSettings.putExtra("wolf", wolf);
        changeSettings.putExtra("villagers", villagers);
        changeSettings.putExtra("seer", seer);
        changeSettings.putExtra("witcher", witcher);
        changeSettings.putExtra("guardian", guardian);
        changeSettings.putExtra("idiot", idiot);
        changeSettings.putExtra("hunter", hunter);
        changeSettings.putExtra("mode", mode);
        startActivity(changeSettings);
    }
}