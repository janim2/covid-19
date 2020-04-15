package com.tekdevisal.covid;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dialer extends AppCompatActivity {

    private String doc_id, doc_name, which_action;
    private Intent dialer_intent;
    private TextView name_calling;
    private ImageView endcall, accept_call;
    private FirebaseAuth myauth;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);

//        getSupportActionBar().setTitle("COVID 19 | Dialer");
        dialer_intent = getIntent();
        myauth = FirebaseAuth.getInstance();

        doc_id = dialer_intent.getStringExtra("doc_id");
        doc_name = dialer_intent.getStringExtra("doc_name");
        which_action = dialer_intent.getStringExtra("action");

        name_calling = findViewById(R.id.name_calling);
        accept_call = findViewById(R.id.make_call);
        endcall = findViewById(R.id.cancel_call);

        name_calling.setText(doc_name);
        findViewById(R.id.cancel_call).setOnClickListener(v -> {
            if(which_action.equals("iamcalling")){
                if(isNetworkAvailable()){
                    FirebaseDatabase.getInstance().getReference("calls")
                            .orderByChild("ringing")
                            .equalTo(myauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
//                            String value = snap.getValue(String.class);
                                String key = snap.getKey();
                                FirebaseDatabase.getInstance().getReference("calls").child(key)
                                        .removeValue().addOnCompleteListener(task -> {
                                    if(task.isComplete()){
                                        finish();
                                    }
                                });
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

            }else{
                if(isNetworkAvailable()){
                    FirebaseDatabase.getInstance().getReference("calls").child(myauth.getCurrentUser().getUid())
                            .removeValue().addOnCompleteListener(task -> finish());
                }else{
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            }
        });

        if(which_action.equals("iamcalling")){

        }else{
            accept_call.setVisibility(View.VISIBLE);
        }

        accept_call.setOnClickListener(v -> {

        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
