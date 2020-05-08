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

    private String doc_id, doc_name, which_action, has_opened_video, the_token="";
    private Intent dialer_intent;
    private TextView name_calling;
    private ImageView endcall, accept_call;
    private FirebaseAuth myauth;
    private Snackbar snackbar;
    private MediaPlayer player;
    private Accessories dialer_accessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_call__dialer);

        dialer_intent = getIntent();
        myauth = FirebaseAuth.getInstance();
        dialer_accessor = new Accessories(Pick_call_Dialer.this);

        registerReceiver(close_me, new IntentFilter("to_close"));
        //starting the ring tone
        player = MediaPlayer.create(this,
                Settings.System.DEFAULT_RINGTONE_URI);
        player.start();
//        ringtone ends here

        doc_id = dialer_accessor.getString("doc_id");
        doc_name = dialer_accessor.getString("doc_name");
        the_token = dialer_accessor.getString("token");
        which_action = dialer_accessor.getString("action");

        name_calling = findViewById(R.id.name_calling);
        accept_call = findViewById(R.id.make_call);
        endcall = findViewById(R.id.cancel_call);

//        Toast.makeText(Pick_call_Dialer.this, "token"+the_token, Toast.LENGTH_LONG).show();

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
                                    call_reference.removeValue().addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            startActivity(new Intent(getApplicationContext(), Live_Chat.class));
                                            finish();
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
//                    Toast.makeText(Pick_call_Dialer.this, "Testing", Toast.LENGTH_LONG).show();
                    DatabaseReference pick_call = FirebaseDatabase.getInstance().getReference("calls")
                            .child(myauth.getCurrentUser().getUid());

                    pick_call.child("picked").setValue("picked").addOnCompleteListener(task -> {
                        if(task.isSuccessful()){

                            Intent gotovideo = new Intent(getApplicationContext(), VideoChat_Activity.class);
                            gotovideo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            dialer_accessor.put("action","ipicked");
                            dialer_accessor.put("users_name",doc_name);
                            dialer_accessor.put("users_id",doc_id);
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

    @Override
    protected void onStart() {
        super.onStart();
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
