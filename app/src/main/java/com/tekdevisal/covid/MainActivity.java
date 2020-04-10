package com.tekdevisal.covid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.joooonho.SelectableRoundedImageView;
import com.sdsmdg.harjot.rotatingtext.RotatingTextSwitcher;
import com.sdsmdg.harjot.rotatingtext.RotatingTextWrapper;
import com.sdsmdg.harjot.rotatingtext.models.Rotatable;


public class MainActivity extends AppCompatActivity {

    private RotatingTextWrapper rotatingTextSwitcher;
    private SelectableRoundedImageView complain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        getSupportActionBar().setTitle("COVID 19 | HOME");

        rotatingTextSwitcher = findViewById(R.id.custom_switcher);
        rotatingTextSwitcher.setSize(19);

        Rotatable rotatable = new Rotatable(Color.parseColor("#f8615a"), 5000, "SAFE", "INDOORS", "INFORMED");
        rotatable.setSize(21);
        rotatable.setAnimationDuration(500);

        rotatingTextSwitcher.setContent("BE ?", rotatable);

        findViewById(R.id.info).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, About_.class));
        });

        findViewById(R.id.complain).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Report_.class));
        });

        findViewById(R.id.chat).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Live_Chat.class));
        });

        findViewById(R.id.statistic).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Statistics.class));
        });

        findViewById(R.id.about_app).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, App_description.class));
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
