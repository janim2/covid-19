package com.tekdevisal.covid;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdevisal.covid.Adapters.DoctorsAdapter;
import com.tekdevisal.covid.Models.Doctors;

import java.util.ArrayList;

public class Live_Chat extends AppCompatActivity {

    private ArrayList doctorsArray = new ArrayList<Doctors>();
    private RecyclerView doctors_RecyclerView;
    private RecyclerView.Adapter doctors_Adapter;
    private String doc_name;
    private TextView no_docs;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live__chat);

        getSupportActionBar().setTitle("COVID 19 | SELECT");

        doctors_RecyclerView = findViewById(R.id.doctors_recyclerView);
        no_docs = findViewById(R.id.no_docs);

        if(isNetworkAvailable()){
            fetchDoctorsIDs();

            doctors_RecyclerView.setHasFixedSize(true);
            doctors_Adapter = new DoctorsAdapter(getFromDatabase(),Live_Chat.this);
            doctors_RecyclerView.setAdapter(doctors_Adapter);
        }else{
            snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "No internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        findViewById(R.id.phone_call).setOnClickListener(v -> {
            openDialer(v,"112");
        });
    }

    private void fetchDoctorsIDs() {
        DatabaseReference fetchdocsids = FirebaseDatabase.getInstance().getReference("available");
        fetchdocsids.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        fetchDoctors(child.getKey());
                    }
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fetchDoctors(String key) {
//        Toast.makeText(Live_Chat.this, "key"+key, Toast.LENGTH_LONG).show();
        no_docs.setVisibility(View.GONE);
        DatabaseReference fetch_docs =
                FirebaseDatabase.getInstance().getReference("doctors").child(key);
        fetch_docs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("name")){
                            doc_name = child.getValue().toString();

                        } else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                        }
                    }
                    Doctors obj = new Doctors(key,doc_name);
                    doctorsArray.add(obj);
                    doctors_RecyclerView.setAdapter(doctors_Adapter);
                    doctors_Adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Live_Chat.this,"Cancelled",Toast.LENGTH_LONG).show();

            }
        });

    }

    public ArrayList<Doctors> getFromDatabase(){
        return  doctorsArray;
    }

    private void openDialer(View v, String call_number){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + call_number));
        v.getContext().startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
