package com.example.werewolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button start;
    private Button setting;

    private int wolf = 1;
    private int villagers = 2;
    private int seer = 1;
    private int witcher = 0;
    private int guardian = 0;
    private int idiot = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button)findViewById(R.id.start_button);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        setting = (Button)findViewById(R.id.settings_button);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSettings();
            }
        });
        System.out.print(wolf);
        System.out.println("个狼人");
        System.out.print(villagers);
        System.out.println("个平民");
        System.out.print(seer);
        System.out.println("个预言家");
        System.out.print(witcher);
        System.out.println("个女巫");
        System.out.print(guardian);
        System.out.println("个守卫");
        System.out.print(idiot);
        System.out.println("个白痴");
    }

    private void startGame() {
        System.out.println("游戏开始！");
        System.out.print(wolf);
        System.out.println("个狼人");
        System.out.print(villagers);
        System.out.println("个平民");
        System.out.print(seer);
        System.out.println("个预言家");
        System.out.print(witcher);
        System.out.println("个女巫");
        System.out.print(guardian);
        System.out.println("个守卫");
        System.out.print(idiot);
        System.out.println("个白痴");
    }

    public void changeSettings() {
        Intent changeSettings = new Intent(MainActivity.this, Setting.class);
        changeSettings.putExtra("wolf", wolf);
        changeSettings.putExtra("villagers", villagers);
        changeSettings.putExtra("seer", seer);
        changeSettings.putExtra("witcher", witcher);
        changeSettings.putExtra("guardian", guardian);
        changeSettings.putExtra("idiot", idiot);
        startActivity(changeSettings);
    }
}