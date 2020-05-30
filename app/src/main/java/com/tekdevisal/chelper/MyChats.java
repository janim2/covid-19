package com.tekdevisal.chelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdevisal.chelper.Adapters.ChatsAdapter;
import com.tekdevisal.chelper.Adapters.DoctorsAdapter;
import com.tekdevisal.chelper.Helpers.Accessories;
import com.tekdevisal.chelper.Models.Chats;
import com.tekdevisal.chelper.Models.Doctors;
import com.tekdevisal.chelper.Models.Report_Model;

import java.util.ArrayList;

public class MyChats extends AppCompatActivity {

    private ArrayList chatsArray = new ArrayList<Chats>();
    private RecyclerView chats_RecyclerView;
    private RecyclerView.Adapter chats_Adapter;

    private TextView no_chats;
    private Snackbar snackbar;
    private FirebaseAuth myauth;

    private String person_i_chatted_name, senders_name, user_type, receipent_name, chat_ID;
    private Accessories mychats_accessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats);

        getSupportActionBar().setTitle("Covaid | Chats");
        mychats_accessor = new Accessories(this);
        myauth = FirebaseAuth.getInstance();
        chats_RecyclerView = findViewById(R.id.chats_recyclerView);
        no_chats = findViewById(R.id.no_chats);

        if(isNetworkAvailable()){
            FetchReceipentID();
            chats_RecyclerView.setHasFixedSize(true);
            chats_Adapter = new ChatsAdapter(getFromDatabase(),MyChats.this);
            chats_RecyclerView.setAdapter(chats_Adapter);
        }else{
            snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "No internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    private void FetchReceipentID() {
        DatabaseReference fetch_chat_ids = FirebaseDatabase.getInstance()
                .getReference("chat").child(myauth.getUid());
        fetch_chat_ids.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Fetch_chatsID(child.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Fetch_chatsID(String receipentID) {
        chatsArray.clear();
        DatabaseReference fetch_chat_ids = FirebaseDatabase.getInstance()
                .getReference("chat").child(myauth.getUid())
                .child(receipentID);
        fetch_chat_ids.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        chat_ID = child.getKey();
                    }
                    Fetch_Name(receipentID, chat_ID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Fetch_Name(String receipentID, String chat_id) {
        user_type = mychats_accessor.getString("user_type");
        DatabaseReference getname;
        if(user_type.equals("doctor")){
            getname = FirebaseDatabase.getInstance()
                    .getReference("users").child(receipentID);
            getname.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("name")){
                                receipent_name = child.getValue().toString();
                                Chats obj = new Chats(receipentID, chat_id, receipent_name);
                                chatsArray.add(obj);
                                chats_RecyclerView.setAdapter(chats_Adapter);
                                chats_Adapter.notifyDataSetChanged();
                                no_chats.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            getname = FirebaseDatabase.getInstance()
                    .getReference("doctors").child(receipentID);
            getname.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("name")){
                                receipent_name = child.getValue().toString();
                                Chats obj = new Chats(receipentID, chat_id, receipent_name);
                                chatsArray.add(obj);
                                chats_RecyclerView.setAdapter(chats_Adapter);
                                chats_Adapter.notifyDataSetChanged();
                                no_chats.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public ArrayList<Chats> getFromDatabase(){
        return  chatsArray;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
