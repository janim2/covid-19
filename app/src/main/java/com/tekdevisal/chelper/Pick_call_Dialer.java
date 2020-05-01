package com.tekdevisal.chelper;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdevisal.chelper.Helpers.Accessories;

public class Pick_call_Dialer extends AppCompatActivity {

    private String doc_id, doc_name, which_action, has_opened_video;
    private Intent dialer_intent;
    private TextView name_calling;
    private ImageView endcall, accept_call;
    private FirebaseAuth myauth;
    private Snackbar snackbar;
    private MediaPlayer player;
    private Accessories dialer_accessor;
    private MainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_call__dialer);

        dialer_intent = getIntent();
        myauth = FirebaseAuth.getInstance();
        activity = new MainActivity();
        dialer_accessor = new Accessories(Pick_call_Dialer.this);

        registerReceiver(close_me, new IntentFilter("to_close"));
        //starting the ring tone
        player = MediaPlayer.create(this,
                Settings.System.DEFAULT_RINGTONE_URI);
        player.start();
//        ringtone ends here

        doc_id = dialer_intent.getStringExtra("doc_id");
        doc_name = dialer_intent.getStringExtra("doc_name");
        which_action = dialer_intent.getStringExtra("action");

        name_calling = findViewById(R.id.name_calling);
        accept_call = findViewById(R.id.make_call);
        endcall = findViewById(R.id.cancel_call);

        name_calling.setText(doc_name);
        endcall.setOnClickListener(v -> {
                if(isNetworkAvailable()){
                    player.stop();
                    DatabaseReference call_reference = FirebaseDatabase.getInstance().getReference("calls").child(myauth.getCurrentUser().getUid());
                    call_reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                if(snap.exists()){
                                    call_reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                startActivity(new Intent(getApplicationContext(), Live_Chat.class));
                                                finish();
                                            }
                                        }
                                    });

                                }else{
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }else{
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
        });

        accept_call.setOnClickListener(v -> {
            player.stop();
                if(isNetworkAvailable()){
                    Toast.makeText(Pick_call_Dialer.this, "Testing", Toast.LENGTH_LONG).show();
                    DatabaseReference pick_call = FirebaseDatabase.getInstance().getReference("calls")
                            .child(myauth.getCurrentUser().getUid());

                    pick_call.child("picked").setValue("picked").addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Intent gotovideo = new Intent(getApplicationContext(), VideoChat_Activity.class);
                            gotovideo.putExtra("action","ipicked");
                            gotovideo.putExtra("users_name",doc_name);
                            gotovideo.putExtra("users_id",doc_id);
                            new Accessories(getApplicationContext()).put("action", "ipicked");
                            startActivity(gotovideo);
                            finish();

                        }
                    });
                }else{
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("StaticFieldLeak")
    private class iscall_picked extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            final Handler thehandler;

            thehandler = new Handler(Looper.getMainLooper());
            final int delay = 15000;

            thehandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isNetworkAvailable()){
                        hasCallBeenPicked();
                    }else{
//                        Toast.makeText(Admin_MainActivity.this,"checking", Toast.LENGTH_LONG).show();
                    }
                    thehandler.postDelayed(this,delay);
                }
            },delay);
            return null;
        }
    }

    private void hasCallBeenPicked() {
        try{
            if(which_action.equals("iamcalling")){
                if(isNetworkAvailable()){
                    FirebaseDatabase.getInstance().getReference("calls")
                            .orderByChild("ringing")
                            .equalTo(myauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
//                            String value = snap.getValue(String.class);
                                String key = snap.getKey();
                                FirebaseDatabase.getInstance().getReference("calls")
                                        .child(key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("picked")){
                                            if(has_opened_video.equals("")){
                                                Intent gotoVideo = new Intent(getApplicationContext(), VideoActivity.class);
                                                gotoVideo.putExtra("action", "icalled");
                                                startActivity(gotoVideo);
                                                finish();
                                                has_opened_video = "open";
                                            }
                                        }else{
//                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
//                                            try{
//                                                FirebaseDatabase.getInstance().getReference("calls").child(myauth.getCurrentUser().getUid())
//                                                        .removeValue().addOnCompleteListener(task -> {
//                                                    if(task.isSuccessful()){
//                                                        startActivity(new Intent(getApplicationContext(), Live_Chat.class));
//                                                        finish();
//                                                    }
//                                                });
//                                            }catch (NullPointerException e){
//                                                e.printStackTrace();
//                                            }


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
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        new iscall_picked().execute();
    }

    private final BroadcastReceiver close_me = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            player.stop();
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(close_me);
        super.onDestroy();
    }
}
