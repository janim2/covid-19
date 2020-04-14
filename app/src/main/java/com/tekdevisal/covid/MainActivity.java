package com.tekdevisal.covid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.joooonho.SelectableRoundedImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        mauth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("COVID 19 | HOME");

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

    @Override
    protected void onStart() {
        super.onStart();
        if(mauth.getCurrentUser() != null){

        }else{
            startActivity(new Intent(MainActivity.this, Login_.class));
        }
    }

}
