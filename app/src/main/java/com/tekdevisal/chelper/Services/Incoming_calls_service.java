package com.tekdevisal.chelper.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdevisal.chelper.Dialer;
import com.tekdevisal.chelper.MainActivity;
import com.tekdevisal.chelper.Pick_call_Dialer;

import java.util.Objects;

@SuppressLint("Registered")
public class Incoming_calls_service extends Service {

    FirebaseAuth myauth = FirebaseAuth.getInstance();
    String caller_id = "", caller_name;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DatabaseReference my_calls = FirebaseDatabase.getInstance().getReference("calls")
                .child(Objects.requireNonNull(myauth.getCurrentUser()).getUid());
        my_calls.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("ringing")) {
                        caller_id = dataSnapshot.child("ringing").getValue().toString();
                        DatabaseReference getname = FirebaseDatabase.getInstance().getReference("users")
                                .child(caller_id);
                        getname.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    caller_name = dataSnapshot.child("name").getValue().toString();
                                    Intent gotodialer = new Intent(getApplicationContext(), Pick_call_Dialer.class);
                                    gotodialer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    gotodialer.putExtra("doc_id", caller_id);
                                    gotodialer.putExtra("doc_name", caller_name);
                                    gotodialer.putExtra("action", "accepting_call");
                                    startActivity(gotodialer);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }else{
                    sendBroadcast(new Intent("to_close"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
