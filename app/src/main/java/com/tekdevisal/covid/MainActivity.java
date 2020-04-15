package com.tekdevisal.covid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joooonho.SelectableRoundedImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mauth;
    private String caller_id, caller_name, has_open_dialer="";
    private Snackbar snackbar;

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

        findViewById(R.id.logout).setOnClickListener(v -> {
            final AlertDialog.Builder logout = new AlertDialog.Builder(MainActivity.this, R.style.Myalert);
            logout.setTitle("Signing Out?");
            logout.setMessage("Leaving us? Please reconsider.");
            logout.setNegativeButton("Sign out", (dialog, which) -> {
                if(isNetworkAvailable()){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,Login_.class));
                }else{
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();                }
            });

            logout.setPositiveButton("Stay", (dialog, which) -> dialog.cancel());
            logout.show();
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
            new checkfor_incoming_calls().execute();
        }else{
            startActivity(new Intent(MainActivity.this, Login_.class));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class checkfor_incoming_calls extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            final Handler thehandler;

            thehandler = new Handler(Looper.getMainLooper());
            final int delay = 15000;

            thehandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isNetworkAvailable()){
                        Findincoming_calls();
                    }else{
//                        Toast.makeText(Admin_MainActivity.this,"checking", Toast.LENGTH_LONG).show();
                    }
                    thehandler.postDelayed(this,delay);
                }
            },delay);
            return null;
        }
    }

    private void Findincoming_calls() {
        DatabaseReference incoming = FirebaseDatabase.getInstance().getReference("calls")
                .child(mauth.getCurrentUser().getUid());
        incoming.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    caller_id = dataSnapshot.child("ringing").getValue().toString();
                    DatabaseReference getname = FirebaseDatabase.getInstance().getReference("users").child(caller_id);
                    getname.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                caller_name = dataSnapshot.child("name").getValue().toString();

                                if(has_open_dialer.equals("")){
                                    Intent gotodialer = new Intent(MainActivity.this, Dialer.class);
                                    gotodialer.putExtra("doc_id", caller_id);
                                    gotodialer.putExtra("doc_name", caller_name);
                                    gotodialer.putExtra("action", "accepting_call");
                                    startActivity(gotodialer);
                                    has_open_dialer = "opened";
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
